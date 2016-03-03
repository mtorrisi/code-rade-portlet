/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.dfa.unict;

import it.dfa.unict.exception.CodeRadePortletException;
import it.infn.ct.GridEngine.Job.InfrastructureInfo;
import it.infn.ct.GridEngine.Job.MultiInfrastructureJobSubmission;
import it.infn.ct.GridEngine.JobResubmission.GEJobDescription;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ProcessAction;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.util.bridges.mvc.MVCPortlet;
import com.liferay.util.portlet.PortletProps;

/**
 *
 * @author mario
 */
public class CodeRadePortlet extends MVCPortlet {

	private AppPreferences appPreferences;
	private List<AppInfrastructureInfo> appInfrastructureInfoPreferences;
		
    private PortletConfig portletConfig;
    private LiferayPortletConfig liferayPortletConfig;

    private final static String ROOT_FOLDER_NAME = PortletProps.get("fileupload.folder.path");
    private final static long UPLOAD_LIMIT = Long.parseLong(PortletProps.get("fileupload.limit"));
    private static final String TS_FORMAT = "yyyyMMddHHmmss";
	private static final String LS = System.getProperty("line.separator");;
    
    private final Log _log = LogFactoryUtil.getLog(CodeRadePortlet.class);


    @Override
    public void doView(RenderRequest renderRequest,
            RenderResponse renderResponse) throws IOException, PortletException {
        _log.debug("doView()");
        
        PortletPreferences preferences = renderRequest.getPreferences();
        
        int count = 0;
        appPreferences = getAppPreferences(preferences);
        appInfrastructureInfoPreferences = getAppInfrastructureInfo(preferences);
        
        for (AppInfrastructureInfo appInfrastructureInfo : appInfrastructureInfoPreferences) {
			if(appInfrastructureInfo.isEnableInfrastructure()){
				count++;
			}
		}
        
        renderRequest.setAttribute("enabledInfrastructuresCount", String.valueOf(count));
       
        super.doView(renderRequest, renderResponse);
    }

    @Override
    public void doEdit(RenderRequest renderRequest,
            RenderResponse renderResponse) throws IOException, PortletException {
        _log.debug("doEdit()");
                
        PortletPreferences preferences = renderRequest.getPreferences();
        
        appPreferences = getAppPreferences(preferences);
        appInfrastructureInfoPreferences = getAppInfrastructureInfo(preferences);
        
        renderRequest.setAttribute("appInfrastructureInfoPreferences", appInfrastructureInfoPreferences);
        renderRequest.setAttribute("appPreferences", appPreferences);
        
        super.doEdit(renderRequest, renderResponse);
    }

    /**
     *
     * @param actionRequest
     * @param actionResponse
     * @throws IOException
     * @throws PortletException 
     */
    @ProcessAction(name = "submit")
    public void submit(ActionRequest actionRequest,
            ActionResponse actionResponse) throws IOException, PortletException {

        AppInput appInput = new AppInput();
        portletConfig = (PortletConfig) actionRequest.getAttribute(JavaConstants.JAVAX_PORTLET_CONFIG);
        liferayPortletConfig = (LiferayPortletConfig) portletConfig;

        SimpleDateFormat dateFormat = new SimpleDateFormat(TS_FORMAT);
        String timestamp = dateFormat.format(Calendar.getInstance().getTime());
        appInput.setTimestamp(timestamp);

        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
        User user = themeDisplay.getUser();
        String username = user.getScreenName();
        appInput.setUsername(username);
        UploadPortletRequest uploadPortletRequest = PortalUtil.getUploadPortletRequest(actionRequest);

        String appServerPath = actionRequest.getPortletSession().getPortletContext().getRealPath(File.separator);

        try {
            String uploadedModel = processInputFile(uploadPortletRequest, username, timestamp, appInput);
            appInput.setInputSandbox(uploadedModel);
            String jobIdentifier = ParamUtil.getString(actionRequest, "jobIdentifier", appInput.getModelName() + "_" + appInput.getTimestamp());
            appInput.setJobIdentifier(jobIdentifier);
            _log.info(appInput);
            
            InfrastructureInfo[] enabledInfrastructures = getEnabledInfrastructures(appInfrastructureInfoPreferences);
                      
            submitJob(appServerPath, appInput, enabledInfrastructures);

            PortalUtil.copyRequestParameters(actionRequest, actionResponse);
            actionResponse.setRenderParameter("jobIdentifier", jobIdentifier);
            actionResponse.setRenderParameter("jspPage", "/jsps/submit.jsp");
        } catch (CodeRadePortletException ex) {
            _log.warn(ex.getMessage());
            SessionErrors.add(actionRequest, ex.getMessage());
        }        
        
        SessionMessages.add(actionRequest, liferayPortletConfig.getPortletId() + SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);
    }
    
