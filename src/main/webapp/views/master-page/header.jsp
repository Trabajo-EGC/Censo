<%--
 * header.jsp
 *
 * Copyright (C) 2014 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>

<div class="col-md-12 col-xs-12">
<div class="row">
	<div class="container-fluid">
        <div class="navbar-header">
			<div id="cssmenu">
			<ul>
			<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.listar" /></a>
				<ul class="dropdown-menu">
					<li><a href="census/listAll.do"><spring:message code="master.page.census.all" /></a></li>
					<li><a href="census/mostPopularCensus.do"><spring:message code="master.page.census.mostPopular" /></a></li>	
					<li><a href="census/abstentionPercentage.do"><spring:message code="master.page.census.abstention" /></a></li>
					<li><a href="census/listActive.do"><spring:message code="master.page.census.active" /></a></li>
				</ul>
			</li>
			<security:authorize access="isAuthenticated()">
			<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown"><spring:message code="master.page.userlistar" /></a>
				<ul class="dropdown-menu">
					<security:authorize access="hasRole('ADMIN')">
					<li><a href="census/create.do"><spring:message code="master.page.census.createCensusFromFile" /></a></li>
					<li><a href="census/getAllCensusByCreador.do?token=${token}"><spring:message code="master.page.census.byCreator" /></a></li>
					</security:authorize>
					<li><a href="misc/mant.do"><spring:message code="master.page.census.updateUser" /></a></li>
					<!--<li><a href="census/updateUser.do"><spring:message code="master.page.census.updateUser" /></a></li>  -->
					<li><a href="census/votesByUser.do?token=${token}"><spring:message code="master.page.census.activeVotes" /></a></li>
					<security:authorize access="hasRole('CUSTOMER')">
					<li><a href="census/getCensusesToRegister.do"><spring:message code="master.page.census.listRegister" /></a></li>
					</security:authorize>
					<li><a href="census/findRecentFinishedCensus.do"><spring:message code="master.page.census.findRecentFinishedCensus" /></a></li>
				</ul>
			</li>
			</security:authorize>	
			<!-- Mientras no haya integración se evitará que salgan errores 404 -->
			<li class='has-sub'><a href="misc/mant.do"><spring:message code="master.page.votaciones" /></a></li>
			<li class='has-sub'><a href="misc/mant.do"><spring:message code="master.page.resultados" /></a></li>
			<li class='has-sub'><a href="misc/mant.do"><spring:message code="master.page.deliberaciones" /></a></li>
			<!--<li class='has-sub'><a href="http://localhost:8080/CreacionAdminVotaciones"><spring:message code="master.page.votaciones" /></a></li>-->
			<!--<li class='has-sub'><a href="http://localhost:8080/results_view"><spring:message code="master.page.resultados" /></a></li>-->
			<!--<li class='has-sub'><a href="http://localhost:8080/Deliberations"><spring:message code="master.page.deliberaciones" /></a></li>-->
			<li><a href="https://recuento.herokuapp.com/"><spring:message code="master.page.recuento" /></a></li>
			<li><a href="http://storage-egc1516.rhcloud.com/"><spring:message code="master.page.almacenamiento" /></a></li>
			<security:authorize access="isAnonymous()">
			<li><a href="security/login.do"><spring:message code="master.page.login" /></a></li>
			</security:authorize>
			
			<security:authorize access="isAuthenticated()">
			<li><a href="j_spring_security_logout"><spring:message code="master.page.logout" /></a></li>
			</security:authorize>
		
	</ul>
</div>
</div>
</div>
</div>
</div>


