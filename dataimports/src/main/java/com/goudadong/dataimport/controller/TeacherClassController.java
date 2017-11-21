/**   
* @Title: TeacherClassController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 下午4:59:32 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.ScheduleMethodService;
import com.goudadong.dataimport.service.TeacherClassService;
import com.goudadong.dataimport.service.TheoryCourseService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value = "teacherClass")
public class TeacherClassController {

	@Resource(name = "teacherClassService")
	private TeacherClassService teacherClassService;

	@Resource(name = "theoryCourseService")
	private TheoryCourseService theoryCourseService;
	
	@Resource(name = "scheduleMethodService")
	private ScheduleMethodService scheduleMethodService;
	
	@RequestMapping(value = "teacherClasslist")
	public ModelAndView TeacherClassList() throws Exception {
		// 切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list = teacherClassService.teacherClassList(null);
	//	List<PageData> list = new ArrayList<PageData>();
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveteacherClass");
		mv.setViewName("index");
		return mv;
	}

	@RequestMapping(value = "saveteacherClass")
	public ModelAndView saveTeacherClass() throws Exception {
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list = teacherClassService.teacherClassList(null);
		// 切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = teacherClassService.getMaxId();
		int maxid = 0;
		if(pdData!=null){
			Object object = pdData.get("MAX_ID");
			maxid = Integer.parseInt(object.toString());
		}
		for (PageData classData : list) {
			maxid++;
			classData.put("mainId", maxid);

			String courseCode = classData.getString("KCDM").trim();
			
			//学期
			if (classData.get("xq_id").equals("0")) {
				classData.put("semester", "1");
			}
			if (classData.get("xq_id").equals("1")) {
				classData.put("semester", "2");
			}

			//课程代码
			classData.put("KCDM", courseCode);
			// 截取班号：如013120-002 ，截取002
			String classCode = classData.get("T_SKBJ").toString().split("\\-")[1]
					.trim();

			classData.put("classCode", classCode);
			// 班级名称=课程名称_班级代码
			PageData courseNamePd = new PageData();
			courseNamePd = teacherClassService.getCourseName(courseCode);
			if (courseNamePd != null && courseNamePd.containsKey("COURSENAME")) {
				String courseName = courseNamePd.getString("COURSENAME");
				classData.put("className", courseName + "_" + classCode);
			} else {
				classData.put("className", classCode);
			}
			
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			classData.put("xn_", classData.getString("xn"));
			List<PageData>  bjdms =  teacherClassService.getBjdm(classData);
			
			for (PageData bjdm : bjdms) { 
				// 切换数据库
				DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
				PageData pdDatas = teacherClassService.getNatureMaxId();
				int naturemaxid = 0 ;
				if(pdDatas != null){
					Object object = pdDatas.get("MAX_ID");
					naturemaxid = Integer.parseInt(object.toString());
				}
				naturemaxid++;
				classData.put("naMainid", naturemaxid);
				classData.put("teachClassId", maxid);
				classData.put("ISVALID", 1); 
				classData.put("ISDELETED", 0);
				if(!teacherClassService.getOrgClassId(bjdm).isEmpty()){
					classData.put("natureClassId", teacherClassService.getOrgClassId(bjdm));
					teacherClassService.insertTeachNature(classData);
				}
			}
			classData.put("reasonCode", "0");
			////班级人数
			//学年
			//SetXnUtil.setXn(classData);
			
			int zxs = 0; //青果总学时
			int zzxs = 0; //青果周学时
			if(classData.containsKey("ZZXS")){
				zzxs = (int)Double.parseDouble(classData.get("ZZXS").toString().trim());
			}
			if(classData.containsKey("ZXS")){
				zxs = (int)Double.parseDouble(classData.get("ZXS").toString().trim());
			}
			 
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			//设置排课系统代码：理论学时不为0则作为理论排课系统"001"，否则，作为实践排课系统"002"
			PageData courseData = theoryCourseService.getCourseByCode(courseCode);
			if(courseData != null){
				//处理学时
				int theoryHours = 0; //文理理论学时
				
				if(courseData.containsKey("THEORYHOURS")){
					theoryHours = Integer.parseInt(courseData.get("THEORYHOURS").toString().trim());
					if(theoryHours >0 ){
						classData.put("manageSysCode", "001"); //理论排课系统
						classData.put("manageHours", theoryHours);//理论排课系统总学时
						//计算理论排课系统周学时
						if(zxs > 0){
							int manageWeekHours = (int)Math.ceil((theoryHours/zxs)*zzxs); //向上取整
							classData.put("manageWeekHours", manageWeekHours);//理论排课系统周学时
						}else {
							classData.put("manageWeekHours", 0);//理论排课系统周学时
						}
						
						//插入理论学时排课方式，实践排课不插入
						scheduleMethod_insert(classData);
					}else {
						classData.put("manageSysCode", "002"); //实践排课系统
						classData.put("manageHours", zxs); //总学时
						classData.put("manageWeekHours", zzxs); //周学时
					}
				}
			}else {
				classData.put("manageSysCode", "001"); //如果没找到该课程，则默认理论排课系统
				classData.put("manageHours", zxs); //总学时
				classData.put("manageWeekHours", zzxs); //周学时
				
				//插入理论学时排课方式，实践排课不插入
				scheduleMethod_insert(classData); 
			}
			


			
			classData.put("ISVALID", 1);
			classData.put("ISDELETED", 0);
			teacherClassService.teacherClass_insert(classData);
			
		}
//		List<PageData> o_List = teacherClassService.o_teacherClass(null);
		List<PageData> o_List = new ArrayList<PageData>();
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}

	/**插入数据到排课方式表
	 * 
	 * @param classData
	 * @throws Exception 
	 */
	public void scheduleMethod_insert(PageData classData) throws Exception {
		PageData scheduleMethodData = new PageData();
		
		PageData object  = scheduleMethodService.getMaxId();
		int maxId = 0;
		if(object!=null){
			maxId=Integer.parseInt(object.get("MAX_ID").toString());
		}
		maxId++;
		scheduleMethodData.put("mainId", maxId);
		scheduleMethodData.put("teacherClassId", classData.get("mainId"));
		scheduleMethodData.put("studyTimeType", "01");//学时类型：理论学时
		scheduleMethodData.put("startWeek", classData.get("KSZ"));
		scheduleMethodData.put("endWeek", classData.get("JSZ"));
		scheduleMethodData.put("assignCode", "");
		scheduleMethodData.put("teachPlaceType", "01"); //教室类型：一般教室
		scheduleMethodData.put("capacity", classData.get("stuNum"));
		scheduleMethodData.put("isValid", "1");
		scheduleMethodData.put("isDeleted", "0");
		scheduleMethodData.put("dataRights", classData.get("manageWeekHours")); ////排课系统周学时--暂时存此字段
		
		
		scheduleMethodService.scheduleMethod_insert(scheduleMethodData);
	}
	
	
	
	
	/**
	 * 更新列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/updatelist")
	public ModelAndView updateTeacherClassList() throws Exception{
		ModelAndView mv =new ModelAndView();
		List<PageData> list = teacherClassService.getUpdateList();
		mv.addObject("list", list);
		mv.addObject("url", "update");
		mv.setViewName("index");
		return mv;
	}
	
	
	@RequestMapping(value="/update")
	public ModelAndView updateTeacherClass() throws Exception{
		ModelAndView mv = new ModelAndView();
		List<PageData> list = teacherClassService.getUpdateList();
		for (PageData pageData : list) {
			//学期
			if (pageData.get("xq_id").equals("0")) {
				pageData.put("semester", "1");
			}
			if (pageData.get("xq_id").equals("1")) {
				pageData.put("semester", "2");
			}
			//学年
			//SetXnUtil.setXn(pageData);
			// 截取班号：如013120-002 ，截取002
			String classCode = pageData.get("T_SKBJ").toString().split("\\-")[1]
					.trim();
			String courseCode = pageData.get("T_SKBJ").toString().split("\\-")[0]
					.trim();

			pageData.put("classCode", classCode);
			pageData.put("courseCode", courseCode);
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			teacherClassService.updateXq(pageData);
		}
		mv.setViewName("success");
		return mv;
	}
	
	
	
}
