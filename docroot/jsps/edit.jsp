<%-- 
    Document   : edit.jsp
    Created on : Feb 15, 2016, 10:08:35 AM
    Author     : mario
--%>

<%@page import="it.dfa.unict.AppPreferences"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="it.dfa.unict.AppInfrastructureInfo" %>
<%@page import="com.liferay.portal.kernel.util.ListUtil"%>
<%@page import="com.liferay.portal.kernel.util.ParamUtil"%>
<%@include file="../init.jsp"%>

<jsp:useBean id="appInfrastructureInfoPreferences" class="ArrayList<AppInfrastructureInfo>" scope="request" />
<jsp:useBean id="appPreferences" class="it.dfa.unict.AppPreferences" scope="request" />

<%
	String commonImgPath = "/html/themes/classic/images/common/";

	int infrastrucuresCount = 0;
	if(appInfrastructureInfoPreferences != null)
		infrastrucuresCount = appInfrastructureInfoPreferences.size();
%>


<liferay-portlet:actionURL name="savePreferences" var="savePreferencesUrl" />
<liferay-portlet:renderURL var="addInfrastructureUrl" windowState="normal">
	<liferay-portlet:param name="mvcPath" value="/jsps/edit-infrastructure.jsp"/>
	<liferay-portlet:param name="backURL" value="<%= currentURL %>"/>
	<liferay-portlet:param name="infrastrucuresCount" value="<%= String.valueOf(infrastrucuresCount) %>"/>
	<liferay-portlet:param name="add" value="<%= String.valueOf(true) %>"/>
</liferay-portlet:renderURL>

<liferay-portlet:actionURL name="pilotScript" var="pilotScriptUrl" >
	<liferay-portlet:param name="pilotScript" value="<%= appPreferences.getPilotScript() %>" />
</liferay-portlet:actionURL>

<aui:form action="${savePreferencesUrl}" name="aForm" method="post">
<aui:fieldset label="generic-pref-lbl">
<aui:field-wrapper>
   	<aui:button name="save" value="save" type="submit" />
</aui:field-wrapper>
<aui:layout> 
    <aui:column columnWidth="50" first="true" >
		<table class="table_prefs">
	     	<tr>
	         	<td ><b>Application Identifier</b></td>
	         	<td><aui:input type="text" name="pref_gridOperationId" label="" style="width: 98%"	
	                    id="gridOperationId" title="application-identifier" value="<%=appPreferences.getGridOperationId() %>"/></td>
	     	</tr>
	     	<tr>
	         	<td ><b>Application label</b></td>
	        	<td><aui:input type="text" name="pref_gridOperationDesc" label="" style="width: 98%"
	                    id="gridOperationDesc" title="Infrastructure number" value="<%=appPreferences.getGridOperationDesc() %>"/></td>
	     	</tr>
	     	<tr>
	         	<td ><b>Infrastructures counter</b></td>
	         	<td><aui:input type="text" name="pref_numInfrastructures" label="" style="width: 98%"
	                    id="numInfrastructures" title="Infrastructure number" disabled="true" value="<%= infrastrucuresCount %>" /></td>
	     	</tr>
	     	<tr>
	         	<td ><b>Production environment</b></td>
	         	<td><aui:input type="checkbox" name="pref_productionEnv" label=""
	                    id="productionEnv" title="Production environment" checked="<%=appPreferences.isProductionEnviroment() %>" onChange="showDevEnvPanel()"/></td>
	     	</tr>
   			<tr>
       			<td ><b>Application job requirements</b></td>
       			<td><aui:input type="textarea" name="pref_jobRequirements" label="" style="width: 98%"	
               	    id="jobRequirements" title="Application requirements" value="<%= appPreferences.getJobRequirements() %>"/></td>
   			</tr>
   			<tr>
       			<td ><aui:button id="add" name="add" value="Pilot script" onClick="<%= pilotScriptUrl %>"/></td>
       			<td><aui:input type="text" name="pref_pilotScript" label="" style="width: 98%"
               	    id="pilotScript" title="Application pilot script" value="<%=appPreferences.getPilotScript() %>"/></td>
   			</tr>
		</table>
	</aui:column>
	<aui:column columnWidth="50" last="true">
		<div id="devEnvPrefs">
			<table class="table_prefs" >
     			<tr>
         			<td ><b>UserTrackingDB hostname	</b></td>
         			<td><aui:input type="text" name="pref_sciGwyUserTrackingDB_Hostname" label="" style="width: 98%"	
                    	id="sciGwyUserTrackingDB_Hostname" title="UserTrackingDB hostname" value="<%= appPreferences.getSciGwyUserTrackingDB_Hostname() %>"/></td>
     			</tr>
     			<tr>
         			<td ><b>UserTrackingDB username </b></td>
         			<td><aui:input type="text" name="pref_sciGwyUserTrackingDB_Username" label="" style="width: 98%"	
                    	id="sciGwyUserTrackingDB_Username" title="UserTrackingDB username" value="<%= appPreferences.getSciGwyUserTrackingDB_Username() %>"/></td>
     			</tr>
     			<tr>
         			<td ><b>UserTrackingDB password </b></td>
         			<td><aui:input type="text" name="pref_sciGwyUserTrackingDB_Password" label="" style="width: 98%"	
                    	id="sciGwyUserTrackingDB_Password" title="UserTrackingDB password" value="<%= appPreferences.getSciGwyUserTrackingDB_Password() %>" /></td>
     			</tr>
     			<tr>
         			<td ><b>UserTrackingDB database </b></td>
         			<td><aui:input type="text" name="pref_sciGwyUserTrackingDB_Database" label="" style="width: 98%"	
                    	id="sciGwyUserTrackingDB_Database" title="UserTrackingDB database" value="<%= appPreferences.getSciGwyUserTrackingDB_Database() %>"/></td>
     			</tr>
			</table>
		</div>
	</aui:column>
