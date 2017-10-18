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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goudadong.dataimport.service.Hwadee_OpTableService;
import com.goudadong.dataimport.service.OpenCourseScheduleService;
import com.goudadong.dataimport.util.Const;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
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
			try {
				logger.info(pageData.toString());
				pageData.put("opState", 1);
				switch (pageData.getString("opType")) {
				case "update":
					saveData(pageData,1);//保存数据，1表示更新
					hwadee_OpTableService.hwadee_OpTable_update(pageData);
					break;
				case "delete":
					saveData(pageData,2);//保存数据，2表示删除	
					hwadee_OpTableService.hwadee_OpTable_update(pageData);
					break;
				case "insert":
					saveData(pageData,3);//保存数据，3表示插入
					hwadee_OpTableService.hwadee_OpTable_update(pageData);
					break;
				default:
					break;
				}
				
			} catch (Exception e) {
				logger.error("同步出现异常"+e.getMessage());
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
	private void saveData(PageData pData,int flag) throws Exception{
		String []tableMainId = pData.getString("tableMainId").split("\\|");//tableMainId=XN:2001|XQ_ID:0|KCID:000005|xh:374833
		if (tableMainId.length>1) {
			PageData pd =new PageData();
			String tempXn = tableMainId[0].split(":")[1].trim();
			String tempXq = tableMainId[1].split(":")[1].trim();
			String courseCode = tableMainId[2].split(":")[1].trim();
			String xh = tableMainId[3].split(":")[1].trim();
			
			pd.put("xn", tempXn);
			pd.put("xq", tempXq);
			pd.put("kcid", courseCode);
			pd.put("xh", xh);
			
			pd = hwadee_OpTableService.findByColums_plan(pd);
			logger.info("------------>获取到了数据"+pd);
			//切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			logger.info("----------->开始处理数据！");
			try {
				detalData(pd,flag);
				//切换数据库
				DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			} catch (Exception e) {
				//切换数据库
				DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
				logger.error("同步出现异常"+e.getMessage());
				pData.put("opState", -1);
				hwadee_OpTableService.hwadee_OpTable_update(pData);
			}
			logger.info("----------->处理数据结束！");
			
		}
	}


	/**
	 * @throws Exception 
	 * @throws Exception  
	* @Title: detalData 
	* @Description:  处理数据
	* @param pd
	* @param flag  1：更新，2：删除，3：新增
	* @return void    返回类型 
	* @throws 
	*/
	private void detalData(PageData data,int flag) throws Exception {
		PageData pageData = new PageData();
		if (flag==1) {
			pageData =  getOracleData(data);
			openCourseScheduleService.openCourseSchedule_update(pageData);
		}
		if (flag==2) {
			pageData =  getOracleData(data);
			openCourseScheduleService.openCourseSchedule_delete(pageData);
		}
		if (flag==3) {
			pageData =getOracleData(data);
			openCourseScheduleService.openCourseSchedule_insert(pageData);
		}
	}
	
	/** 
	* @Title: update 
	* @Description: 获取更新数据
	* @param pageData
	* @return
	* @throws Exception  参数说明 
	* @return PageData    返回类型 
	* @throws 
	*/
	private PageData getOracleData(PageData pageData) throws Exception{
		
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
		
		try {
			PageData pData = new PageData();
			pData = openCourseScheduleService.getMainId(pageData);
			if(pData!=null){//更新或者删除
				pageData.put("mainId", Integer.parseInt(pData.get("MAINID").toString()));
			}else{//新增
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
