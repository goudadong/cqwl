/**   
* @Title: OpenCourseScheduleController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 下午3:58:23 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.OpenCourseScheduleService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.InstitutionUtil;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value="openCourseSchedule")
public class OpenCourseScheduleController {

	@Resource(name="openCourseScheduleService")
	private OpenCourseScheduleService openCourseScheduleService;
	public List<PageData> list = new ArrayList<PageData>();
	
	@RequestMapping(value="openCourseSchedulelist")
	public ModelAndView OpenCourseScheduleList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		list =  openCourseScheduleService.openCourseScheduleList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveopenCourseSchedule");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="saveopenCourseSchedule")
	public ModelAndView saveOpenCourseSchedule() throws Exception {
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  openCourseScheduleService.openCourseScheduleList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = openCourseScheduleService.getMaxId();
		int maxid = 0 ;
		if (pdData!=null) {
			Object object = pdData.get("MAX_ID");
			maxid = Integer.parseInt(object.toString());
		}
		for (PageData pageData : list) {
			maxid++;
			
			if (pageData.containsKey("ZY_ID")) {
				// 专业OrgId
				String majorCode = (pageData.get("ZY_ID") + "").trim();
				PageData majorData = openCourseScheduleService
						.getMajorOrgId(majorCode);
				long majorOrgId = 0;
				if (majorData != null && majorData.containsKey("ORGANIZATIONID")) {
					majorOrgId = Long.parseLong(majorData.get("ORGANIZATIONID")+"");
					pageData.put("majorOrgId", majorOrgId + "");
				} else {
					pageData.put("majorOrgId", "");
				}
				// 学院OrgId						
				PageData departmentData = openCourseScheduleService
						.getDepartmentOrgId(majorOrgId);
				long departmentOrgId = 0;
				if (departmentData != null && departmentData.containsKey("PARENTORGANIZATIONID")) {
					departmentOrgId = Long.parseLong(departmentData
							.get("PARENTORGANIZATIONID")+"");
					pageData.put("departmentOrgId", departmentOrgId + "");
				} else {
					pageData.put("departmentOrgId", "");
				}
			}else {
				pageData.put("majorOrgId", "");
				pageData.put("departmentOrgId", "");
				
			}
			
			
			//处理课程性质
			//kc_flag=0 理论课程   kc_flag=1实践课程
			//对应的课程性质：  01-公选课   02-公共基础课  03-专业基础课   04-专业课  05-实践课
			if (!pageData.containsKey("kc_flag")){
				pageData.put("courseNature", "");
			}else if((pageData.get("kc_flag")+"").trim().equals("1")) { //实践课程
				pageData.put("courseNature", "05");
			}else if ((pageData.get("kc_flag")+"").trim().equals("0")) { //理论课程
				if (!pageData.containsKey("kclb2")){
					pageData.put("courseNature", "");
				}else {
					pageData.put("courseNature", (pageData.get("kclb2")+"").trim());
				}
			}else {
				pageData.put("courseNature", "");
			}
			
			//处理选修方式
			if (!pageData.containsKey("KCLB")){
				pageData.put("studyWay", "01");
			}else {
				pageData.put("studyWay", (pageData.get("KCLB")+"").trim());
			}
			
			
			if (pageData.get("XQ_ID").equals("0")) {
				pageData.put("semester", "1");
			}
			if (pageData.get("XQ_ID").equals("1")) {
				pageData.put("semester", "2");
			}
			//SetXnUtil.setXn(pageData);
			//总学时
			float zxs = 0;
			//总学时
			if (pageData.containsKey("ZXS")) {
				zxs += Float.parseFloat(pageData.get("ZXS").toString());
			}
			if (pageData.containsKey("SJXS")) {
				zxs += Float.parseFloat(pageData.get("SJXS").toString());
			}
			if (pageData.containsKey("SYXS")) {
				zxs += Float.parseFloat(pageData.get("SYXS").toString());
			}
			if (pageData.containsKey("QTXS")) {
				zxs += Float.parseFloat(pageData.get("QTXS").toString());
			}
			
			InstitutionUtil inUtil = new InstitutionUtil();
			Map<String, String> map = inUtil.map;//承担单位对应的组织结构代码
			String orgId = "";
			if(pageData.containsKey("CDDW_ID")){
				String dm = pageData.getString("CDDW_ID");
				orgId = map.get(dm);
			}
			pageData.put("CDDW_ID", orgId);
			pageData.put("totalHours", zxs);
			pageData.put("mainId", maxid);
			pageData.put("ISVALID", 1);
			pageData.put("ISDELETED", 0);
			openCourseScheduleService.openCourseSchedule_insert(pageData);
		}
		List<PageData> o_List = openCourseScheduleService.o_openCourseSchedule(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
}
