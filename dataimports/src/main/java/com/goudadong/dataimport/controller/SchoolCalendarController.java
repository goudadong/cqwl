package com.goudadong.dataimport.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.SchoolCalendarService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;
import com.goudadong.dataimport.util.SetXnUtil;

/**
 * @author wulinmin
 *
 */
@Controller
@RequestMapping(value="schoolCalendar")
public class SchoolCalendarController {
	@Resource(name="schoolCalendarService")
	private SchoolCalendarService schoolCalendarService;
	
	@RequestMapping(value="schoolCalendarList")
	public ModelAndView schoolCalendarList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  schoolCalendarService.schoolCalendarList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveSchoolCalendar");
		mv.setViewName("index");
		return mv;
	}
	
	@RequestMapping(value="saveSchoolCalendar")
	public ModelAndView saveSchoolCalendar() throws Exception {
		List<PageData> list =  schoolCalendarService.schoolCalendarList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = schoolCalendarService.getMaxId();
		int maxid = 0 ;
		if(pdData != null){
			Object object = pdData.get("MAX_ID");
	        maxid = Integer.parseInt(object.toString());
		}
		for (PageData pageData : list) {
			maxid++;
			pageData.put("mainId", maxid);
			//转换学期
			if ((pageData.get("xq_id")+"").trim().equals("0")) {
				pageData.put("semester", "一");
			}
			if ((pageData.get("xq_id")+"").trim().equals("1")) {
				pageData.put("semester", "二");
			}
			//设置学年
			SetXnUtil.setXn(pageData);
			pageData.put("ISVALID", 1);
			pageData.put("ISDELETED", 0);
			if (!pageData.containsKey("jq_start")) {
				pageData.put("jq_start", "");
			}
			if (!pageData.containsKey("jq_end")) {
				pageData.put("jq_end", "");
			}
			System.err.println("------------开始导入--------------");
			System.err.println(pageData);
			
			schoolCalendarService.schoolCalendar_insert(pageData);
		}
		List<PageData> o_List = schoolCalendarService.o_schoolCalendar(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
	

}