</aui:layout>
</aui:fieldset>
</aui:form>
<aui:layout> 
<aui:fieldset label="available-infras">
    <aui:column columnWidth="100" first="true" >
			<aui:field-wrapper>
				<aui:button id="add" name="add" value="add-new" onClick="<%= addInfrastructureUrl %>"/>
			</aui:field-wrapper>
			<liferay-ui:success key="infra-saved-success" message="infra-saved-success" />
			<liferay-ui:success key="infra-delete-success" message="infra-delete-success" />
			<liferay-ui:success key="infra-toggle-success" message="infra-toggle-success" />
			<liferay-ui:search-container delta="10" emptyResultsMessage="no-infras-available" >
				<liferay-ui:search-container-results
					total="<%=appInfrastructureInfoPreferences.size()%>"
					results="<%=ListUtil.subList(appInfrastructureInfoPreferences, searchContainer.getStart(), searchContainer.getEnd())%>" />
				<liferay-ui:search-container-row modelVar="infrastructure" 
						className="it.dfa.unict.AppInfrastructureInfo" >						
					<liferay-ui:search-container-column-text name="name"
						value="<%=infrastructure.getNameInfrastructure()%>" />
					<liferay-ui:search-container-column-text name="acronym"
						value="<%=infrastructure.getAcronymInfrastructure()%>" />
					<liferay-ui:search-container-column-text name="status">
						<img src="<%= infrastructure.isEnableInfrastructure() ? commonImgPath + "activate.png" : commonImgPath + "deactivate.png" %>" />
					</liferay-ui:search-container-column-text>
					<liferay-ui:search-container-column-jsp path="/jsps/infra-action.jsp"/>
				</liferay-ui:search-container-row>
				<liferay-ui:search-iterator searchContainer="<%= searchContainer %>" />
			</liferay-ui:search-container>
     </aui:column>
</aui:fieldset>
</aui:layout>

<script type="text/javascript">
	var productionEnvId = "<portlet:namespace/>productionEnv";
	var devEnvPrefsId="devEnvPrefs";
</script>
