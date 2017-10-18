<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<style>
	ul li{
		margin:5px 0;
		list-style: none;
	}
	
</style>
<body>
<h2>数据导入成功！</h2>
	
	<ul>
		<li><h3>原始数据</h3></li>
		<c:forEach items="${list }" var="i">
			<li>${i }</li>
		</c:forEach>
	</ul>
	
	<ul>
		<li><h3>导入数据</h3></li>
		<c:forEach items="${o_list }" var="i">
			<li>${i }</li>
		</c:forEach>
	</ul>
</body>
</html>
