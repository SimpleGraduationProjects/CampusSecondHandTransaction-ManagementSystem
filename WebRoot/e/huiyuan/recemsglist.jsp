﻿<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%@ include file="law.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="daowen" uri="/daowenpager"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
	<head>
		<title>留言信息</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/e/css/index.css" type="text/css"></link>
     <link rel="stylesheet" href="${pageContext.request.contextPath}/e/css/box.all.css" type="text/css"></link>
    <script src="${pageContext.request.contextPath}/webui/jquery/jquery-1.8.3.min.js" type="text/javascript"></script>
    <link href="${pageContext.request.contextPath}/webui/artDialog/skins/default.css" rel="stylesheet" type="text/css" />
    <script src="${pageContext.request.contextPath}/webui/artDialog/jquery.artDialog.source.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/webui/artDialog/iframeTools.source.js" type="text/javascript"></script>
    <script src="${pageContext.request.contextPath}/webui/combo/combo.js" type="text/javascript"></script>
    <link href="${pageContext.request.contextPath}/webui/treetable/skin/jquery.treetable.css" rel="stylesheet" type="text/css" />
    <link href="${pageContext.request.contextPath}/webui/treetable/skin/jquery.treetable.theme.default.css" rel="stylesheet"
        type="text/css" />
    <script src="${pageContext.request.contextPath}/webui/treetable/js/jquery.treetable.js" type="text/javascript"></script>
     <link href="${pageContext.request.contextPath}/admin/css/web2table.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript">
			$(function() {
				$("#btnDelete").click(function(){
			    	var ids = $(".check[type=checkbox]:checked").serialize();
					 if($(".check:checked").length<1)
				        {
				           $.dialog.alert("请选择需要删除条目");
				           return;
				        } 
					if(!confirm("你确定要删除吗")){
						return;
					}
					$.ajax({
			             url: "${pageContext.request.contextPath}/admin/leavewordmanager.do?actiontype=delete",
			                     method: 'post',
			                     data: ids,
			                     success: function (data) {
			                          window.location.reload();
			                     },
			                     error: function (XMLHttpRequest, textStatus, errorThrown) {
			                         alert(XMLHttpRequest.status + errorThrown);
			                     }
			                 });
			    });
			    $("#btnCheckAll").click(function(){
			           var ischeck=false;
			           $(".check").each(function(){
			               if($(this).is(":checked"))
			               {
			                  $(this).prop("checked","");
			                  ischeck=false;
			                }
			               else
			               {
			                  $(this).prop("checked","true");
			                  ischeck=true;
			                }
			           });
			           if($(this).text()=="选择记录")
			              $(this).text("取消选择");
			           else
			              $(this).text("选择记录");
			    })
			});
       </script>
	</head>
	 <body >

	<jsp:include page="head.jsp"></jsp:include>

	<div class="wrap round-block">
		<div class="line-title">
			当前位置：<a href="${pageContext.request.contextPath}/e/index.jsp">首页</a> &gt;&gt; <a href="myindex.jsp">会员中心</a>
		</div>

		<div class="main">

			<jsp:include page="menu.jsp"></jsp:include>
			<div class="main-content">
				
				<!-- 搜索控件开始 -->
				<div class="search-options">
					<form id="searchForm"
						action="${pageContext.request.contextPath}/admin/leavewordmanager.do"
						method="post">
						<table cellspacing="0" width="100%">
							<tbody>
								<tr>
									<td> <input
										type="hidden" name="actiontype" value="search" /> <input
										type="hidden" name="replyren" value="${sessionScop.huiyuan.accountname}" />
										<input type="hidden" name="forwardurl"
										value="/e/huiyuan/recemsglist.jsp" />
										
										
									</td>
								</tr>
							</tbody>
						</table>
					</form>
				</div>
				<!-- 搜索控件结束 -->
				<div class="clear"></div>
				
				<table id="leaveword" width="100%" border="0" cellspacing="0"
					cellpadding="0" class="ui-record-table">
					<thead>
						<tr>
							<th><b>标题</b></th>
							<th><b>咨询时间</b></th>
							<th><b>状态</b></th>
							<th><b>咨询人</b></th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<c:if
							test="${listleaveword== null || fn:length(listleaveword) == 0}">
							<tr>
								<td colspan="20">没有相关留言信息</td>
							</tr>
						</c:if>
						<%
						    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							if (request.getAttribute("listleaveword") != null) {
								List<Leaveword> listleaveword = (List<Leaveword>) request.getAttribute("listleaveword");
								for (Leaveword temleaveword : listleaveword) {
						%>
						<tr>
							
							<td><%=temleaveword.getTitle()%></td>
							<td><%=sdf.format(temleaveword.getCreatetime())%></td>
							<td>
								<%
									if (temleaveword.getStatus() == 1) {
								%> 待回复 <%
									}
								%> <%
								 	if (temleaveword.getStatus() == 2) {
								 %> 已回复 <%
								 	}
								 %>
							</td>
							<td><%=temleaveword.getLwname()%>-<%=temleaveword.getLwren()%></td>
							<td>
								 <%if(temleaveword.getStatus()==1){%>
				                
				   
						<a class="orange-href"
							href="${pageContext.request.contextPath}/admin/leavewordmanager.do?actiontype=load&id=<%=temleaveword.getId()%>&replyren=<%=temleaveword.getReplyren() %>&forwardurl=/e/huiyuan/lwreply.jsp&seedid=404">回复</a>
				<%} %>
				 <%if(temleaveword.getStatus()==2){%>
				                
			      <a class="orange-href"
					href="${pageContext.request.contextPath}/e/huiyuan/leaveworddetails.jsp?id=<%=temleaveword.getId()%>&seedid=204">查看</a>
				 <%}%>		

							</td>
						</tr>
						<%
							}}
						%>
					</tbody>
				</table>
				<div class="clear"></div>
				<daowen:pager id="pager1" attcheform="searchForm" />

			</div>
		</div>
	</div>
</body>
</html>
