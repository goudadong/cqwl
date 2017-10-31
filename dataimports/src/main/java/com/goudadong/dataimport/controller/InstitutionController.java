/**   
* @Title: InstitutionController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 上午10:48:12 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.InstitutionService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value="institution")
public class InstitutionController {

	@Resource(name="institutionService")
	private InstitutionService institutionService;	
	 
	
	@RequestMapping(value="institutionlist")
	public ModelAndView InstitutionList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  institutionService.institutionList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "saveInstitution");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="saveInstitution")
	public ModelAndView saveInstitution() throws Exception {
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  institutionService.institutionList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = institutionService.getMaxId();
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
			institutionService.institution_insert(pageData);
		}
		List<PageData> o_List = institutionService.o_institution(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
}
