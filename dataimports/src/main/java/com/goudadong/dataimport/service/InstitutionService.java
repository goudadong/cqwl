/**   
* @Title: InstitutionService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 上午10:47:54 
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
@Service(value="institutionService")
public class InstitutionService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> institutionList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("InstitutionMapper.institutionList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_institution(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("InstitutionMapper.o_institution", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("InstitutionMapper.getMaxId", null);
	}
	
	public void institution_insert(PageData pd) throws Exception{
		dao.save("InstitutionMapper.institution_insert", pd);
	}
}
