<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@include file="../init.jsp"%>

<%
	int enabledInfrastructuresCount = 0;
	String tmp = (String)renderRequest.getAttribute("enabledInfrastructuresCount");
	if(tmp != null)
		enabledInfrastructuresCount = Integer.parseInt(tmp);
%>


<c:choose>
	<c:when test="<%= enabledInfrastructuresCount == 0 %>" >
		<div class="portlet-msg-info">
			<liferay-ui:message key="no-infras-available" />
		</div>
	</c:when>
	<c:otherwise>
		<liferay-ui:error key="error-space" message="error-disk-space" />

		<portlet:actionURL name="submit" var="submitURL"></portlet:actionURL>

		<center>
			<aui:form action="<%=submitURL%>" enctype="multipart/form-data"
				method="post">

				<table>
					<tr>
						<td colspan="2"><liferay-ui:error key="empty-file"
								message="empty-file" /> <liferay-ui:error
								key="error-limit-exceeded" message="error-limit-exceeded" /></td>
					</tr>
					<tr>
						<td style="width: 40%"><b>Model</b></td>
						<td><aui:input type="file" name="fileupload" label=""
								id="model" title="model-archive" /></td>
					</tr>
					<tr>
						<td style="width: 40%"><b>Job Identifier</b></td>
						<td><aui:input type="text" name="jobIdentifier" label=""
								style="width: 98%" id="jobIdentifier" title="job-identifier" /></td>
					</tr>
					<tr>
						<td colspan="2"><aui:field-wrapper cssClass="centered">
								<aui:button name="submit" value="Submit" type="submit" />
								<aui:button name="demo" value="Demo" type="submit" />
								<aui:button name="help" value="Help" type="submit" />
							</aui:field-wrapper></td>
					</tr>
				</table>

			</aui:form>
		</center>
	</c:otherwise>
</c:choose>