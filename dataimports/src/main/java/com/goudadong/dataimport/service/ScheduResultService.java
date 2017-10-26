/**   
* @Title: ScheduResultService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月14日 下午1:52:25 
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
@Service(value="scheduResultService")
public class ScheduResultService {

	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> scheduResultList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ScheduResultMapper.scheduResultList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_scheduResult(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("ScheduResultMapper.o_scheduResult", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("ScheduResultMapper.getMaxId", null);
	}
	
	public int scheduResult_insert(PageData pd) throws Exception{
		return (int) dao.save("ScheduResultMapper.scheduResult_insert", pd);
	}
	
	//获取教师编号
	public PageData getTeacherId(PageData pd) throws Exception{
		return (PageData) dao.findForObject("ScheduResultMapper.getTeacherId", pd);
	}
	
	/**
	 * 获取教学班id
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getTeachClassId(PageData pd) throws Exception {
		return (PageData) dao.findForObject("ScheduResultMapper.getTeachClassId", pd);
	}
	
	/**
	 * 获取校历
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<PageData> findSchoolCalendar(PageData pd) throws Exception {
		return (List<PageData>) dao.findForList("ScheduResultMapper.findSchoolCalendar", pd);
	}
	
	@SuppressWarnings("unchecked")
	public  List<PageData> getMainId(PageData pd) throws Exception {
		return  ( List<PageData>) dao.findForList("ScheduResultMapper.getMainId", pd);
	}
	
	/**
	 * 更新
	 * @param pageData
	 * @throws Exception
	 */
	public void scheduResult_update(PageData pageData) throws Exception {
		dao.update("ScheduResultMapper.scheduResult_update", pageData);
		
	}
	
	/**
	 * 删除
	 * @param pageData
	 * @return 
	 * @throws Exception
	 */
	public int scheduResult_delete(PageData pageData) throws Exception {
		return (int) dao.delete("ScheduResultMapper.scheduResult_delete", pageData);
		
	}
}
