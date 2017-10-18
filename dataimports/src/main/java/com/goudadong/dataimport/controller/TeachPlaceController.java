/**   
* @Title: TeachPlaceController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 上午9:19:26 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.TeachPlaceService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value="teachPlace")
public class TeachPlaceController {

	@Resource(name="teachPlaceService")
	private TeachPlaceService teachPlaceService;
	 
	
	@RequestMapping(value="teachPlacelist")
	public ModelAndView TeachPlaceList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  teachPlaceService.teachPlaceList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveteachPlace");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="saveteachPlace")
	public ModelAndView saveTeachPlace() throws Exception {
		List<PageData> list =  teachPlaceService.teachPlaceList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = teachPlaceService.getMaxId();
		int maxid = 0;
		if(pdData!=null){
			Object object = pdData.get("MAX_ID");
			maxid = Integer.parseInt(object.toString());
		}
		for (PageData pageData : list) {
			maxid++;
			pageData.put("mainId", maxid);
			pageData.put("ISVALID", 1);
			pageData.put("ISDELETED", 0);
			teachPlaceService.teachPlace_insert(pageData);
		}
		List<PageData> o_List = teachPlaceService.o_teachPlace(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
	
}
