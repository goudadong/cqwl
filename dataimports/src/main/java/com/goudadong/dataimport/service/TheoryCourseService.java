/**   
* @Title: TheoryCourseService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 下午2:22:53 
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
@Service(value="theoryCourseService")
public class TheoryCourseService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> theoryCourseList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("TheoryCourseMapper.theoryCourseList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_theoryCourse(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("TheoryCourseMapper.o_theoryCourse", pd);
	}
	
	/**
	 * 根据代码获取课程信息
	 * @param courseCode
	 * @return
	 * @throws Exception
	 */
	public PageData getCourseByCode(String courseCode) throws Exception{
		return (PageData) dao.findForObject("TheoryCourseMapper.getCourseByCode", courseCode);
	}
	
	/**
	 * 获取最大id
	 * @return
	 * @throws Exception
	 */
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("TheoryCourseMapper.getMaxId", null);
	}
	
	public void theoryCourse_insert(PageData pd) throws Exception{
		dao.save("TheoryCourseMapper.theoryCourse_insert", pd);
	}
}
