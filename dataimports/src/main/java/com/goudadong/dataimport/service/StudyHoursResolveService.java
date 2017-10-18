package com.goudadong.dataimport.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.goudadong.dataimport.dao.DaoSupport;
import com.goudadong.dataimport.util.PageData;

/**
 * 
 * @author wulinmin
 *
 */
@Service("studyHoursResolveService")
public class StudyHoursResolveService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*普通开课计划列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_openCourseSchedule(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("OpenCourseScheduleMapper.o_openCourseSchedule", pd);
	}
	/*
	*公选课开课计划列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_publicOptionalCourse(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("PublicOptionalCourseMapper.o_publicOptionalCourse", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_studyHoursResolve(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StudyHoursResolveMapper.o_studyHoursResolve", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("StudyHoursResolveMapper.getMaxId", null);
	}
	
	public void studyHoursResolve_insert(PageData pd) throws Exception{
		dao.save("StudyHoursResolveMapper.studyHoursResolve_insert", pd);
	}
	

}
