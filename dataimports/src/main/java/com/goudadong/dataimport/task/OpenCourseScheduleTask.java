/**   
* @Title: OpenCourseScheduleTask.java 
* @Package com.goudadong.dataimport.task 
* @Description: 开课计划同步任务
* @author goudadong
* @date 2017年9月29日 上午10:25:04 
* @version V1.0   
*/
package com.goudadong.dataimport.task;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goudadong.dataimport.service.Hwadee_OpTableService;
import com.goudadong.dataimport.service.OpenCourseScheduleService;
import com.goudadong.dataimport.util.Const;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.InstitutionUtil;
import com.goudadong.dataimport.util.PageData;
import com.goudadong.dataimport.util.PropertiesUtil;
import com.goudadong.dataimport.util.SetXnUtil;



/**
 * @author goudadong
 *
 */
public class OpenCourseScheduleTask {
	
	//日志
	private Logger logger = LoggerFactory.getLogger(OpenCourseScheduleTask.class);
	//同步标志
	private static boolean syncFlag = true;
	@Resource(name="hwadee_OpTableService")
	private Hwadee_OpTableService hwadee_OpTableService;
	@Resource(name="openCourseScheduleService")
	private OpenCourseScheduleService openCourseScheduleService;
	private PageData pd =new PageData();
	
	/**
	 * 同步操作
	 */  
	public void job() {
		synchronized (this) {
			if (syncFlag) {
				logger.info("-------------------->开始同步");
				syncFlag = false;
				try {
					//切换数据库
					DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
					pd.put("tableName", PropertiesUtil.getValueByKey(Const.SYSTEM_PROPERTIES_CONST, "T_1"));
					SyncData(hwadee_OpTableService.opTableList(pd));
				} catch (Exception e) {
					logger.error(MessageFormat.format("同步失败", e.getMessage()));
				}
				logger.info("-------------------->同步结束");
				syncFlag = true;
			}
		}
	}
	
	
	/**
	 * @throws Exception  
	* @Title: SyncData 
	* @Description: 同步数据
	* @param list  参数说明 
	* @return void    返回类型 
	* @throws 
	*/
	private void SyncData(List<PageData> list) throws Exception{
		for (PageData pageData : list) {
			int result =0 ;
			try {
				logger.info(pageData.toString());
				switch (pageData.getString("opType")) {
				case "update":
					result = saveData(pageData,1);//保存数据，1表示更新
					break;
				case "delete":
					result = saveData(pageData,2);//保存数据，2表示删除	
					break;
				case "insert":
					result = saveData(pageData,3);//保存数据，3表示插入
					break;
				default:
					break;
				}
				if(result==0){
					logger.error("受影响行数为0,同步失败！同步数据："+pageData);
					pageData.put("opState", -1);
					hwadee_OpTableService.hwadee_OpTable_update(pageData);
				}if(result>0){
					pageData.put("opState", 1);
					hwadee_OpTableService.hwadee_OpTable_update(pageData);
					logger.error("受影响行数为"+result+",同步成功！");
				}
				
			} catch (Exception e) {
				logger.error("同步出现异常"+e.getMessage()+"同步数据："+pageData);
				pageData.put("opState", -1);
				hwadee_OpTableService.hwadee_OpTable_update(pageData);
			}
		}
	}
	
