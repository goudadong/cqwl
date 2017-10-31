/**   
* @Title: BuildingController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 上午9:08:47 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.BuildingService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value="building")
public class BuildingController {

	@Resource(name="buildingService")
	private BuildingService buildingService;
	 
	
	@RequestMapping(value="buildlist")
	public ModelAndView BuildList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  buildingService.buildingList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveBuild");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="saveBuild")
	public ModelAndView saveCampus() throws Exception {
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  buildingService.buildingList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = buildingService.getMaxId();
		int maxid = 0 ;
		if (pdData!=null) {
			Object object = pdData.get("MAX_ID");
			maxid = Integer.parseInt(object.toString());
		}
		for (PageData pageData : list) {
			maxid++;
			pageData.put("mainId", maxid);
			pageData.put("ISVALID", 1);
			pageData.put("ISDELETED", 0);
			buildingService.building_insert(pageData);
		}
		List<PageData> o_List = buildingService.o_building(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
	
	@RequestMapping(value="delete")
	@ResponseBody
	public PageData delete(HttpServletRequest request) throws Exception{
		PageData pd = new PageData(request);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
	  	int result = buildingService.deleteAll(pd);
	  	pd.put("total", result);
	  	return pd;
	}
}
