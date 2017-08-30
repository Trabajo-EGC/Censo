<%--
 * action-2.jsp
 *
 * Copyright (C) 2013 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags"%>

<div class="col-md-12">
			<div class="row">
				<div class="col-md-6 col-xs-6 text-right">
					<h4><strong><spring:message code="census.token_propietario" />: </strong></h4>
				</div>
				<div class="col-md-6 col-xs-6 text-left">
					<h4><jstl:out value="${census.username}" /></h4>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-xs-6 text-right">
					<h4><strong><spring:message code="census.tipo" />: </strong></h4>
				</div>
				<div class="col-md-6 col-xs-6 text-left">
				<jstl:if test="${census.tipoCenso == 'abierto' }">
					<h4><spring:message code="census.abierto" /></h4>
				</jstl:if>

				<jstl:if test="${census.tipoCenso == 'cerrado' }">
					<h4><spring:message code="census.cerrado" /></h4>
				</jstl:if>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-xs-6 text-right">
					<h4><strong><spring:message code="census.votacio.name" />: </strong></h4>
				</div>
				<div class="col-md-6 col-xs-6 text-left">
					<h4><jstl:out value="${census.tituloVotacion}" /></h4>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-xs-6 text-right">
					<h4><strong><spring:message code="census.fecha.inicio" />: </strong></h4>
				</div>
				<div class="col-md-6 col-xs-6 text-left">
					<h4><fmt:formatDate value="${census.fechaInicioVotacion}"
					pattern="dd/MM/yyyy" /></h4>
				</div>
			</div>
			<div class="row">
				<div class="col-md-6 col-xs-6 text-right">
					<h4><strong><spring:message code="census.fecha.fin" />: </strong></h4>
				</div>
				<div class="col-md-6 col-xs-6 text-left">
					<h4><fmt:formatDate value="${census.fechaFinVotacion}" pattern="dd/MM/yyyy" /></h4>
				</div>
			</div>
</div>



<jstl:if test="${editable}">
	<acme:cancel url="census/edit.do?censusId=${census.id }"
		code="census.edit" />
</jstl:if>

<acme:cancel url="census/export.do?censusId=${census.id}"
	code="census.export" />


<acme:cancel url="welcome/index.do" code="census.back" />