	/** 
	* @Title: saveData 
	* @Description: 保存数据
	* @param pData
	* @param flag 1：更新，2：删除，3：新增
	* @throws Exception  参数说明 
	* @return void    返回类型 
	* @throws 
	*/
	private int saveData(PageData pData,int flag) throws Exception{
		int result = 0;
		try {
			String []tableMainId = pData.getString("tableMainId").split("\\|");//修改之后的数据
			String []tableMainIdNew = {};
			if(pData.containsKey("tableMainIdNew")){
				tableMainIdNew= pData.getString("tableMainIdNew").split("\\|");//修改之前的数据
			}
			//变之后的数据
			PageData pd =new PageData();
			PageData findPd =null;
			if (tableMainId.length>1) {
				String tempXn = tableMainId[0].split(":")[1].trim();
				String tempXq = tableMainId[1].split(":")[1].trim();
				String courseCode = tableMainId[2].split(":")[1].trim();
				String xh = tableMainId[3].split(":")[1].trim();
				String zy_id = tableMainId[4].split(":")[1].trim();
				String nj = tableMainId[5].split(":")[1].trim();
				
				pd.put("xn", tempXn);
				pd.put("xq", tempXq);
				pd.put("kcid", courseCode);
				pd.put("xh", xh);
				pd.put("ZY_ID", zy_id);
				pd.put("NJ", nj);
				findPd = hwadee_OpTableService.findByColums_plan(pd);
			}
			//变之前的数据
			PageData beforePd = new PageData();
			if(tableMainIdNew.length>1){
				String tempXn = tableMainIdNew[0].split(":")[1].trim();
				String tempXq = tableMainIdNew[1].split(":")[1].trim();
				String courseCode = tableMainIdNew[2].split(":")[1].trim();
				String xh = tableMainIdNew[3].split(":")[1].trim();
				String zy_id = tableMainIdNew[4].split(":")[1].trim();
				String nj = tableMainIdNew[5].split(":")[1].trim();
				
				beforePd.put("xn", tempXn);
				beforePd.put("xq", tempXq);
				beforePd.put("kcid", courseCode);
				beforePd.put("xh", xh);
				beforePd.put("ZY_ID", zy_id);
				beforePd.put("NJ", nj);
			}
			logger.info("------------>获取到了数据"+findPd);
			//切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			logger.info("----------->开始处理数据！");
			if(findPd!=null && flag!=2){
				result = detalData(findPd,beforePd,flag);
			}else if(flag==2){
				result = detalData(pd,beforePd,flag);
			}else{
				logger.info("在更新或者新增时没有查到源数据，请排查！查询字段："+pd);
			}
			//切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("----------->处理数据结束！");
			
		return result;
	}


	/**
	 * @param beforePd 
	 * @throws Exception 
	 * @throws Exception  
	* @Title: detalData 
	* @Description:  处理数据
	* @param pd
	* @param flag  1：更新，2：删除，3：新增
	* @return void    返回类型 
	* @throws 
	*/
	private int  detalData(PageData data,PageData beforePd, int flag)  {
		PageData pageData = new PageData();
		int result = 0;
		try {
			if (flag==1) {
				pageData =  getOracleData(data,beforePd);
				result = openCourseScheduleService.openCourseSchedule_update(pageData);
				logger.info("受影响行数"+result+",执行方法openCourseSchedule_update");
			}
			if (flag==2) {
				pageData =  getDelOracleData(data);
				result = openCourseScheduleService.openCourseSchedule_delete(pageData);
				logger.info("受影响行数"+result+",执行方法openCourseSchedule_delete");
			}
			if (flag==3) {
				pageData =getOracleData(data,beforePd);
				result = openCourseScheduleService.openCourseSchedule_insert(pageData);
				logger.info("受影响行数"+result+",执行方法openCourseSchedule_insert");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取删除的数据
	 * @param pagedata
	 * @return
	 * @throws Exception 
	 */
	private PageData getDelOracleData(PageData pageData) throws Exception {
		if (pageData.getString("xq").equals("0")) {
			pageData.put("semester", "一");
		}
		if (pageData.getString("xq").equals("1")) {
			pageData.put("semester", "二");
		}
		SetXnUtil.setXn(pageData);
		pageData.put("KCID", pageData.getString("kcid"));
		String majorCode = (pageData.get("ZY_ID") + "").trim();
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData majorData = openCourseScheduleService.getMajorOrgId(majorCode);
		long majorOrgId = 0;
		if (majorData != null && majorData.containsKey("ORGANIZATIONID")) {
			majorOrgId = Long.parseLong(majorData.get("ORGANIZATIONID")+"");
			pageData.put("majorOrgId", majorOrgId + "");
		} else {
			pageData.put("majorOrgId", "");
		}
		PageData pData = openCourseScheduleService.getMainId(pageData);
		if(pData!=null){//删除
			pageData.put("mainId", Integer.parseInt(pData.get("MAINID").toString()));
		}else{
			pageData=null;
		}
		return pageData;
	}


	/**
	 * @param beforePd  
	* @Title: update 
	* @Description: 获取更新数据
	* @param pageData
	* @return
	* @throws Exception  参数说明 
	* @return PageData    返回类型 
	* @throws 
	*/
	private PageData getOracleData(PageData pageData, PageData beforePd) throws Exception{
		
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		if (pageData.containsKey("ZY_ID")) {
			// 专业OrgId
			String majorCode = (pageData.get("ZY_ID") + "").trim();
			PageData majorData = openCourseScheduleService
					.getMajorOrgId(majorCode);
			long majorOrgId = 0;
			if (majorData != null && majorData.containsKey("ORGANIZATIONID")) {
				majorOrgId = Long.parseLong(majorData.get("ORGANIZATIONID")+"");
				pageData.put("majorOrgId", majorOrgId + "");
			} else {
				pageData.put("majorOrgId", "");
			}
			// 学院OrgId						
			PageData departmentData = openCourseScheduleService
					.getDepartmentOrgId(majorOrgId);
			long departmentOrgId = 0;
			if (departmentData != null && departmentData.containsKey("PARENTORGANIZATIONID")) {
				departmentOrgId = Long.parseLong(departmentData
						.get("PARENTORGANIZATIONID")+"");
				pageData.put("departmentOrgId", departmentOrgId + "");
			} else {
				pageData.put("departmentOrgId", "");
			}
		}else {
			pageData.put("majorOrgId", "");
			pageData.put("departmentOrgId", "");
			
		}
		
		
		//处理课程性质
		//kc_flag=0 理论课程   kc_flag=1实践课程
		//对应的课程性质：  01-公选课   02-公共基础课  03-专业基础课   04-专业课  05-实践课
		if (!pageData.containsKey("kc_flag")){
			pageData.put("courseNature", "");
		}else if((pageData.get("kc_flag")+"").trim().equals("1")) { //实践课程
			pageData.put("courseNature", "05");
		}else if ((pageData.get("kc_flag")+"").trim().equals("0")) { //理论课程
			if (!pageData.containsKey("kclb2")){
				pageData.put("courseNature", "");
			}else {
				pageData.put("courseNature", (pageData.get("kclb2")+"").trim());
			}
		}else {
			pageData.put("courseNature", "");
		}
		
		//处理选修方式
		if (!pageData.containsKey("KCLB")||pageData.get("KCLB").toString().isEmpty()){
			pageData.put("studyWay", "01");
		}else  {
			pageData.put("studyWay", (pageData.get("KCLB")+"").trim());
		}
		
		
		if (pageData.get("XQ_ID").equals("0")) {
			pageData.put("semester", "一");
		}
		if (pageData.get("XQ_ID").equals("1")) {
			pageData.put("semester", "二");
		}
		SetXnUtil.setXn(pageData);
		//总学时
		float zxs = 0;
		//总学时
		if (pageData.containsKey("ZXS")) {
			zxs += Float.parseFloat(pageData.get("ZXS").toString());
		} 
		if (pageData.containsKey("SJXS")) {
			zxs += Float.parseFloat(pageData.get("SJXS").toString());
		}
		if (pageData.containsKey("SYXS")) {
			zxs += Float.parseFloat(pageData.get("SYXS").toString());
		}
		if (pageData.containsKey("QTXS")) {
			zxs += Float.parseFloat(pageData.get("QTXS").toString());
		}
		pageData.put("totalHours", zxs);
		InstitutionUtil inUtil = new InstitutionUtil();
		Map<String, String> map = inUtil.map;//承担单位对应的组织结构代码
		String orgId = "";
		if(pageData.containsKey("CDDW_ID")){
			String dm = pageData.getString("CDDW_ID");
			orgId = map.get(dm);
		}
		pageData.put("CDDW_ID", orgId);
		
		try {
			PageData pData = null;
			if(!beforePd.isEmpty()){//判断是否为空，为空的情况为新增的情况
				pData = getDelOracleData(beforePd);
				if(pData!=null){//更新或者删除
					pageData.put("mainId", Integer.parseInt(pData.get("mainId").toString()));
				}
			}
			else{//新增
				PageData pdData = openCourseScheduleService.getMaxId();
				int maxid = 0 ;
				if(pdData != null){
					Object object = pdData.get("MAX_ID");
					maxid = Integer.parseInt(object.toString());
				}
				maxid++;
				pageData.put("mainId", maxid);
				pageData.put("ISVALID", 1);
				pageData.put("ISDELETED", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return pageData;
		
	}
	
	
}
