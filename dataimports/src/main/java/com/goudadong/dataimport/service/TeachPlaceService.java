/**   
* @Title: TeachPlaceService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 上午9:19:41 
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
@Service(value="teachPlaceService")
public class TeachPlaceService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> teachPlaceList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("TeachPlaceMapper.teachPlaceList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_teachPlace(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("TeachPlaceMapper.o_teachPlace", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("TeachPlaceMapper.getMaxId", null);
	}
	
	public void teachPlace_insert(PageData pd) throws Exception{
		dao.save("TeachPlaceMapper.teachPlace_insert", pd);
	}
	
	public void classroom_insert(PageData pd) throws Exception {
		dao.save("TeachPlaceMapper.classroom_insert", pd);
	}
	
	public void gymnasium_insert(PageData pd) throws Exception {
		dao.save("TeachPlaceMapper.gymnasium_insert", pd);
	}
	
	public void labBranch_insert(PageData pd) throws Exception {
		dao.save("TeachPlaceMapper.labBranch_insert", pd);
	}
	
	public void updateSchedleMethod(PageData pd) throws Exception{
		dao.update("TeachPlaceMapper.updateSchedleMethod", pd);
	}
}
