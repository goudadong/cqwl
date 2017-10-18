/**   
* @Title: SelectCourseTotalController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月22日 下午5:27:52 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.SelectCourseTotalService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value="courseTotal")
public class SelectCourseTotalController {

	

	@Resource(name="selectCourseTotalService")
	private SelectCourseTotalService selectCourseTotalService;
	 
	
	@RequestMapping(value="list")
	public ModelAndView CampusList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		List<PageData> list =  selectCourseTotalService.selectOpenCourseList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveCourse");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="saveCourse")
	
	public ModelAndView saveCourse() throws Exception {
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		List<PageData> list1 =  selectCourseTotalService.selectOpenCourseList(null);
		List<PageData> list2 =  selectCourseTotalService.selectPublicCourseList(null);
		PageData p1 = selectCourseTotalService.getMaxId();
		int maxid1 = 1;
		if (p1!=null) {
			Object o1 = p1.get("max_Id");
			maxid1 = Integer.parseInt(o1.toString());
		}
		for (PageData pageData : list1) {
			maxid1++;
			pageData.put("mainId", maxid1);
			pageData.put("ISVALID", 1);
			pageData.put("ISDELETED", 0);
			selectCourseTotalService.selectOpen_insert(pageData);
		}
		for (PageData pageData : list2) {
			maxid1++;
			pageData.put("mainId", maxid1);
			pageData.put("ISVALID", 1);
			pageData.put("ISDELETED", 0);
			selectCourseTotalService.selectPublic_insert(pageData);
		}
		ModelAndView mv = new ModelAndView();
		mv.setViewName("success");
		return mv;
	}
	
}
