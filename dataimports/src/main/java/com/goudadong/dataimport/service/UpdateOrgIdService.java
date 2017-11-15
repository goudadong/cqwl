package com.goudadong.dataimport.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.goudadong.dataimport.dao.DaoSupport;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Service(value="updateOrgIdService")
public class UpdateOrgIdService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	/**
	 * @param orgId
	 * @throws Exception
	 */
	public void update(PageData orgId) throws Exception {
		dao.update("UpdateOrgIdMapper.updateTheoryCourse", orgId);
		dao.update("UpdateOrgIdMapper.updateOpenCourseSchedule", orgId);
		dao.update("UpdateOrgIdMapper.updatePublicOptionalCourse", orgId);
		dao.update("UpdateOrgIdMapper.updateSelectCourseTotal", orgId);
	}
	
	
}
