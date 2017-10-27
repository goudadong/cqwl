/**   
* @Title: BuildingService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 上午9:08:29 
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
@Service(value="buildingService")
public class BuildingService {
	
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> buildingList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("BuildingMapper.buildingList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_building(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("BuildingMapper.o_building", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("BuildingMapper.getMaxId", null);
	}
	
	public void building_insert(PageData pd) throws Exception{
		dao.save("BuildingMapper.building_insert", pd);
	}
	
	/**
	 * 删除所有表的数据
	 * @return 
	 * @throws Exception 
	 */
	public int deleteAll(PageData pd) throws Exception {
		return (int) dao.delete("BuildingMapper.deleteAll", pd);
	}
}
