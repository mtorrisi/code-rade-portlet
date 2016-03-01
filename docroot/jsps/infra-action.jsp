<%@page import="it.dfa.unict.AppInfrastructureInfo"%>
<%@page import="com.liferay.portal.kernel.dao.search.ResultRow"%>
<%@page import="com.liferay.portal.kernel.json.JSONFactoryUtil"%>
<%@include file="../init.jsp" %>

<%
    ResultRow row = (ResultRow) request.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
	AppInfrastructureInfo infrastructureInfo = (AppInfrastructureInfo) row.getObject();
%>


<liferay-ui:icon-menu>

	<liferay-portlet:actionURL var="disableInfraURL" name="disableInfra" >
		<portlet:param name="id" value="<%= String.valueOf(infrastructureInfo.getId()) %>"/>
		<portlet:param name="enabled" value="<%= String.valueOf(infrastructureInfo.isEnableInfrastructure()) %>"/>
	</liferay-portlet:actionURL>
	
	
	<c:if test="<%= infrastructureInfo.isEnableInfrastructure() %>">
        <liferay-ui:icon
            image="deactivate"
            url="<%=disableInfraURL %>"
            />

    </c:if>
    <c:if test="<%= !infrastructureInfo.isEnableInfrastructure() %>">
        <liferay-ui:icon
            image="activate"
            url="<%=disableInfraURL %>"
            />
    </c:if>

	<liferay-portlet:renderURL var="editInfraURL">
		<liferay-portlet:param name="backURL" value="<%= currentURL %>"/>
        <liferay-portlet:param name="infrastructure" value="<%= JSONFactoryUtil.looseSerialize(infrastructureInfo) %>" />
        <liferay-portlet:param name="jspPage"  value="/jsps/addInfrastructure.jsp"/>
    </liferay-portlet:renderURL>
    <liferay-ui:icon 
        image="edit" 
        label="true"
        url="<%= editInfraURL%>"/>
        
    <liferay-portlet:actionURL var="deleteInfraURL" name="deleteInfra" >
    	<portlet:param name="id" value="<%= String.valueOf(infrastructureInfo.getId()) %>"/>
    </liferay-portlet:actionURL>
    
    <liferay-ui:icon-delete url="<%=deleteInfraURL %>">
    </liferay-ui:icon-delete>

</liferay-ui:icon-menu>
