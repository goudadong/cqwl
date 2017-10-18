/**   
* @Title: TeacherClassService.java 
* @Package com.goudadong.dataimport.service 
* @Description: TODO
* @author goudadong
* @date 2017年9月12日 下午4:59:18 
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
@Service(value="teacherClassService")
public class TeacherClassService {

	
	@Resource(name = "daoSupport")
	private DaoSupport dao;
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> teacherClassList(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("TeacherClassMapper.teacherClassList", pd);
	}
	
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> getUpdateList()throws Exception{
		return (List<PageData>)dao.findForList("TeacherClassMapper.getUpdateList", null);
	}
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> o_teacherClass(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("TeacherClassMapper.o_teacherClass", pd);
	}
	
	/*
	*列表(全部)
	*/
	@SuppressWarnings("unchecked")
	public List<PageData> getBjdm(PageData pd)throws Exception{
		return (List<PageData>)dao.findForList("TeacherClassMapper.getBjdm", pd);
	}
	
	
	
	public String  getOrgClassId(PageData pd) throws Exception{
		return (String)dao.findForObject("TeacherClassMapper.getOrgClassId", pd);
	}
	
	
	/**
	 * 班级人数
	 * @param pd
	 * @return 
	 * @throws Exception
	 */
	public  PageData getBjrs(PageData pd) throws Exception{
		return (PageData)dao.findForObject("TeacherClassMapper.getBjrs", pd);
	}
	
	//获取最大id
	public PageData getMaxId() throws Exception{
		return (PageData) dao.findForObject("TeacherClassMapper.getMaxId", null);
	}
	
	//获取课程名称
	public PageData getCourseName(String courseCode) throws Exception{
		return (PageData) dao.findForObject("TeacherClassMapper.getCourseName", courseCode);
	}
	
	public void teacherClass_insert(PageData pd) throws Exception{
		dao.save("TeacherClassMapper.teacherClass_insert", pd);
	}
	
	//获取教师编号
	public PageData getTeacherId(PageData pd) throws Exception{
		return (PageData) dao.findForObject("TeacherClassMapper.getTeacherId", pd);
	}
	
	/**
	 * 更新
	 * @param pageData
	 * @throws Exception
	 */
	public void teacherClass_update(PageData pageData) throws Exception {
		dao.update("TeacherClassMapper.teacherClass_update", pageData);
		
	}
	
	/**
	 * 更新
	 * @param pageData
	 * @throws Exception
	 */
	public void updateXq(PageData pageData) throws Exception {
		dao.update("TeacherClassMapper.updateXq", pageData);
		
	}
	
	/**
	 * 删除
	 * @param data
	 * @throws Exception
	 */
	public void teacherClass_delete(PageData data) throws Exception {
		dao.delete("TeacherClassMapper.teacherClass_delete", data);
		
	}
	public PageData getMainId(PageData pd) throws Exception {
		return  (PageData) dao.findForObject("TeacherClassMapper.getMainId", pd);
	}
}
