/**   
* @Title: Hwadee_OpTableService.java 
* @Package com.goudadong.dataimport.service 
* @Description: 同步数据表
* @author goudadong
* @date 2017年9月29日 上午9:57:57 
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
@Service(value="hwadee_OpTableService")
public class Hwadee_OpTableService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/**
	 * @param pd
	 * @return 同步表数据
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> opTableList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("Hwadee_OptableMapper.opTableList", pd);
	}
	
	/**
	 * 更新同步表
	 * @param pd
	 * @throws Exception
	 */
	public void  hwadee_OpTable_update(PageData pd) throws Exception {
		dao.update("Hwadee_OptableMapper.hwadee_OpTable_update", pd);
	}

	/**
	 * @throws Exception  
	* @Title: findById 
	* @Description: 查询青果数据
	* @param pd
	* @return  参数说明 
	* @return PageData    返回类型 
	* @throws 
	*/
	public PageData findByColums_plan(PageData pd) throws Exception {
		return (PageData)dao.findForObject("Hwadee_OptableMapper.findByColums_plan", pd);
	}

	
	/**
	 * @param pd
	 * @return 查询青果教学班数据
	 * @throws Exception
	 */
	public PageData findByColums_class(PageData pd) throws Exception {
		return (PageData)dao.findForObject("Hwadee_OptableMapper.findByColums_class", pd);
	}
	/**
	 * @param pd
	 * @return 查询青果选课结果表数据
	 * @throws Exception
	 */
	public PageData findByColums_stuselect(PageData pd) throws Exception {
		return (PageData)dao.findForObject("Hwadee_OptableMapper.findByColums_stuselect", pd);
	}

	/**
	 * @param pd
	 * @return 查询青果排课结果表数据
	 * @throws Exception 
	 */
	public PageData findByColums_schedul(PageData pd) throws Exception {
		return (PageData)dao.findForObject("Hwadee_OptableMapper.findByColums_schedul", pd);
	}
	/**
	 * @param pd
	 * @return 查询青果 教学任务表数据
	 * @throws Exception 
	 */
	public PageData findByColums_task(PageData pd) throws Exception {
		return (PageData)dao.findForObject("Hwadee_OptableMapper.findByColums_task", pd);
	}
	/**
	 * @param pd
	 * @return 查询青果 选课结果表数据
	 * @throws Exception 
	 */
	public String  findByColums_beforeStuSelect(PageData beforePd) throws Exception {
		return (String)dao.findForObject("Hwadee_OptableMapper.findByColums_beforeStuSelect", beforePd);
	}
}
