<%@include file="../init.jsp"%>
<% 
	String jobIdentifier = ParamUtil.getString(renderRequest, "jobIdentifier", "");
%>
<table>
<tr>
  <td valign="top"><img align="left" style="padding:10px 10px;" src="<%=renderRequest.getContextPath()%>/images/AppLogo.png" /></td>
  <td>
  Your job has been <b>successfully</b> submitted; you may get reference to it with identifier:<br>
  <b><%= jobIdentifier %></b><br>
  Have a look on <a href="/my-jobs">MyJobs</a> area to get more information about all your submitted jobs.
  </td>
</tr>
<tr>
<td align="center"><form action="<portlet:actionURL portletMode="view"><portlet:param name="PortletStatus" value="ACTION_INPUT"/></portlet:actionURL>" method="post">
<input type="submit" value="Run a new application"></form></td>
<td>Press the <b>Run a new application</b> button to start another job submission</td>
</tr>
</table>

