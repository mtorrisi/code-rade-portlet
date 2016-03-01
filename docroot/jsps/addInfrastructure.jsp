<%-- 
    Document   : edit.jsp
    Created on : Feb 24, 2016, 10:08:35 AM
    Author     : mario
--%>
<%@page import="it.dfa.unict.AppInfrastructureInfo"%>
<%@page import="com.liferay.portal.kernel.json.JSONFactoryUtil"%>
<%@include file="../init.jsp"%>

<%
	String backURL = ParamUtil.getString(request, "backURL");
	String JSONinfrastructure = ParamUtil.getString(renderRequest, "infrastructure", null);
	int id = Integer.parseInt(ParamUtil.getString(renderRequest, "id", "-1"));
	int infraNumber = 0;
	AppInfrastructureInfo infrastructureInfo = new AppInfrastructureInfo();
	if(JSONinfrastructure != null){
		infrastructureInfo = JSONFactoryUtil.looseDeserialize(JSONinfrastructure, AppInfrastructureInfo.class);
		infraNumber = infrastructureInfo.getId();
	} else if (id > 0){
		//TODO getInfra from pref
		infraNumber = id;
	}
	else		
		infraNumber = ParamUtil.getInteger(renderRequest, "infrastrucuresCount", 0) + 1;
%>

<liferay-ui:header backLabel="&laquo; Back" title=""
	backURL="<%=backURL%>" />


<portlet:actionURL name="addInfrastructure" var="addInfrastructureURL"></portlet:actionURL>

<center>
	<aui:form action="<%=addInfrastructureURL%>" method="post">

		<table class="table_prefs">
			<tr>
				<td colspan="2"><h3>Infrastructure preferences</h3> <hr/></td>
			</tr>
				<tr>
					<td class="label_prefs"><b>Enabled</b></td>
					<td> <aui:input type="checkbox" checked="true" name="pref_enabledInfrastructure" id="enabled" label="" value="<%= infrastructureInfo.isEnableInfrastructure() %>"/></td>
				</tr>
			<tr>
				<td class="label_prefs"><b>Infrastructure number </b></td>
				<td><aui:input type="text" style="width: 98%"
					name="pref_currInfrastructure" id="infraNumber" readonly="true"
					value="<%= infraNumber %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>Infrastructure name </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_nameInfrastructure" id="infraName"
					value="<%= infrastructureInfo.getNameInfrastructure() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>Infrastructure acronym </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_acronymInfrastructure" id="infraAcronym"
					value="<%= infrastructureInfo.getAcronymInfrastructure() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>BDII Host </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_bdiiHost" id="bdiiHost" value="<%= infrastructureInfo.getBdiiHost() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>WMS Hosts </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_wmsHosts" id="wmsHosts" value="<%= infrastructureInfo.getWmsHosts() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>Proxy Robot host server </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_pxServerHost" id="pxServerHost"
					value="<%= infrastructureInfo.getPxServerHost() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>Proxy Robot host port </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_pxServerPort" id="pxServerPort"
					value="<%= infrastructureInfo.getPxServerPort() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>Proxy Robot secure connection </b></td>
				<td><aui:input type="checkbox" checked="true" name="pref_pxServerSecure" id="enabled" label="" value="<%= infrastructureInfo.isPxServerSecure() %>"/></td>
<%-- 				<td><aui:input type="text" --%>
<%-- 					style="width: 98%" --%>
<%-- 					name="pref_pxServerSecure" id="pxServerSecure" --%>
<%-- 					value="" label="" /></td> --%>
			</tr>
			<tr>
				<td class="label_prefs"><b>Proxy
						Robot Identifier </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_pxRobotId" id="pxRobotId" value="<%= infrastructureInfo.getPxRobotId() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>Proxy
						Robot Virtual Organization </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_pxRobotVO" id="pxRobotVO" value="<%= infrastructureInfo.getPxRobotVO() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>Proxy
						Robot VO Role </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_pxRobotRole" id="pxRobotRole"
					value="<%= infrastructureInfo.getPxRobotRole() %>" label="" /></td>
			</tr>
			<tr>
				<td class="label_prefs"><b>Proxy
						Robot Renewal Flag </b></td>
				<td> 
					<aui:input type="checkbox" checked="true" name="pref_pxRobotRenewalFlag" id="enabled" label="" value="<%= infrastructureInfo.isPxRobotRenewalFlag() %>"/>
				</td>
<%-- 				<td><aui:input type="text" --%>
<%-- 					style="width: 98%" --%>
<%-- 					name="" id="pxRobotRenewalFlag" --%>
<%-- 					value="" label="" /></td> --%>
			</tr>
			<tr>
				<td class="label_prefs"><b>Local 
					Proxy</b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_pxUserProxy" id="pxUserProxy"
					value="<%= infrastructureInfo.getPxUserProxy() %>" label="" /></td>
			</tr>
				<tr>
				<td class="label_prefs"><b>Software
						Tags </b></td>
				<td><aui:input type="text"
					style="width: 98%"
					name="pref_softwareTags" id="softwareTags"
					value="<%= infrastructureInfo.getSoftwareTags() %>" label="" /></td>
			</tr>
			<tr>
                <td colspan="2"><aui:field-wrapper cssClass="centered">
                <aui:button name="save" value="Save" type="submit" />
                <aui:button name="cancel" value="Cancel" type="reset" />
            </aui:field-wrapper></td>
            </tr>
		</table>
	</aui:form>
</center>