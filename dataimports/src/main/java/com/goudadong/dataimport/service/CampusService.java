/**   
* @Title: DataImportService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月11日 上午9:41:20 
* @version V1.0   
*/
package com.goudadong.dataimport.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.goudadong.dataimport.dao.DaoSupport;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Service("dataImportService")
public class CampusService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> campusList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("CampusMapper.campusList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_campus(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("CampusMapper.o_campus", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("CampusMapper.getMaxId", null);
	}
	
	public void campus_insert(PageData pd) throws Exception{
		dao.save("CampusMapper.campus_insert", pd);
	}
}