    /**
     *
     * @param actionRequest
     * @param actionResponse
     */
    @ProcessAction(name = "savePreferences")
    public void savePreferences(ActionRequest actionRequest,
            ActionResponse actionResponse) {
        _log.debug("savePreferences()");
        PortletPreferences preferences = actionRequest.getPreferences();

        AppPreferences appPreferences_new=new AppPreferences();
        
        String gridOperationId = ParamUtil.getString(actionRequest, "pref_gridOperationId", "");
        String gridOperationDesc = ParamUtil.getString(actionRequest, "pref_gridOperationDesc", "");
        boolean productionEnv = ParamUtil.getBoolean(actionRequest, "pref_productionEnv", true);
        String jobRequirements = ParamUtil.getString(actionRequest, "pref_jobRequirements", "");
        String pilotScript = ParamUtil.getString(actionRequest, "pref_pilotScript", "");
        
        String sciGwyUserTrackingDB_Hostname,
        		sciGwyUserTrackingDB_Username,
        		sciGwyUserTrackingDB_Password,
        		sciGwyUserTrackingDB_Database = "";
        
        appPreferences_new.setGridOperationId(gridOperationId);
        appPreferences_new.setGridOperationDesc(gridOperationDesc);
        appPreferences_new.setProductionEnviroment(productionEnv);
        appPreferences_new.setJobRequirements(jobRequirements);
        appPreferences_new.setPilotScript(pilotScript);
        
        if(!productionEnv){
        	sciGwyUserTrackingDB_Hostname = ParamUtil.getString(actionRequest, "pref_sciGwyUserTrackingDB_Hostname", "");
        	sciGwyUserTrackingDB_Username = ParamUtil.getString(actionRequest, "pref_sciGwyUserTrackingDB_Username", "");
        	sciGwyUserTrackingDB_Password = ParamUtil.getString(actionRequest, "pref_sciGwyUserTrackingDB_Password", "");
        	sciGwyUserTrackingDB_Database = ParamUtil.getString(actionRequest, "pref_sciGwyUserTrackingDB_Database", "");
        	
        	appPreferences_new.setSciGwyUserTrackingDB_Hostname(sciGwyUserTrackingDB_Hostname);
            appPreferences_new.setSciGwyUserTrackingDB_Username(sciGwyUserTrackingDB_Username);
            appPreferences_new.setSciGwyUserTrackingDB_Password(sciGwyUserTrackingDB_Password);
            appPreferences_new.setSciGwyUserTrackingDB_Database(sciGwyUserTrackingDB_Database);
        }
                
        String JSONAppPrefs_new = JSONFactoryUtil.looseSerialize(appPreferences_new);
        _log.debug(JSONAppPrefs_new);
        try {
			preferences.setValue("appPreferences",JSONAppPrefs_new);
			preferences.store();
			_log.debug(preferences.getValue("appPreferences", null));
		} catch (ReadOnlyException e) {
			_log.error(e.getMessage());
			SessionErrors.add(actionRequest, e.getMessage());
			e.printStackTrace();
		} catch (ValidatorException e) {
			_log.error(e.getMessage());
			SessionErrors.add(actionRequest, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			_log.error(e.getMessage());
			SessionErrors.add(actionRequest, e.getMessage());
			e.printStackTrace();
		}
    }
    
    /**
    *
    * @param actionRequest
    * @param actionResponse
    * @throws IOException
    */
    @ProcessAction(name = "addInfrastructure")
    public void addInfrastructure(ActionRequest actionRequest,
    		ActionResponse actionResponse) throws IOException {
    	_log.debug("addInfrastructure()");
	   
    	boolean enabledInfrastructure = ParamUtil.getBoolean(actionRequest, "pref_enabledInfrastructure", true);
	   	int currInfrastructure = ParamUtil.getInteger(actionRequest, "pref_currInfrastructure", -1);
    	String nameInfrastructure = ParamUtil.getString(actionRequest, "pref_nameInfrastructure",  "");
    	String acronymInfrastructure = ParamUtil.getString(actionRequest, "pref_acronymInfrastructure", "");
    	String bdiiHost = ParamUtil.getString(actionRequest, "pref_bdiiHost", "");
    	String wmsHosts = ParamUtil.getString(actionRequest, "pref_wmsHosts", "");
    	String pxServerHost = ParamUtil.getString(actionRequest, "pref_pxServerHost", "");
    	String pxServerPort = ParamUtil.getString(actionRequest,  "pref_pxServerPort", "");
    	boolean pxServerSecure = ParamUtil.getBoolean(actionRequest, "pref_pxServerSecure", true);
    	String pxRobotId = ParamUtil.getString(actionRequest, "pref_pxRobotId", "");
	   	String pxRobotVO = ParamUtil.getString(actionRequest, "pref_pxRobotVO", "");
	   	String pxRobotRole = ParamUtil.getString(actionRequest, "pref_pxRobotRole", "");
	   	boolean pxRobotRenewalFlag = ParamUtil.getBoolean(actionRequest, "pref_pxRobotRenewalFlag", true);
	   	String pxUserProxy = ParamUtil.getString(actionRequest, "pref_pxUserProxy", "");
	   	String softwareTags = ParamUtil.getString(actionRequest, "pref_softwareTags", "");
	   	
	   	AppInfrastructureInfo appInfrastructureInfo = new AppInfrastructureInfo(currInfrastructure
	   			, enabledInfrastructure
	   			, nameInfrastructure
	   			, acronymInfrastructure
	   			, bdiiHost
	   			, wmsHosts
	   			, pxServerHost
	   			, pxServerPort
	   			, pxServerSecure
               	, pxRobotId
               	, pxRobotVO
               	, pxRobotRole
               	, pxRobotRenewalFlag
               	, pxUserProxy
               	, softwareTags
	   			);
	   
	   	PortletPreferences preferences = actionRequest.getPreferences();
	   	
	   	List<AppInfrastructureInfo> appInfrastructureInfosPreferences = getAppInfrastructureInfo(preferences);
	   	appInfrastructureInfosPreferences.add(appInfrastructureInfo);
	   	
	   	JSONArray jsonArray = createJSONArray(appInfrastructureInfosPreferences);
	   	
	    try {
			storeInfrastructureInfo(actionRequest, preferences, jsonArray);
			SessionMessages.add(actionRequest, "infra-saved-success");
		} catch (ReadOnlyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @ProcessAction(name = "disableInfra")
    public void disableInfra(ActionRequest actionRequest,
    		ActionResponse actionResponse){
    	_log.debug("disableInfra()");
    	int infraId = Integer.parseInt(ParamUtil.getString(actionRequest, "id", "-1"));
	   	if(infraId != -1){
	   		PortletPreferences preferences = actionRequest.getPreferences();
		   	List<AppInfrastructureInfo> appInfrastructureInfosPreferences = getAppInfrastructureInfo(preferences);
		   	appInfrastructureInfosPreferences.get(infraId - 1).setEnableInfrastructure(!appInfrastructureInfosPreferences.get(infraId - 1).isEnableInfrastructure());;		   
		   	_log.debug("Infrastructure " + (infraId - 1) + " is enabled: " + appInfrastructureInfosPreferences.get(infraId - 1).isEnableInfrastructure());
		   	JSONArray jsonArray = createJSONArray(appInfrastructureInfosPreferences);
		   	try {
				storeInfrastructureInfo(actionRequest, preferences, jsonArray);
				SessionMessages.add(actionRequest, "infra-toggle-success");
			} catch (ReadOnlyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	} else {
	   		_log.warn("No valid infrastructure id: " + infraId);
	   	}
    }

    @ProcessAction(name = "editInfra")
    public void editInfra(ActionRequest actionRequest,
    		ActionResponse actionResponse){
    	_log.debug("editInfra()");
    	//TODO
    } 
    
    @ProcessAction(name = "deleteInfra")
    public void deleteInfra(ActionRequest actionRequest,
    		ActionResponse actionResponse){
    	_log.debug("deleteInfra()");
    		   	
	   	int infraId = Integer.parseInt(ParamUtil.getString(actionRequest, "id", "-1"));
	   	if(infraId != -1){
	   		PortletPreferences preferences = actionRequest.getPreferences();
		   	List<AppInfrastructureInfo> appInfrastructureInfosPreferences = getAppInfrastructureInfo(preferences);
		   	AppInfrastructureInfo tmp = appInfrastructureInfosPreferences.remove(infraId - 1);
		   	_log.debug("Deleted Infrastructure: " + tmp.dump());
		   	JSONArray jsonArray = createJSONArray(appInfrastructureInfosPreferences);
		   	try {
				storeInfrastructureInfo(actionRequest, preferences, jsonArray);
				SessionMessages.add(actionRequest, "infra-delete-success");
			} catch (ReadOnlyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ValidatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   	} else {
	   		_log.warn("No valid infrastructure id: " + infraId);
	   	}
    }
        
    @ProcessAction(name = "saveInfrastructure")
    public void saveInfrastructure(ActionRequest actionRequest,
    		ActionResponse actionResponse){
    	_log.debug("saveInfrastructure()");
    }
    
    private String processInputFile(UploadPortletRequest uploadRequest, 
    		String username, String timestamp, AppInput appInput) throws CodeRadePortletException, IOException {

        String createdFile = "";
        String fileInputName = "fileupload"; //Input filed name in view.jsp        

        // Get the uploaded file as a file.
        File uploadedFile = uploadRequest.getFile(fileInputName);
        String sourceFileName = uploadRequest.getFileName(fileInputName);
        String modelName = FilenameUtils.removeExtension(sourceFileName);
        appInput.setModelName(modelName);
        String extension = FilenameUtils.getExtension(sourceFileName);

        long sizeInBytes = uploadRequest.getSize(fileInputName);

        if (uploadRequest.getSize(fileInputName) == 0) {
            throw new CodeRadePortletException("empty-file");
        }

        _log.debug("Uploading file: " + sourceFileName + " ...");

        File folder = new File(ROOT_FOLDER_NAME);

        // Check minimum storage space to save new files...
        if (folder.getUsableSpace() < UPLOAD_LIMIT) {
            throw new CodeRadePortletException("error-disk-space");
        } else if (sizeInBytes > UPLOAD_LIMIT) {
            throw new CodeRadePortletException("error-limit-exceeded");
        } else {

            // This is our final file path.
            File filePath = new File(folder.getAbsolutePath() + File.separator
                    + username + "_" + modelName + "_" + timestamp + "." + extension);

            // Move the existing temporary file to new location.
            FileUtils.copyFile(uploadedFile, filePath);
            _log.debug("File created: " + filePath);
            createdFile = filePath.getAbsolutePath();

        }
        return createdFile;
    }
    
    private AppPreferences getAppPreferences(PortletPreferences preferences) {
		String JSONAppPrefs = preferences.getValue("appPreferences", null);
        AppPreferences appPreferences=new AppPreferences();
        if(JSONAppPrefs!=null)
        	appPreferences = JSONFactoryUtil.looseDeserialize(JSONAppPrefs, AppPreferences.class);
        _log.info(appPreferences.dump());
		return appPreferences;
	}
    
	private List<AppInfrastructureInfo> getAppInfrastructureInfo(PortletPreferences preferences) {
		_log.debug("getAppInfrastructureInfo()");
		String JSONAppInfrastructuresInfo = preferences.getValue("appInfrastructureInfoPreferences", null);
        List<AppInfrastructureInfo> appInfrastructureInfoPreferences = new ArrayList<AppInfrastructureInfo>();
        if(JSONAppInfrastructuresInfo != null){
        	try {
				JSONArray JSONAppInfrastructuresInfoArray = JSONFactoryUtil.createJSONArray(JSONAppInfrastructuresInfo);
				for (int i = 0; i < JSONAppInfrastructuresInfoArray.length(); i++) {
					appInfrastructureInfoPreferences.add(JSONFactoryUtil.looseDeserialize(JSONAppInfrastructuresInfoArray.getJSONObject(i).toString(), AppInfrastructureInfo.class));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
        
        for (AppInfrastructureInfo appInfrastructureInfo : appInfrastructureInfoPreferences) {
			_log.info(appInfrastructureInfo.dump());
		}
		return appInfrastructureInfoPreferences;
	}
	
	private JSONArray createJSONArray(
			List<AppInfrastructureInfo> appInfrastructureInfosPreferences) {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();
	   	for (AppInfrastructureInfo appInfrastructureInfoPreferences : appInfrastructureInfosPreferences) {
			try {
				jsonArray.put(JSONFactoryUtil.createJSONObject(JSONFactoryUtil.looseSerialize(appInfrastructureInfoPreferences)));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonArray;
	}

	private void storeInfrastructureInfo(ActionRequest actionRequest,
			PortletPreferences preferences, JSONArray jsonArray) throws ReadOnlyException, ValidatorException, IOException {
		String JSONAppInfrastructureInfosPreferences_new = JSONFactoryUtil.looseSerialize(jsonArray);
        _log.debug(JSONAppInfrastructureInfosPreferences_new);
      
		preferences.setValue("appInfrastructureInfoPreferences",JSONAppInfrastructureInfosPreferences_new);
		preferences.store();
		SessionMessages.add(actionRequest, "saved-infra");
		_log.debug(preferences.getValue("appInfrastructureInfoPreferences", null));
        
	}
	
    private void submitJob(String appServerPath, AppInput appInput, InfrastructureInfo[] enabledInfrastructures) {

        // Job details
        String executable = "/bin/sh";                       // Application executable
        String arguments = appPreferences.getPilotScript(); // executable' arguments
        String outputPath = "/tmp/";                         // Output Path
        String outputFile = "code-rade-Output.txt";           // Distributed application standard output
        String errorFile = "code-rade-Error.txt";            // Distributed application standard error
        String appFile = "code-rade-Files.tar.gz";         // Hostname output files (created by the pilot script)

        // InputSandbox (string with comma separated list of file names)
        String inputSandbox=appServerPath+"WEB-INF/job/"    //
                           +appPreferences.getPilotScript() // pilot script
                           +","+appInput.getInputSandbox() // input file
                           ;
                
        // OutputSandbox (string with comma separated list of file names)
        String outputSandbox = appFile;                       // Output file
        
     // Take care of job requirements
        // More requirements can be specified in the preference value 'jobRequirements'
        // separating each requirement by the ';' character
        // The loop prepares a string array with GridEngine/JSAGA compliant requirements
        String jdlRequirements[] = appPreferences.getJobRequirements().split(";");
        int numRequirements=0;
        for(int i=0; i<jdlRequirements.length; i++) {
            if(!jdlRequirements[i].equals("")) {
              jdlRequirements[numRequirements] = "JDLRequirements=("+jdlRequirements[i]+")";
              numRequirements++;
              _log.info("Requirement["+i+"]='"+jdlRequirements[i]+"'");
            }
        } // for each jobRequirement
        
        // Prepare the GridEngine job description
        GEJobDescription jobDesc = new GEJobDescription();
        jobDesc.setExecutable (   executable); // Specify the executeable
        jobDesc.setArguments  (    arguments); // Specify the application' arguments
        jobDesc.setOutputPath (   outputPath); // Specify the output directory
        jobDesc.setOutput     (   outputFile); // Specify the std-output file
        jobDesc.setError      (    errorFile); // Specify the std-error file
        jobDesc.setOutputFiles(outputSandbox); // Setup output files (OutputSandbox) (*)
        jobDesc.setInputFiles ( inputSandbox); // Setut input files (InputSandbox)
        
        // GridEngine' MultiInfrastructure job submission object
        MultiInfrastructureJobSubmission miJobSubmission=null;
        
        // Initialize the GridEngine Multi Infrastructure Job Submission object
        //
        //  GridEngine uses two different kind of constructors. The constructor
        //  taking no database arguments is used for production environments, while
        //  the constructor taking SciGwyUserTrackingDB parameters is normally used
        //  for development purposes. In order to switch-on the production constructor
        //  just set to empty strings the following portlet init parameters or form
        //  the portlet preferences:
        //      sciGwyUserTrackingDB_Hostname
        //      sciGwyUserTrackingDB_Username
        //      sciGwyUserTrackingDB_Password
        //      sciGwyUserTrackingDB_Database
        //
        if(!appPreferences.isProductionEnviroment()){
        	String DBNM="jdbc:mysql://" + appPreferences.getSciGwyUserTrackingDB_Hostname() +
                    "/" + appPreferences.getSciGwyUserTrackingDB_Database();
        	String DBUS=appPreferences.getSciGwyUserTrackingDB_Username();
        	String DBPW=appPreferences.getSciGwyUserTrackingDB_Password();
        	miJobSubmission = new MultiInfrastructureJobSubmission(DBNM,DBUS,DBPW,jobDesc);
        	_log.info("MultiInfrastructureJobSubmission [DEVEL]\n"
        			+LS+"    DBNM: '" +  DBNM + "'"
        			+LS+"    DBUS: '" +  DBUS + "'"
        			+LS+"    DBPW: '" +  DBPW + "'"
        	);
        }
        
        else {
            miJobSubmission = new MultiInfrastructureJobSubmission(jobDesc);
            _log.info("MultiInfrastructureJobSubmission [PROD]");
        }
        
        for(int i=0; i < enabledInfrastructures.length; i++) {
            _log.info("Adding infrastructure #"+(i+1)
                     +" - Name: '"+enabledInfrastructures[i].getName()+"'"+LS);
            miJobSubmission.addInfrastructure(enabledInfrastructures[i]);
        }
        
        // GridOperations' Application Id
        int applicationId=Integer.parseInt(appPreferences.getGridOperationId());

        // Grid Engine' UserTraking needs the portal IP address
        String   portalIPAddress="";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            byte[] ipAddr=addr.getAddress();
            portalIPAddress= ""+(short)(ipAddr[0]&0xff)
                           +":"+(short)(ipAddr[1]&0xff)
                           +":"+(short)(ipAddr[2]&0xff)
                           +":"+(short)(ipAddr[3]&0xff);
        }
        catch(Exception e) {
            _log.error("Unable to get the portal IP address");
        }

        // Setup job requirements
        if(numRequirements>0)
            miJobSubmission.setJDLRequirements(jdlRequirements);

        // Ready now to submit the Job
        miJobSubmission.submitJobAsync( appInput.getUsername()
                                       ,portalIPAddress
                                       ,applicationId
                                       ,appInput.getJobIdentifier()
                                      );

        // Show log
        // View jobSubmission details in the log
        _log.info(
           LS+"JobSent"
          +LS+"-------"
          +LS+"Portal address: '"+portalIPAddress+"'"
          +LS+"Executable    : '"+executable     +"'"
          +LS+"Arguments     : '"+arguments      +"'"
          +LS+"Output path   : '"+outputPath     +"'"
          +LS+"Output sandbox: '"+outputSandbox  +"'"
          +LS+"Ouput file    : '"+outputFile     +"'"
          +LS+"Error file    : '"+errorFile      +"'"
          +LS+"Input sandbox : '"+inputSandbox   +"'"
          +LS); // _log.info
    }
    
    
    /**
     * Returns an array of GridEngine InfrastructureInfo object isntance related to
     * enabled instances of AppInfrastructureInfo items
     * 
     * @return Array of acrivated GridEngine' InfrastructureInfo infrastructures data
     */
    public InfrastructureInfo[] getEnabledInfrastructures(List<AppInfrastructureInfo> appInfrastructureInfoPreferences2) {
          _log.debug(LS+"*******************************"
                   +LS+" getEnabledInfrastructure"
                   +LS+"*******************************"
                   );
          List<AppInfrastructureInfo> enabledAppInfrastructuresInfo = new ArrayList<AppInfrastructureInfo>();
        //
        // Determine the number of enabled infrastructures
        //
        int numEnabledInfrastructures=0;
        for(int i=0; i < appInfrastructureInfoPreferences2.size(); i++) {
            // Counts the number of enabled infrastructures
            if(appInfrastructureInfoPreferences2.get(i).isEnableInfrastructure()){                    
            	numEnabledInfrastructures++;
            	enabledAppInfrastructuresInfo.add(appInfrastructureInfoPreferences2.get(i));
            }
        }
        
        _log.debug("Enabled infrastructures: '"+numEnabledInfrastructures+"'");

        // Initialize the array of GridEngine' infrastructure objects
        InfrastructureInfo infrastructuresInfo[] = new InfrastructureInfo[numEnabledInfrastructures];
        // For each infrastructure
        for(int i=0,h=0; i < numEnabledInfrastructures; i++) {
            // For each enabled infrastructure ... 
        	AppInfrastructureInfo enabledAppInfrastructureInfo = enabledAppInfrastructuresInfo.get(i);
            
        	// Take care of the adaptor/wms list
            // GridEngine supports a list of WMSes/adaptors as an array of strings
            // while the AppPreferences uses a ';' separated list of entries
            // Following code makes the necessary conversion
            String wmsHostList[]=null;
            if(null != enabledAppInfrastructureInfo.getWmsHosts() && !enabledAppInfrastructureInfo.getWmsHosts().equals("")) {
                wmsHostList = enabledAppInfrastructureInfo.getWmsHosts().split(";");
            } // if wmsList 

            // Multi-infrastructure support
            // Different Infrastructure objects can be created accordingly to the 
            // requested infrastructure type specified into the acronymInfrastructure
            // field. Each infrastructure maps its specific values accordingly to
            // several field mappings shown below on each infrastructure case
            if(enabledAppInfrastructureInfo.getAcronymInfrastructure().equalsIgnoreCase("ssh")) {
                    // Multi-infrastructure support (field mapping)
                    // ssh requires to specify:
                    // * Username      -> bdiiHost first  field of the given ';' separated string
                    // * Password      -> bdiiHost second field of the given ';' separated string
                    // * JSAGA adaptor -> wmsHosts
                    String sshItems[]  = enabledAppInfrastructureInfo.getBdiiHost().split(";");
                    int    numSshItems = sshItems.length;
                    String username = "";
                    String password = "";
                    if(numSshItems>0) username=sshItems[0].trim();
                    if(numSshItems>1) password=sshItems[1].trim();

                    // Build the GridEngine' infrastructure object and assign it to the infrastructures array
                    infrastructuresInfo[h++] = new InfrastructureInfo(enabledAppInfrastructureInfo.getAcronymInfrastructure()
                                                                      ,"ssh"
                                                                      ,username
                                                                      ,password
                                                                      ,wmsHostList);
                    _log.debug("SSH resource: "
                           +LS+" username:      '" + username    +"'"
                           +LS+" password:      '" + password    +"'"
                           +LS+" JSAGA adaptor: '" + wmsHostList +"'"
                             );
                } 
//                else if(enabledAppInfrastructureInfo.getAcronymInfrastructure().equalsIgnoreCase("rocci")) {
//                    // Multi-infrastructure support (field mapping)
//                    // rocci requires to specify:
//                    // * OCCI_ENDPOINT_HOST -> bdiiHost first  field of the given ';' separated string
//                    // * Password      -> bdiiHost second field of the given ';' separated string
//                    // * JSAGA adaptor -> wmsHosts
//                    
//                    String OCCI_ENDPOINT_HOST = wmsHostList[0];
//                 
//                    String OCCI_AUTH = "x509";
//                    
//                    // Possible RESOURCE values: 'os_tpl', 'resource_tpl', 'compute'
//                    String OCCI_RESOURCE = "compute";
//                    String OCCI_VM_TITLE = "RepastAnalysis";
//                    
//                    // Possible ACTION values: 'list', 'describe', 'create' and 'delete'
//                    String OCCI_ACTION = "create";
//                    
//                    String[] occiItems = enabledAppInfrastructureInfo.getSoftwareTags().split(";");
//                    int occiItemscount = occiItems.length;
//                    
//                    String OCCI_OS = "";
//                    String OCCI_FLAVOR = "";
//                    
//                    if(occiItemscount>0) 
//                        OCCI_OS=occiItems[0].trim();
//                    if(occiItemscount>1) 
//                        OCCI_FLAVOR=occiItems[1].trim();
//                    
//                    String rOCCIURL = OCCI_ENDPOINT_HOST + "?" +
//                            "action=" + OCCI_ACTION + 
//                            "&resource=" + OCCI_RESOURCE +
//                            "&attributes_title=" + OCCI_VM_TITLE +
//                            "&mixin_os_tpl=" + OCCI_OS +
//                            "&mixin_resource_tpl=" + OCCI_FLAVOR +
//                            "&auth=" + OCCI_AUTH;
//		
//                    String rOCCIResourcesList[] = {rOCCIURL};
//                                        
//                    infrastructuresInfo[h++] = new InfrastructureInfo(
//                            getAcronymInfrastructure(i), 
//                            getBdiiHost(i), 
//                            rOCCIResourcesList, 
//                            getPxServerHost(i), 
//                            getPxServerPort(i), 
//                            getPxRobotId(i), 
//                            getPxRobotVO(i), 
//                            getPxRobotRole(i), 
//                            true
//                    );
//                    if(_log !=null)
//                        _log.info(LS+"*******************************"
//                                 +LS+"OCCI resource URL: " + rOCCIURL
//                                 +LS+"*******************************");
//                }
                else {
                    // Multi-infrastructure support (no matching cases)
                    // If the acronym does not match to a specific infrastructure type
                    // the preference fields will be interpreted as gLite middleware settings

                    // Build the GridEngine' infrastructure object and assign it to the infrastructures array
                    // (!)Not yet used values:
                    //    pxServerSecure
                    //    pxRobotRenewalFlag
                    //    pxUserProxy                
                    infrastructuresInfo[h++] = new InfrastructureInfo( enabledAppInfrastructureInfo .getAcronymInfrastructure()
                                                                      ,enabledAppInfrastructureInfo.getBdiiHost()                                                                
                                                                      ,wmsHostList
                                                                      ,enabledAppInfrastructureInfo.getPxServerHost()
                                                                      ,enabledAppInfrastructureInfo.getPxServerPort()
                                                                      ,enabledAppInfrastructureInfo.getPxRobotId()
                                                                      ,enabledAppInfrastructureInfo.getPxRobotVO()
                                                                      ,enabledAppInfrastructureInfo.getPxRobotRole()                                                                
                                                                      ,enabledAppInfrastructureInfo.getSoftwareTags()
                                                                     );
                }                
                // Shows the added infrastructure
                _log.info(LS+appInfrastructureInfoPreferences2.get(i).dump());
            
        } // for each infrastructure
        return infrastructuresInfo;
    }
    
}
