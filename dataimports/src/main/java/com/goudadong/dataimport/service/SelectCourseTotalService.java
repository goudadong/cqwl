/**   
* @Title: SelectCourseTotalService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月22日 下午5:22:56 
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
@Service(value="selectCourseTotalService")
public class SelectCourseTotalService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> selectOpenCourseList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("SelectCourseTotalMapper.selectOpenCourseList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> selectPublicCourseList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("SelectCourseTotalMapper.selectPublicCourseList", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("SelectCourseTotalMapper.getMaxId", null);
	}
		
	
	/**
	 * 开课计划
	 * @param pd
	 * @throws Exception
	 */
	public void selectOpen_insert(PageData pd) throws Exception{
		dao.save("SelectCourseTotalMapper.selectOpen_insert", pd);
	}
	

	/**
	 * 公共课
	 * @param pd
	 * @throws Exception
	 */
	public void selectPublic_insert(PageData pd) throws Exception{
		dao.save("SelectCourseTotalMapper.selectPublic_insert", pd);
	}
	
	
}
