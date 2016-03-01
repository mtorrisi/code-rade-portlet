package it.dfa.unict;

public class AppInfrastructureInfo {
	// Line separator
	// (!) Pay attention that altough the use of the LS variable
	// the replaceAll("\n","") has to be used
	static final String LS = System.getProperty("line.separator");

	private int id; 					  // Flag that enables/disable the infrastructure
	private boolean enableInfrastructure; // Flag that enables/disable the infrastructure
	private String nameInfrastructure; 	  // Complete name of the infrastructure
	private String acronymInfrastructure; // Infrastructure Acronym (short name)
	private String bdiiHost; 			  // topBDII host name
	private String wmsHosts; 			  // ';' separated list of enabled WMSes
	private String pxServerHost; 		  // eTokenServer hostname
	private String pxServerPort; 		  // eTokenServer port number
	private boolean pxServerSecure;   	  // eTokenServer secure connection flag
	private String pxRobotId; 			  // Robot proxy identifier
	private String pxRobotVO; 			  // Robot proxy VO
	private String pxRobotRole; 		  // Robot proxy role
	private boolean pxRobotRenewalFlag;   // Robot proxy renewal flag
	private String pxUserProxy; 		  // Holds a path to an User Proxy (test jobSubmissions)
	private String softwareTags; 		  // ';' separated list of software tags

	/**
	 * Standard constructor
	 * 
	 * Just initialize as empty all AppInfrastructureInfo fields
	 * 
	 * @see it.infn.ct.GridEngine.Job.InfrastructureInfo
	 */
	public AppInfrastructureInfo() {
		// Initialize AppInfrastructureInfo
		id = -1;
		
		enableInfrastructure = 
		pxRobotRenewalFlag = 
		pxServerSecure = true;

		nameInfrastructure = 
		acronymInfrastructure =
		bdiiHost =
		wmsHosts = 
		pxServerHost = 
		pxServerPort = 
		pxRobotId = 
		pxRobotVO = 
		pxRobotRole = 
		pxUserProxy = 
		softwareTags = "";
	} // AppInfrastructureInfo

	public AppInfrastructureInfo(
			int id,
			boolean enabledInfrastructure,
			String nameInfrastructure, 
			String acronymInfrastructure,
			String bdiiHost,
			String wmsHosts, 
			String pxServerHost,
			String pxServerPort, 
			boolean pxServerSecure,
			String pxRobotId,
			String pxRobotVO,
			String pxRobotRole,
			boolean pxRobotRenewalFlag, 
			String pxUserProxy,
			String softwareTags) {
		
			this.id 				  =id;
		 	this.enableInfrastructure =enabledInfrastructure;
	        this.nameInfrastructure   =nameInfrastructure;
	        this.acronymInfrastructure=acronymInfrastructure;
	        this.bdiiHost             =bdiiHost;
	        this.wmsHosts             =wmsHosts;
	        this.pxServerHost         =pxServerHost;
	        this.pxServerPort         =pxServerPort;
	        this.pxServerSecure       =pxServerSecure;
	        this.pxRobotId            =pxRobotId;
	        this.pxRobotVO            =pxRobotVO;
	        this.pxRobotRole          =pxRobotRole;
	        this.pxRobotRenewalFlag   =pxRobotRenewalFlag;
	        this.pxUserProxy          =pxUserProxy;
	        this.softwareTags         =softwareTags;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isEnableInfrastructure() {
		return enableInfrastructure;
	}

	public void setEnableInfrastructure(boolean enableInfrastructure) {
		this.enableInfrastructure = enableInfrastructure;
	}

	public String getNameInfrastructure() {
		return nameInfrastructure;
	}

	public void setNameInfrastructure(String nameInfrastructure) {
		this.nameInfrastructure = nameInfrastructure;
	}

	public String getAcronymInfrastructure() {
		return acronymInfrastructure;
	}

	public void setAcronymInfrastructure(String acronymInfrastructure) {
		this.acronymInfrastructure = acronymInfrastructure;
	}

	public String getBdiiHost() {
		return bdiiHost;
	}

	public void setBdiiHost(String bdiiHost) {
		this.bdiiHost = bdiiHost;
	}

	public String getWmsHosts() {
		return wmsHosts;
	}

	public void setWmsHosts(String wmsHosts) {
		this.wmsHosts = wmsHosts;
	}

	public String getPxServerHost() {
		return pxServerHost;
	}

	public void setPxServerHost(String pxServerHost) {
		this.pxServerHost = pxServerHost;
	}

	public String getPxServerPort() {
		return pxServerPort;
	}

	public void setPxServerPort(String pxServerPort) {
		this.pxServerPort = pxServerPort;
	}

	public boolean isPxServerSecure() {
		return pxServerSecure;
	}

	public void setPxServerSecure(boolean pxServerSecure) {
		this.pxServerSecure = pxServerSecure;
	}

	public String getPxRobotId() {
		return pxRobotId;
	}

	public void setPxRobotId(String pxRobotId) {
		this.pxRobotId = pxRobotId;
	}

	public String getPxRobotVO() {
		return pxRobotVO;
	}

	public void setPxRobotVO(String pxRobotVO) {
		this.pxRobotVO = pxRobotVO;
	}

	public String getPxRobotRole() {
		return pxRobotRole;
	}

	public void setPxRobotRole(String pxRobotRole) {
		this.pxRobotRole = pxRobotRole;
	}

	public boolean isPxRobotRenewalFlag() {
		return pxRobotRenewalFlag;
	}

	public void setPxRobotRenewalFlag(boolean pxRobotRenewalFlag) {
		this.pxRobotRenewalFlag = pxRobotRenewalFlag;
	}

	public String getPxUserProxy() {
		return pxUserProxy;
	}

	public void setPxUserProxy(String pxUserProxy) {
		this.pxUserProxy = pxUserProxy;
	}

	public String getSoftwareTags() {
		return softwareTags;
	}

	public void setSoftwareTags(String softwareTags) {
		this.softwareTags = softwareTags;
	}

	public static String getLs() {
		return LS;
	}

	/**
	 * Make a text dump of the whole AppInfrastructureInfo data
	 * 
	 * @return The content dump of the AppInfrastructureInfo values
	 * 
	 * @see AppInfrastructureInfo
	 */
	public String dump() {
		String dump = LS + "Preference values:" + LS 
				+ "------------------" + LS
				+ "    id 			: '" + id + "'"	+ LS 
				+ "    enableInfrastructure : '" + enableInfrastructure + "'" + LS
				+ "    nameInfrastructure   : '" + nameInfrastructure + "'" + LS 
				+ "    acronymInfrastructure: '" + acronymInfrastructure + "'" + LS 
				+ "    bdiiHost             : '" + bdiiHost + "'" + LS 
				+ "    wmsHosts             : '" + wmsHosts + "'" + LS 
				+ "    pxServerHost         : '" + pxServerHost + "'" + LS
				+ "    pxServerPort         : '" + pxServerPort + "'" + LS
				+ "    pxRobotId            : '" + pxRobotId + "'" + LS
				+ "    pxRobotRole          : '" + pxRobotRole + "'" + LS
				+ "    pxRobotVO            : '" + pxRobotVO + "'" + LS
				+ "    pxRobotRenewalFlag   : '" + pxRobotRenewalFlag + "'"	+ LS 
				+ "    pxUserProxy          : '" + pxUserProxy + "'" + LS
				+ "    softwareTags         : '" + softwareTags + "'" + LS;
		return dump;
	}
}
