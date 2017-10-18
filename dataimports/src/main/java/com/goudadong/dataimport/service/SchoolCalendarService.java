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
@Service("schoolCalendarService")
public class SchoolCalendarService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> schoolCalendarList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("SchoolCalendarMapper.schoolCalendarList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_schoolCalendar(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("SchoolCalendarMapper.o_schoolCalendar", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("SchoolCalendarMapper.getMaxId", null);
	}
	
	public void schoolCalendar_insert(PageData pd) throws Exception{
		dao.save("SchoolCalendarMapper.schoolCalendar_insert", pd);
	}
}
