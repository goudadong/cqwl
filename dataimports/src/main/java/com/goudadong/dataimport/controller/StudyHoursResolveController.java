package com.goudadong.dataimport.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.StudyHoursResolveService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;



/** 课程开课计划分解
 * 
 * @author wulinmin
 *
 */
@Controller
@RequestMapping(value="studyHoursResolve")
public class StudyHoursResolveController {
	@Resource(name="studyHoursResolveService")
	private StudyHoursResolveService studyHoursResolveService;
	
	@RequestMapping(value="openCourseScheduleList")
	public ModelAndView openCourseScheduleList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		List<PageData> list =  studyHoursResolveService.o_openCourseSchedule(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveStudyHoursResolve");
		mv.setViewName("index");
		return mv;
	}
	
	@RequestMapping(value="saveStudyHoursResolve")
	public ModelAndView saveStudyHoursResolve() throws Exception {
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		List<PageData>list =  studyHoursResolveService.o_openCourseSchedule(null);
		PageData pdData = studyHoursResolveService.getMaxId();
		Object object = pdData.get("MAX_ID");
		int maxid = Integer.parseInt(object.toString());
		for (PageData pageData : list) {
			long openCourseScheduleId = Long.parseLong((pageData.get("MAINID")+"").trim());

			pageData.put("openCourseScheduleId", openCourseScheduleId);
				
			//是否公选课，此处置为否 0
			pageData.put("isPublicCouse", 0);
			
			//学时类别
			//对应的课程性质courseNature：  01-公选课   02-公共基础课  03-专业基础课   04-专业课  05-实践课
			if ((pageData.get("COURSENATURE")+"").trim().equals("05")) { //实践课 直接用实践课代码
				if(pageData.containsKey("COURSECODE")){
					pageData.put("studyHoursOrLinkType", (pageData.get("COURSECODE")+"").trim());
				}else{
					pageData.put("studyHoursOrLinkType","");
				}
				pageData.put("manageSysCode", "002"); //实验实训管理系统
				pageData.put("ISVALID", 1);
				pageData.put("ISDELETED", 0);
				maxid++;
				pageData.put("mainId", maxid);
				studyHoursResolveService.studyHoursResolve_insert(pageData);
			}else {//理论课就判断学时大小
				//不包含，则默认0
				if(!pageData.containsKey("THEORYHOURS")){
					pageData.put("THEORYHOURS", 0);
				}
				if(!pageData.containsKey("PRACTICEHOURS")){
					pageData.put("PRACTICEHOURS", 0);
				}
				if(!pageData.containsKey("EXPERIMENTHOURS")){
					pageData.put("EXPERIMENTHOURS", 0);
				}
				if(!pageData.containsKey("PRACTICALTRAININGHOURS")){
					pageData.put("PRACTICALTRAININGHOURS", 0);
				}
				if(!pageData.containsKey("COMPUTERHOURS")){
					pageData.put("COMPUTERHOURS", 0);
				}
				if(!pageData.containsKey("OTHERHOURS")){
					pageData.put("OTHERHOURS", 0);
				}
				
				//判断学时大小,确定学时类型
				// 01：理论学时     02：实践学时   03：  实验学时    04： 实训学时   05：上机学时   06：其他学时
				if(Long.parseLong(pageData.get("THEORYHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "01"); //学时类型
					pageData.put("manageSysCode", "001"); //理论教学系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("PRACTICEHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "02");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("EXPERIMENTHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "03");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("PRACTICALTRAININGHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "04");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("COMPUTERHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "05");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("OTHERHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "06");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				
				if(!pageData.containsKey("studyHoursOrLinkType")){ //如果不包含该字段，进行相关处理
					pageData.put("studyHoursOrLinkType", "");
					pageData.put("manageSysCode", "002"); 
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}

			}

		}
		List<PageData> o_List = new ArrayList<PageData>();
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
	
	@RequestMapping(value="publicOptionalCourseList")
	public ModelAndView publicOptionalCourseList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		List<PageData> list =  studyHoursResolveService.o_publicOptionalCourse(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveStudyHoursResolve2");
		mv.setViewName("index");
		return mv;
	}
	
	@RequestMapping(value="saveStudyHoursResolve2")
	public ModelAndView saveStudyHoursResolve2() throws Exception {
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		List<PageData>list =  studyHoursResolveService.o_publicOptionalCourse(null);
		PageData pdData = studyHoursResolveService.getMaxId();
		Object object = pdData.get("MAX_ID");
		int maxid = Integer.parseInt(object.toString());
		for (PageData pageData : list) {
			long openCourseScheduleId = Long.parseLong((pageData.get("MAINID")+"").trim());

			pageData.put("openCourseScheduleId", openCourseScheduleId);
				
			//公共任选课，此处为1
			pageData.put("isPublicCouse", 1);
			
			//学时类别
			//对应的课程性质courseNature：  01-公选课   02-公共基础课  03-专业基础课   04-专业课  05-实践课
			if ((pageData.get("COURSENATURE")+"").trim().equals("05")) { //实践课 直接用实践课代码
				if(pageData.containsKey("COURSECODE")){
					pageData.put("studyHoursOrLinkType", (pageData.get("COURSECODE")+"").trim());
				}else{
					pageData.put("studyHoursOrLinkType","");
				}
				pageData.put("manageSysCode", "002"); //实验实训管理系统
				pageData.put("ISVALID", 1);
				pageData.put("ISDELETED", 0);
				maxid++;
				pageData.put("mainId", maxid);
				studyHoursResolveService.studyHoursResolve_insert(pageData);
			}else {//理论课就判断学时大小
				//不包含，则默认0
				if(!pageData.containsKey("THEORYHOURS")){
					pageData.put("THEORYHOURS", 0);
				}
				if(!pageData.containsKey("PRACTICEHOURS")){
					pageData.put("PRACTICEHOURS", 0);
				}
				if(!pageData.containsKey("EXPERIMENTHOURS")){
					pageData.put("EXPERIMENTHOURS", 0);
				}
				if(!pageData.containsKey("PRACTICALTRAININGHOURS")){
					pageData.put("PRACTICALTRAININGHOURS", 0);
				}
				if(!pageData.containsKey("COMPUTERHOURS")){
					pageData.put("COMPUTERHOURS", 0);
				}
				if(!pageData.containsKey("OTHERHOURS")){
					pageData.put("OTHERHOURS", 0);
				}
				
				//判断学时大小,确定学时类型
				// 01：理论学时     02：实践学时   03：  实验学时    04： 实训学时   05：上机学时   06：其他学时
				if(Long.parseLong(pageData.get("THEORYHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "01"); //学时类型
					pageData.put("manageSysCode", "001"); //理论教学系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("PRACTICEHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "02");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("EXPERIMENTHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "03");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("PRACTICALTRAININGHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "04");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("COMPUTERHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "05");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				if(Long.parseLong(pageData.get("OTHERHOURS").toString().trim()) > 0){
					pageData.put("studyHoursOrLinkType", "06");
					pageData.put("manageSysCode", "002"); //实验实训管理系统
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}
				
				if(!pageData.containsKey("studyHoursOrLinkType")){ //如果不包含该字段，进行相关处理
					pageData.put("studyHoursOrLinkType", "");
					pageData.put("manageSysCode", "002"); 
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					maxid++;
					pageData.put("mainId", maxid);
					studyHoursResolveService.studyHoursResolve_insert(pageData);
				}

			}

		}
		List<PageData> o_List = new ArrayList<PageData>();
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
}
