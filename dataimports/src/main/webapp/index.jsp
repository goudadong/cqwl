<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<html>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
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
<script type="text/javascript" src="<%=basePath %>js/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
	function deleteAll(url,obj){
		$.ajax({
			type : "POST",
			url : url,
			data : {
				delFlag : obj
			},
			dataType : "json",
			async : false,
			success : function(data) {
				alert("一共删除了"+data.total+"条数据！");
			}
		});
	}
</script>
<body>
<h2>重庆文理学院数据导入</h2>
	<div style="margin-left: 5em;font-size:20px;">
		<ul>
			<li><a onclick="deleteAll('building/delete',1)" href="#">清除原有数据</a><a  href="campus/camplist">校区表</a></li>
			<li><a onclick="deleteAll('building/delete',2)" href="#">清除原有数据</a><a href="building/buildlist">楼栋信息表</a></li>
			<li><a onclick="deleteAll('building/delete',3)" href="#">清除原有数据</a><a href="teachPlace/teachPlacelist">教学场地表</a></li> 
			<li><a onclick="deleteAll('building/delete',4)" href="#">清除原有数据</a><a href="teachPlace/classroomlist">教室表</a></li>
			<li><a onclick="deleteAll('building/delete',5)" href="#">清除原有数据</a><a href="teachPlace/labBranchList">实验分室表</a></li>
			<li><a onclick="deleteAll('building/delete',6)" href="#">清除原有数据</a><a href="teachPlace/gymnasiumList">体育场馆表</a></li>
			<li><a onclick="deleteAll('building/delete',7)" href="#">清除原有数据</a><a href="institution/institutionlist">承担单位信息表</a></li>
			<li><a onclick="deleteAll('building/delete',8)" href="#">清除原有数据</a><a href="theoryCourse/theoryCourselist">课程环节表</a></li>
			<li><a onclick="deleteAll('building/delete',9)" href="#">清除原有数据</a><a href="openCourseSchedule/openCourseSchedulelist">课程开课计划表</a></li>
			<li><a onclick="deleteAll('building/delete',10)" href="#">清除原有数据</a><a href="publicOptionalCourse/publicOptionalCourseList">公选课计划表</a></li>
			<li><a onclick="deleteAll('building/delete',11)" href="#">清除原有数据</a><a href="studyHoursResolve/openCourseScheduleList">开课学时分解表</a><a href="studyHoursResolve/publicOptionalCourseList">开课学时分解表（公共任选课）</a></li>
			<li><a onclick="deleteAll('building/delete',12)" href="#">清除原有数据</a><a href="schoolCalendar/schoolCalendarList">校历表</a></li>
			<li><a onclick="deleteAll('building/delete',13)" href="#">清除原有数据</a><a href="teacherClass/teacherClasslist">课程教学班表</a></li>
			<li><a onclick="deleteAll('building/delete',14)" href="#">清除原有数据</a><a href="courseTotal/list">选课总表</a></li>
			<li><a onclick="deleteAll('building/delete',15)" href="#">清除原有数据</a><a href="studentSelectResult/studentSelectResultlist">选课结果表</a></li>
			<li><a onclick="deleteAll('building/delete',16)" href="#">清除原有数据</a><a href="scheduResult/list">排课结果表</a></li>
		</ul>
	</div>
</body>
</html>
