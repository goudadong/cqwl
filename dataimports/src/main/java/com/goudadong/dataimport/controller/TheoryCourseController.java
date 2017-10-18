/**   
* @Title: TheoryCourseController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 下午2:23:04 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.TheoryCourseService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value="theoryCourse")
public class TheoryCourseController {

	@Resource(name="theoryCourseService")
	private TheoryCourseService theoryCourseService;
	 
	
	@RequestMapping(value="theoryCourselist")
	public ModelAndView TheoryCourseList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  theoryCourseService.theoryCourseList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "savetheoryCourse");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="savetheoryCourse")
	public ModelAndView saveTheoryCourse() throws Exception {
		List<PageData> list =  theoryCourseService.theoryCourseList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = theoryCourseService.getMaxId();
		int maxid = 0;
		if(pdData!=null){
			Object object = pdData.get("MAX_ID");
			maxid = Integer.parseInt(object.toString());
		}
		for (PageData pageData : list) {
			maxid++;
			//总学时
			float zxs = 0;
					
			//总学时
			if (pageData.containsKey("ZXS")) {
				zxs += Float.parseFloat(pageData.get("ZXS").toString());
			}
			if (pageData.containsKey("sjxs")) {
				zxs += Float.parseFloat(pageData.get("sjxs").toString());
			}
			if (pageData.containsKey("SYXS")) {
				zxs += Float.parseFloat(pageData.get("SYXS").toString());
			}
			if (pageData.containsKey("qtxs")) {
				zxs += Float.parseFloat(pageData.get("qtxs").toString());
			}
			
			if(pageData.containsKey("DM")){
				//去空格
				String user_kcid = pageData.get("DM").toString().trim();
				pageData.put("DM", user_kcid);
			}
			if(pageData.containsKey("user_kcid")){
				//去空格
				String user_kcid = pageData.get("user_kcid").toString().trim();
				pageData.put("user_kcid", user_kcid);
			}
			if(pageData.containsKey("kclb")){
				//去空格
				String kclbtemp = pageData.get("kclb").toString().trim();
				int kclb = Integer.parseInt(kclbtemp);
				if(kclb==0){
					kclb = 1;
				}else{
					kclb = 2;
				}
				pageData.put("kclb", kclb);
			}
			pageData.put("totalHours", zxs);
			pageData.put("mainId", maxid);
			pageData.put("ISVALID", 1);
			pageData.put("ISDELETED", 0);
			theoryCourseService.theoryCourse_insert(pageData);
		}
		List<PageData> o_List = theoryCourseService.o_theoryCourse(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
}
