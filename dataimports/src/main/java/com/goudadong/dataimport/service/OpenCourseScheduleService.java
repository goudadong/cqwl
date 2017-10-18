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
 * @author goudadong
 *
 */
@Service(value="openCourseScheduleService")
public class OpenCourseScheduleService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> openCourseScheduleList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("OpenCourseScheduleMapper.openCourseScheduleList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_openCourseSchedule(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("OpenCourseScheduleMapper.o_openCourseSchedule", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("OpenCourseScheduleMapper.getMaxId", null);
	}
	
	//获取专业ORGANIZATIONID
	public PageData getMajorOrgId(String majorCode) throws Exception{
		return (PageData) dao.findForObject("OpenCourseScheduleMapper.getMajorOrgId", majorCode);
	}
	
	//获取学院ORGANIZATIONID
	public PageData getDepartmentOrgId(long majorOrgId) throws Exception{
		return (PageData) dao.findForObject("OpenCourseScheduleMapper.getDepartmentOrgId", majorOrgId);
	}
	
	public void openCourseSchedule_insert(PageData pd) throws Exception{
		dao.save("OpenCourseScheduleMapper.openCourseSchedule_insert", pd);
	}
	/**
	 * @throws Exception  
	* @Title: openCourseSchedule_update 
	* @Description: TODO(这里用一句话描述这个方法的作用) 
	* @param pd  参数说明 
	* @return void    返回类型 
	* @throws 
	*/
	public void openCourseSchedule_update(PageData pd) {
		try {
			dao.update("OpenCourseScheduleMapper.openCourseSchedule_update", pd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * @throws Exception  
	* @Title: openCourseSchedule_delete 
	* @Description:  
	* @param pd  参数说明 
	* @return void    返回类型 
	* @throws 
	*/
	public void openCourseSchedule_delete(PageData pd) throws Exception {
		dao.delete("OpenCourseScheduleMapper.openCourseSchedule_delete", pd);
		
	}
	
	/** 
	* @Title: getMainId 
	* @Description: 获取id
	* @param pd
	* @return
	* @throws Exception  参数说明 
	* @return int    返回类型 
	* @throws 
	*/
	public PageData getMainId(PageData pd) throws Exception{
		return (PageData)dao.findForObject("OpenCourseScheduleMapper.getMainId", pd);
	}
}
