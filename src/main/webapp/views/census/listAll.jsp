<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<script type="text/javascript">

document.cookie = "user=${user}";

</script>

<!-- En esta tabla mostraremos los últimos censos que se han realizado -->
<div class="container">
<display:table pagesize="5" class="census" name="census"
	requestURI="${requestURI}" id="census">
	
	<jstl:if test="${!misVotaciones}">
		<display:column>
			<a href="census/details.do?censusId=${census.id}"> <img
				alt="<spring:message	code="census.details" />"
				src="images/details.png" style="height: 25px; width: 25px;">

			</a>
		</display:column>
	</jstl:if>

	<jstl:if test="${misVotaciones}">
		<display:column>
			<a href="http://cabina-egc.jeparca.com/cabinaus/${census.idVotacion}"> <img
				alt="<spring:message	code="census.details" />"
				src="images/votar.gif" style="height: 25px; width: 25px;">

			</a>
		</display:column>
	</jstl:if>
	
	<!-- Atributos de la tabla -->
	<div class="col-md-3">
	<spring:message code="census.username" var="username" />
	<display:column property="username" title="${username}" sortable="true" />
	</div>
	
	<div class="col-md-3">
	<spring:message code="census.tituloVotacion" var="tituloVotacion" />
	<display:column property="tituloVotacion" title="${tituloVotacion}" sortable="true" />
	</div>
	
	<div class="col-md-3">
	<spring:message code="census.tipoCenso" var="tipoCenso" />
	<display:column property="tipoCenso" title="${tipoCenso}" sortable="true" />
	</div>
	
	<div class="col-md-3">
	<spring:message code="census.fechaInicioVotacion" var="fechaInicioVotacion" />
	<display:column title="${fechaInicioVotacion}" sortable="true"><fmt:formatDate value="${census.fechaInicioVotacion }" pattern="dd/MM/yyyy" /></display:column>
	</div>
	
	<div class="col-md-3">
	<spring:message code="census.fechaFinVotacion" var="fechaFinVotacion" />
	<display:column title="${fechaFinVotacion}" sortable="true"><fmt:formatDate value="${census.fechaFinVotacion }" pattern="dd/MM/yyyy" /></display:column>
	</div>
	
	
</display:table>
</div>
<br />
<acme:cancel url="welcome/index.do" code="census.back" />