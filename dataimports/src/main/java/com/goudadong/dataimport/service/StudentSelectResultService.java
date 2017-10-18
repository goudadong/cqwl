/**   
* @Title: StudentSelectResultService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月13日 上午11:01:04 
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
@Service(value="studentSelectResultService")
public class StudentSelectResultService {

	
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> studentSelectResultList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StudentSelectResultMapper.studentSelectResultList", pd);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_studentSelectResult(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("StudentSelectResultMapper.o_studentSelectResult", pd);
	}
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("StudentSelectResultMapper.getMaxId", null);
	}
	
	public void studentSelectResult_insert(PageData pd) throws Exception{
		dao.save("StudentSelectResultMapper.studentSelectResult_insert", pd);
	}
	
	/**
	 * 获取教学班id
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public PageData getTeachClassId(PageData pd) throws Exception {
		return (PageData) dao.findForObject("StudentSelectResultMapper.getTeachClassId", pd);
	}
	
	public PageData getMainId(PageData pd) throws Exception {
		return  (PageData) dao.findForObject("StudentSelectResultMapper.getMainId", pd);
	}
	
	/**
	 * 更新
	 * @param pageData
	 * @throws Exception
	 */
	public void studentSelectResult_update(PageData pageData) throws Exception {
		dao.update("StudentSelectResultMapper.studentSelectResult_update", pageData);
		
	}
	/**
	 * 删除
	 * @param data
	 * @throws Exception
	 */
	public void studentSelectResult_delete(PageData pageData) throws Exception {
		dao.delete("StudentSelectResultMapper.studentSelectResult_delete", pageData);
		
	}
}
