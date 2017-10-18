<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<style>
	ul li{
		margin:5px 0;
		list-style: none;
	}
	ul li a {
		margin:0 10px;
		font: bold;
		height: 21px;
        line-height: 21px;
        padding: 0 11px;
        background: #02bafa;
        border: 1px #26bbdb solid;
        border-radius: 3px;
        /*color: #fff;*/
        display: inline-block;
        text-decoration: none;
        font-size: 14px;
        outline: none;
	}
	
</style>
<body>
<h2>数据导入</h2>
	<ul>
		<li><a href="${url }">导入数据</a><a href="javascript:history.back(-1)">返回</a></li>
		<c:forEach items="${list }" var="i">
			<li>${i }</li>
		</c:forEach>
	</ul>
</body>
</html>
