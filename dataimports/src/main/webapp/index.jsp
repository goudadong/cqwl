<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
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
<h2>重庆文理学院数据导入</h2>
	<div style="margin-left: 5em;font-size:20px;">
		<ul>
			<li><a  href="campus/camplist">校区表</a></li>
			<li><a href="building/buildlist">楼栋信息表</a></li>
			<li><a href="teachPlace/teachPlacelist">教学场地表</a></li> 
			<li><a href="institution/institutionlist">承担单位信息表</a></li>
			<li><a href="theoryCourse/theoryCourselist">课程环节表</a></li>
			<li><a href="openCourseSchedule/openCourseSchedulelist">课程开课计划表</a></li>
			<li><a href="publicOptionalCourse/publicOptionalCourseList">公选课计划表</a></li>
			<li><a href="teacherClass/updatelist">课程教学班表</a></li>
			<li><a href="courseTotal/list">选课总表</a></li>
			<li><a href="studentSelectResult/studentSelectResultlist">选课结果表</a></li>
			<li><a href="scheduResult/list">排课结果表</a></li>
			<li><a href="schoolCalendar/schoolCalendarList">校历表</a></li>
			<li><a href="studyHoursResolve/openCourseScheduleList">开课学时分解表</a></li>
			<li><a href="studyHoursResolve/publicOptionalCourseList">开课学时分解表（公共任选课）</a></li>
		
		</ul>
	</div>
</body>
</html>
