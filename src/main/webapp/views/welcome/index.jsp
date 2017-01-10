<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>

<script type="text/javascript">

document.cookie = "user=${user}";

</script>

<!-- En esta tabla mostraremos los últimos censos que se han realizado -->

<spring:message code="census.searchText"/>
<input type="text" value="" id="textSearch" />
<input type="button" id="buttonSearch"
value="<spring:message code="census.searchButton"/>" />

<script type="text/javascript">
	$(document).ready(function(){
		$("#buttonSearch").click(function(){
			window.location.replace('census/searchByTitle.do?key=' + $("#textSearch").val());
		});
		$("#buttonSearch").onsubmit(function(){
			window.location.replace('census/searchByTitle.do?key=' + $("#textSearch").val());
		});
	});
</script>

<display:table pagesize="5" class="census" name="census"
	requestURI="${requestURI}" id="census">
	
	<!-- Atributos de la tabla -->
	<spring:message code="census.username" var="username" />
	<display:column property="username" title="${username}" sortable="true" />
	
	<spring:message code="census.tituloVotacion" var="tituloVotacion" />
	<display:column property="tituloVotacion" title="${tituloVotacion}" sortable="true" />
	
	<spring:message code="census.tipoCenso" var="tipoCenso" />
	<display:column property="tipoCenso" title="${tipoCenso}" sortable="true" />
	
	<spring:message code="census.fechaInicioVotacion" var="fechaInicioVotacion" />
	<display:column property="fechaInicioVotacion" title="${fechaInicioVotacion}" sortable="true" />
	
	<spring:message code="census.fechaFinVotacion" var="fechaFinVotacion" />
	<display:column property="fechaFinVotacion" title="${fechaFinVotacion}" sortable="true" />
	
	
</display:table>