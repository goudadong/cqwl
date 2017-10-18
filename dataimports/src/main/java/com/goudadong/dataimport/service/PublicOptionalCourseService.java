/**   
* @Title: OpenCourseScheduleService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 下午3:58:10 
* @version V1.0   
*/
package com.goudadong.dataimport.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.goudadong.dataimport.dao.DaoSupport;
import com.goudadong.dataimport.util.PageData;

/**
 * @author wulinmin
 *
 */
@Service(value="publicOptionalCourseService")
public class PublicOptionalCourseService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> publicOptionalCourseList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("PublicOptionalCourseMapper.publicOptionalCourseList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_publicOptionalCourse(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("PublicOptionalCourseMapper.o_publicOptionalCourse", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("PublicOptionalCourseMapper.getMaxId", null);
	}
	
	public void publicOptionalCourse_insert(PageData pd) throws Exception{
		dao.save("PublicOptionalCourseMapper.publicOptionalCourse_insert", pd);
	}
}
