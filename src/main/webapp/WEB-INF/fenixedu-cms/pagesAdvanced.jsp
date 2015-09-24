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
${portal.toolkit()}

<div class="page-header">
    <h1><spring:message code="page.manage.title"/></h1>
    <h2><small><a href="${pageContext.request.contextPath}/cms/sites/${site.slug}">${site.name.content}</a></small></h2>
</div>

<p>
	<div class="row">
		<div class="col-sm-8">
			<a href="#" data-toggle="modal" data-target="#create-page" class="btn btn-primary"><i class="icon icon-plus"></i> New</a>
			<a href="${pageContext.request.contextPath}/cms/pages/${site.slug}" class="btn btn-default"><i class="glyphicon glyphicon-cog"></i> Simplified</a>
		</div>
		<div class="col-sm-4 pull-right">
			<input id="search-query" type="text" class="form-control" placeholder="Search for..." value="${query}">
		</div>
	</div>
</p>

<c:choose>
	<c:when test="${pages.size() == 0}">
		<i><spring:message code="page.manage.label.emptyPages"/></i>
	</c:when>

	<c:otherwise>
		<table class="table table-striped">
			<thead>
			<tr>
				<th><spring:message code="page.manage.label.name"/></th>
				<th><spring:message code="page.manage.label.creationDate"/></th>
				<th><spring:message code="page.manage.label.operations"/></th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="page" items="${pages}">
				<tr>
					<td>
						<h5><a href="${pageContext.request.contextPath}/cms/pages/advanced/${page.site.slug}/${page.slug}/edit">${page.name.content}</a>
							<c:if test="${page.site.initialPage == page}">
								<span class="label label-success"><spring:message code="site.manage.label.default"/></span>
							</c:if>
						</h5>

						<div>
							<small><spring:message code="page.manage.label.url"/>:
								<c:choose>
									<c:when test="${page.slug != ''}">
										<code>${page.slug}</code>
									</c:when>
									<c:otherwise>
										<code>-</code>
									</c:otherwise>
								</c:choose>
							</small>
						</div>
					</td>
					<td>${page.creationDate.toString('dd MMMM yyyy, HH:mm', locale)}
						<small>- ${page.createdBy.name}</small>
					</td>
					<td>
						<div class="btn-group">
							<c:choose>
								<c:when test="${page.slug != ''}">
									<a href="${pageContext.request.contextPath}/cms/pages/advanced/${page.site.slug}/${page.slug}/edit" class="btn btn-sm btn-default">
										<i class="glyphicon glyphicon-edit"></i>
									</a>
								</c:when>
								<c:otherwise>
									<a href="${pageContext.request.contextPath}/cms/pages/advanced/${page.site.slug}/--**--/edit" class="btn btn-sm btn-default">
										<i class="glyphicon glyphicon-edit"></i>
									</a>
								</c:otherwise>
							</c:choose>
							
							<a href="${page.address}" class="btn btn-default btn-sm">
								<i class="glyphicon glyphicon-link"></i>
							</a>

                            <div class="btn-group">
                                <button type="button" class="btn btn-default btn-sm btn-icon dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
                                    <i class="icon icon-dot-3"></i>
                                </button>

                                <ul class="dropdown-menu dropdown-menu-right" role="menu">
                                    <li><a href="#"><i class="glyphicon glyphicon-bullhorn">&nbsp;Unpublish</i></a></li>
                                    <li><a href="#" data-page="${page.slug}"><i class="glyphicon glyphicon-trash">&nbsp;Delete</i></a></li>
                                </ul>
                            </div>
						</div>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>

		<!-- Pagination -->
    	<c:if test="${partition.getNumPartitions() > 1}">
          <nav class="text-center">
            <ul class="pagination">
              <li ${partition.isFirst() ? 'class="disabled"' : ''}>
                <a href="#" onclick="goToPage(${partition.getNumber() - 1})">&laquo;</a>
              </li>
              <li class="disabled"><a>${partition.getNumber()} / ${partition.getNumPartitions()}</a></li>
              <li ${partition.isLast() ? 'class="disabled"' : ''}>
                <a href="#" onclick="goToPage(${partition.getNumber() + 1})">&raquo;</a>
              </li>
            </ul>
          </nav>
        </c:if>
	</c:otherwise>
</c:choose>

<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
						class="sr-only">Close</span></button>
				<h4><spring:message code="page.manage.label.delete.page"/></h4>
			</div>
			<div class="modal-body">
				<p><spring:message code="page.manage.label.delete.page.message"/></p>
			</div>
			<div class="modal-footer">
				<form id="deleteForm" method="POST">
					${csrf.field()}
					<button type="submit" class="btn btn-danger"><spring:message code="action.delete"/></button>
					<a class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></a>
				</form>
			</div>
		</div>
	</div>
</div>

<div class="modal fade" id="create-page" tabindex="-1" role="dialog" aria-labelledby="" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<form class="form-horizontal" action="${pageContext.request.contextPath}/cms/pages/advanced/${site.slug}/create" method="post" role="form">
			  ${csrf.field()}
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-hidden="true"> </button>
		        <h3 class="modal-title">New Page</h3>
		        <small>This could be the start of something great!</small>
		      </div>

		      <div class="modal-body">
		        <div class="${emptyName ? "form-group has-error" : "form-group"}">
		            <label class="col-sm-2 control-label"><spring:message code="post.create.label.name"/></label>
		            <div class="col-sm-10">
		                <input bennu-localized-string required-any name="name" placeholder="<spring:message code="post.create.label.name" />">
		                <c:if test="${emptyName != null}"><p class="text-danger"><spring:message code="post.create.error.emptyName"/></p>
		                </c:if>
		            </div>
		        </div>
		      </div>

		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		        <button type="Submit" class="btn btn-primary">Make</button>
		      </div>
			</form>
		</div>
	</div>
</div>

<script type="application/javascript">
  function getParameterByName(name) {
      var match = RegExp('[?&]' + name + '=([^&]*)').exec(window.location.search);
      return match && decodeURIComponent(match[1].replace(/\+/g, ' ')) || "";
  }
  
  function goToPage(pageNumber) {
      searchPosts({currentPage: pageNumber});
  }

  function searchPosts(options) {
    var searchQueryObj = {
        currentPage: options.currentPage || getParameterByName('currentPage'),
        query: typeof(options.query) === "string" ? options.query : getParameterByName('query')
    };
    window.location.search = $.param(searchQueryObj);
  }

  (function () {
		$("a[data-page]").on('click', function (el) {
			var pageSlug = el.target.getAttribute('data-page');
			$('#deleteForm').attr('action', '${pageContext.request.contextPath}/cms/pages/advanced/${site.slug}/' + pageSlug + '/delete');
			$('#deleteModal').modal('show');
		});

    $('#search-query').keypress(function (e) {
      if (e.which == 13) {
        searchPosts({ query: $('#search-query').val(), currentPage: 1});
      }
    });
  })();
</script>