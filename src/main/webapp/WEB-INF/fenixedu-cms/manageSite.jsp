<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu CMS.

    FenixEdu CMS is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu CMS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu CMS.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<c:set var="locale" value="<%= org.fenixedu.commons.i18n.I18N.getLocale() %>"/>


	<div class="page-header">
  	<h1>${site.name.content}</h1>
  	<h2><small>Site Managment</small></h2>
  	<div class="row">
	  <div class="col-sm-8">
	    <button type="button" class="btn btn-primary">New post</button>
	    
	    <a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/edit" class="btn btn-default">Settings</a>
			
			<c:if test="${site.published}">
				<a href="${site.fullUrl}" target="_blank" class="btn btn-default"><spring:message code="action.link"/></a>
			</c:if>
	  </div>
	  <div class="col-sm-4">
	    <input type="search" class="form-control pull-right" placeholder="Search posts...">
	  </div>
	</div>
	</div>


<!-- 		<c:if test="${not site.published}">
			<span class="badge"><spring:message code="site.manage.label.unpublished"/></span>
		</c:if>
		<div class="button-group pull-right">
			<a href="${pageContext.request.contextPath}/cms/sites/${site.slug}/edit" class="btn btn-primary"><spring:message code="action.edit"/></a>
			<c:if test="${site.published}">
				<a href="${site.fullUrl}" target="_blank" class="btn btn-default"><spring:message code="action.link"/></a>
			</c:if>
		</div> -->

	<div class="row placeholders">
		<div class="col-xs-6 col-sm-3 placeholder">
			<div class="pretty-number">
				${site.postSet.size()}
			</div>
			<h4><spring:message code="site.manage.label.posts"/></h4>
			<span class="text-muted"><a href="${pageContext.request.contextPath}/cms/posts/${site.slug}"><spring:message code="action.show.all"/></a></span>
		</div>
		<div class="col-xs-6 col-sm-3 placeholder">
			<div class="pretty-number green">
				${site.pagesSet.size()}
			</div>
			<h4><spring:message code="site.manage.label.pages"/></h4>
			<span class="text-muted"><a href="${pageContext.request.contextPath}/cms/pages/${site.slug}"><spring:message code="action.show.all"/></a></span>
		</div>
		<div class="col-xs-6 col-sm-3 placeholder">
			<div class="pretty-number">
				${site.categoriesSet.size()}
			</div>
			<h4><spring:message code="site.manage.label.categories"/></h4>
			<span class="text-muted"><a href="${pageContext.request.contextPath}/cms/categories/${site.slug}"><spring:message code="action.show.all"/></a></span>
		</div>
		<div class="col-xs-6 col-sm-3 placeholder">
			<div class="pretty-number green">
				<img height="50" src="${pageContext.request.contextPath}/static/img/menu.svg"/>
			</div>
			<h4><spring:message code="site.manage.label.menus"/></h4>
			<span class="text-muted"><a href="${pageContext.request.contextPath}/cms/menus	/${site.slug}"><spring:message code="action.show.all"/></a></span>
		</div>
	</div>

	<div class="row">
		<div class="col-sm-7">
			<h3 class="sub-header"><spring:message code="site.manage.label.latest.posts"/> <a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/create" class="btn btn-primary pull-right"><spring:message code="action.create"/></a></h3>
			<div class="table-responsive">
				<table class="table table-striped">
					<thead>
						<tr>
							<th><spring:message code="page.manage.label.name" /></th>
							<th><spring:message code="site.manage.label.creationDate"/></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="post" items="${site.latestPosts}">
							<tr>
								<td>
									<c:if test="${site.published}">
										<a href="${post.address}" target="_blank">${post.name.content}</a>
									</c:if>
									<c:if test="${!site.published}">
										${post.name.content}
									</c:if>
								</td>
								<td>${post.creationDate.toString('dd MMMM yyyy, HH:mm', locale)} <small>- ${post.createdBy.name}</small></td>
								<td><a href="${pageContext.request.contextPath}/cms/posts/${site.slug}/${post.slug}/edit"><spring:message code="action.edit"/></a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		<div class="col-sm-5">
			<h3 class="sub-header">${site.name.content}</h3>
			<p><strong><spring:message code="site.edit.label.theme"/>: </strong> ${site.theme.name}</p>
			<p><strong><spring:message code="site.edit.label.description"/>	: </strong> ${site.description.content}</p>
			<p><strong><spring:message code="site.edit.label.slug"/>: </strong> <code>${site.fullUrl}</code></p>
			<p><strong><spring:message code="site.manage.label.visibility"/>: </strong> ${site.canViewGroup.presentationName}</p>
		</div>
	</div>



<style>
.sub-header {
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.pretty-number {
	border-radius: 50%;
	height: 100px;
	width: 100px;
	line-height: 100px;
	background-color: #147ad2;
	margin-left: auto;
	margin-right: auto;
	text-align: center;
	font-weight: bold;
	color: white;
	font-size: 20px;
}
.green {
	background-color: #33d69c;
}
.placeholders {
  margin-bottom: 30px;
  text-align: center;
}
.placeholders h4 {
  margin-bottom: 0;
}
</style>