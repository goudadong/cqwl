/**   
* @Title: StudentSelectResultController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月13日 上午11:01:19 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.StudentSelectResultService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;
import com.goudadong.dataimport.util.SetXnUtil;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value="studentSelectResult")
public class StudentSelectResultController {
	@Resource(name="studentSelectResultService")
	private StudentSelectResultService studentSelectResultService;
	 
	
	@RequestMapping(value="studentSelectResultlist")
	public ModelAndView studentSelectResultList() throws Exception{
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
//		List<PageData> list =  studentSelectResultService.studentSelectResultList(null);
		List<PageData> list = new ArrayList<PageData>();  //数据量太大，不前端显示
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "savestudentSelectResult");
		mv.setViewName("index");
		return mv;
	}
	@RequestMapping(value="savestudentSelectResult")
	public ModelAndView savestudentSelectResult() throws Exception {
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list =  studentSelectResultService.studentSelectResultList(null);
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = studentSelectResultService.getMaxId();
		int maxid = 0 ;
		if(pdData != null){
			Object object = pdData.get("MAX_ID");
	        maxid = Integer.parseInt(object.toString());
		}
		for (PageData pageData : list) {
			maxid++;
			//设置班号
			String classCode = pageData.get("KC_ID").toString().split("\\-")[1].trim();
			if (pageData.get("xq_id").equals("0")) {
				pageData.put("semester", "一");
			}
			if (pageData.get("xq_id").equals("1")) {
				pageData.put("semester", "二");
			}
			pageData.put("classCode",classCode);
			//定义获取教学班id的主键
			PageData pData = new PageData();
			SetXnUtil.setXn(pageData);
			pData.put("xn", pageData.getString("xn").trim());
			pData.put("xq", pageData.getString("semester").trim());
			pData.put("kcid", pageData.getString("kcid").trim());
			pData.put("classCode", pageData.getString("classCode").trim());
			//定义获取教学班id
			PageData pd = new PageData();
			pd = studentSelectResultService.getTeachClassId(pData);
			if (null!=pd) {
				long teachClassId = Long.parseLong(pd.get("MAINID").toString());
				pageData.put("teachClassId", teachClassId);
				pageData.put("mainId", maxid);
				pageData.put("ISVALID", 1);
				pageData.put("ISDELETED", 0);
				System.err.println("------------开始导入--------------");
				System.err.println(pageData);
				studentSelectResultService.studentSelectResult_insert(pageData);
			}
		}
		List<PageData> o_List = studentSelectResultService.o_studentSelectResult(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
}
