<%@include file="../init.jsp"%>


<jsp:useBean id="pilotScript" class="java.lang.String" scope="request" />

<%
	// Below the descriptive area of the view pilot script
%>
<table>
	<tr>
		<td valign="top"><img align="left" style="padding: 10px 10px;"
			src="<%=renderRequest.getContextPath()%>/images/AppLogo.png" />
		</td>
		<td>Please edit/view the selected pilot script and then press <b>'CHANGE'</b>
			button to apply your changes.<br> Press <b>'Cancel'</b> to
			discard any change and go back to preferences.
		</td>
	<tr>
</table>
<%
	// Below the view/edit pilot script web form 
	//
%>
<center>
	<form
		action="<portlet:actionURL portletMode="view"><portlet:param name="PortletStatus" value="ACTION_PILOT"/></portlet:actionURL>"
		method="post">
		<table>
		<dl>
			<!-- This block contains: label, file input and textarea for GATE Macro file -->
			<dd>
				<p>
					<b>Pilot script</b>
			</dd>
			<dd>
				<textarea style="font-family: monospace; font-size: 12px;"
					id="pilotScript" rows="20" cols="100%" name="pilotScript"><%=pilotScript%></textarea>
			</dd>
			<!-- This block contains form buttons: CHANGE, Cancel and Reset values -->
			<dd>
				<td><input type="submit" value="CHANGE"></td>
				<td><input type="reset" value="Reset values"
					onClick="resetForm()"></td>
			</dd>
		</dl>
		</table>
	</form>
	<form
		action="<portlet:renderURL portletMode="edit">/></portlet:renderURL>"
		method="post">
		<dd>
			<td><input type="submit" value="Cancel"></td>
		</dd>
	</form>
</center>