package com.goudadong.dataimport.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.goudadong.dataimport.dao.DaoSupport;
import com.goudadong.dataimport.util.PageData;

/**
 * 
 * @author wulinmin
 *
 */
@Service(value="scheduleMethodService") 
public class ScheduleMethodService {
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	public PageData getMaxId() throws Exception{
		return (PageData)dao.findForObject("ScheduleMethodMapper.getMaxId", null);
	}
	
	public void scheduleMethod_insert(PageData pd) throws Exception{
		dao.save("ScheduleMethodMapper.scheduleMethod_insert", pd);
	}

	public void scheduleMethod_update(PageData pd) throws Exception {
		dao.save("ScheduleMethodMapper.scheduleMethod_update", pd);
		
	}

	public void scheduleMethod_delete(PageData pd) throws Exception {
		dao.delete("ScheduleMethodMapper.scheduleMethod_delete", pd);
	}
	

}
