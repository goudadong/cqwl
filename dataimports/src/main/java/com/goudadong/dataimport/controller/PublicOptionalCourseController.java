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

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.PublicOptionalCourseService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;
import com.goudadong.dataimport.util.SetXnUtil;

/**
 * @author wulinmin
 *
 */
@Controller
@RequestMapping(value="publicOptionalCourse")
public class PublicOptionalCourseController {

	@Resource(name="publicOptionalCourseService")
	private PublicOptionalCourseService publicOptionalCourseService;
	 
	
	@RequestMapping(value="publicOptionalCourseList")
	public ModelAndView publicOptionalCourseList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  publicOptionalCourseService.publicOptionalCourseList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "savePublicOptionalCourse");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="savePublicOptionalCourse")
	public ModelAndView savePublicOptionalCourse() throws Exception {
		List<PageData> list =  publicOptionalCourseService.publicOptionalCourseList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = publicOptionalCourseService.getMaxId();
		int maxid = 0 ;
		if (pdData!=null) {
			Object object = pdData.get("MAX_ID");
			maxid = Integer.parseInt(object.toString());
		}
		for (PageData pageData : list) {		
					
			//处理课程性质
			//kc_flag=0 理论课程   kc_flag=1实践课程
			//对应的课程性质：  01-公选课   02-公共基础课  03-专业基础课   04-专业课  05-实践课
			if (pageData.containsKey("kc_flag")
					&& (pageData.get("kc_flag")+"").trim().equals("0")){ //理论课程

				if (pageData.containsKey("kclb2")
						&&(pageData.get("kclb2")+"").trim().equals("01") //公共课
						&&pageData.containsKey("KCLB")
						&&(pageData.get("KCLB")+"").trim().equals("03")){ //任选课
					pageData.put("courseNature", (pageData.get("kclb2")+"").trim());
					pageData.put("studyWay", "03");
					if (pageData.get("XQ_ID").equals("0")) {
						pageData.put("semester", "一");
					}
					if (pageData.get("XQ_ID").equals("1")) {
						pageData.put("semester", "二");
					}
					SetXnUtil.setXn(pageData);	
					
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
					pageData.put("totalHours", zxs);
					maxid++;
					pageData.put("mainId", maxid);
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					System.err.println("------------开始导入--------------");
					System.err.println(pageData);
					publicOptionalCourseService.publicOptionalCourse_insert(pageData);
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
