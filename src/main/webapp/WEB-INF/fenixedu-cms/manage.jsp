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
<%@ taglib uri="http://fenixedu.org/taglib/intersection" prefix="r" %>

<h1><spring:message code="site.manage.title" /></h1>


<c:if test="${isManager}">
    <p>
        <a href="#" data-toggle="modal" data-target="#defaultSite" class="btn btn-default">Default site</a>
    </p>
</c:if>

<c:choose>
  <c:when test="${sites.size() == 0}">
    <em><spring:message code="site.manage.label.emptySites" /></em>
  </c:when>

  <c:otherwise>
    <table class="table table-striped">
      <thead>
        <tr>
          <th class="col-md-6"><spring:message code="site.manage.label.name" /></th>
          <th class="text-center"><spring:message code="site.manage.label.status" /></th>
          <th><spring:message code="site.manage.label.creationDate" /></th>
          <th></th>
        </tr>
      </thead>
      <tbody>
      <c:forEach var="i" items="${sites}">
        <tr>
          <td>
            <c:choose>
              <c:when test="${i.getInitialPage()!=null}">
                <h5><a href="${i.getInitialPage().getAddress()}" target="_blank">${i.getName().getContent()}</a>
                    <c:if test="${i.isDefault()}">
                        <span class="label label-success"><spring:message code="site.manage.label.default"/></span>
                    </c:if>
                </h5>
              </c:when>
              <c:otherwise>
                <h5>${i.getName().getContent()}</h5>
              </c:otherwise>
            </c:choose>
            <div><small>Url: <code>${i.baseUrl}</code></small></div>
          </td>
          <td class="text-center">
              <c:choose>
                  <c:when test="${ i.published }">
                      <span class="label label-primary">Available</span>
                  </c:when>
                  <c:otherwise>
                      <span class="label label-default">Unavailable</span>
                  </c:otherwise>
              </c:choose>
              <c:if test="${i.getEmbedded()}">
                  <p><span class="label label-info">Embedded</span></p>
              </c:if>
          </td>
          <td>${i.creationDate.toString('MMM dd, yyyy')}</td>
          <td>
            <div class="btn-group">
                <r:intersect location="cms.listSites" position="actionButtons">
                    <r:arg key="site" value="${i}"/>
                </r:intersect>
              <a href="${pageContext.request.contextPath}/cms/sites/${i.slug}" class="btn btn-sm btn-default"><spring:message code="action.manage"/></a>
            </div>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
    <c:if test="${numberOfPages != 1}">
    <div class="row">
        <div class="col-md-2 col-md-offset-5">
            <ul class="pagination">
                <li class="${currentPage <= 0 ? 'disabled' : 'active'}"><a href="${pageContext.request.contextPath}/cms/sites/manage/${page - 1}">«</a></li>
                <li class="disabled"><a href="#">${currentPage + 1} / ${numberOfPages}</a></li>
                <li class="${currentPage + 1 >= numberOfPages ? 'disabled' : 'active'}"><a href="${pageContext.request.contextPath}/cms/sites/manage/${page + 1}">»</a></li>
            </ul>
        </div>
    </div>
    </c:if>
    </c:otherwise>
</c:choose>

<div class="modal fade" id="defaultSite" tabindex="-1" role="dialog" aria-hidden="true">
    <form action="${pageContext.request.contextPath}/cms/sites/default" class="form-horizontal" method="post">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span
                            class="sr-only">Close</span></button>
                    <h4><spring:message code="action.set.default.site"/></h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="inputEmail3" class="col-sm-2 control-label"><spring:message code="theme.site"/>:</label>
                        <div class="col-sm-10">
                            <select class="form-control" name="slug">
                                <option value="">-</option>
                                <c:forEach var="i"  items="${sites}">
                                    <option value="${i.slug}">${i.name.content}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="submit" class="btn btn-primary"><spring:message code="label.save"/></button>
                </div>
            </div>
        </div>
    </form>
</div>