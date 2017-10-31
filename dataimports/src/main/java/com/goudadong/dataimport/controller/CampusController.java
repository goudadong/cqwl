/**   
* @Title: DataImport.java 
* @Package com.goudadong.dataimport 
* @Description: 数据导入
* @author goudadong
* @date 2017年9月11日 上午9:39:33 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.CampusService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value="campus")
public class CampusController {
	
	@Resource(name="dataImportService")
	private CampusService campusService;
	 
	
	@RequestMapping(value="camplist")
	public ModelAndView CampusList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  campusService.campusList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveCampus");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="saveCampus")
	public ModelAndView saveCampus() throws Exception {
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  campusService.campusList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = campusService.getMaxId();
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
			campusService.campus_insert(pageData);
		}
		List<PageData> o_List = campusService.o_campus(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
	
}
