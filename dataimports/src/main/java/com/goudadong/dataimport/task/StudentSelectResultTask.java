package com.goudadong.dataimport.task;

import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goudadong.dataimport.service.Hwadee_OpTableService;
import com.goudadong.dataimport.service.StudentSelectResultService;
import com.goudadong.dataimport.util.Const;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;
import com.goudadong.dataimport.util.PropertiesUtil;
import com.goudadong.dataimport.util.SetXnUtil;

public class StudentSelectResultTask {
	
	
	//日志
	private Logger logger = LoggerFactory.getLogger(StudentSelectResultTask.class);
	//同步标志
	private static boolean syncFlag = true;
	@Resource(name="hwadee_OpTableService")
	private Hwadee_OpTableService hwadee_OpTableService;
	@Resource(name="studentSelectResultService")
	private StudentSelectResultService studentSelectResultService;
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
					pd.put("tableName", PropertiesUtil.getValueByKey(Const.SYSTEM_PROPERTIES_CONST, "T_3"));
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
		String []tableMainIds = pData.getString("tableMainId").split("\\#");//tableMainId=XN:2019|XQ_ID:1|KCID:132292-002|XS_ID:200900003427#XN:2009|XQ_ID:1|KCID:132292-002|XS_ID:200900003427
		String [] tableMainId = tableMainIds[0].split("\\|");//修改之后的数据
		String [] beforMainId = tableMainIds[1].split("\\|");//修改之前的数据
		
		if (tableMainId.length>1) {
			PageData pd =new PageData();
			//变之后的数据
			String tempXn = tableMainId[0].split(":")[1].trim();
			String tempXq = tableMainId[1].split(":")[1].trim();
			String courseCode = tableMainId[2].split(":")[1].trim();
			String xh = tableMainId[3].split(":")[1].trim();
			
			pd.put("xn", tempXn);
			pd.put("xq", tempXq);
			pd.put("kcid", courseCode);
			pd.put("xh", xh);
			pd = hwadee_OpTableService.findByColums_stuselect(pd);
			//变之前的数据
			String beforeXn = beforMainId[0].split(":")[1].trim();
			String beforeXq = beforMainId[1].split(":")[1].trim();
			String beforecourseCode = beforMainId[2].split(":")[1].trim();
			String beforexh = beforMainId[3].split(":")[1].trim();
			PageData beforePd = new PageData();
			beforePd.put("xn", beforeXn);
			beforePd.put("xq", beforeXq);
			beforePd.put("kcid", beforecourseCode);
			beforePd.put("xh", beforexh);
			
			String o_xh = hwadee_OpTableService.findByColums_beforeStuSelect(beforePd);
			beforePd.put("studentNo", o_xh);
			logger.info("------------>获取到了数据"+pd);
			//切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			logger.info("----------->开始处理数据！");
			try {
				detalData(pd,beforePd,flag);
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
	private void detalData(PageData data,PageData beforePd,int flag) throws Exception {
		PageData pageData = new PageData();
		if (flag==1) {
			pageData =  getOracleData(data,beforePd);
			studentSelectResultService.studentSelectResult_update(pageData);
		}
		if (flag==2) {
			pageData =  getOracleData(data,beforePd);
			studentSelectResultService.studentSelectResult_delete(pageData);
		}
		if (flag==3) {
			pageData =getOracleData(data,beforePd);
			studentSelectResultService.studentSelectResult_insert(pageData);
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
	private PageData getOracleData(PageData pageData,PageData beforePd) throws Exception{
		
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		//设置班号
		String classCode = pageData.get("KC_ID").toString().split("\\-")[1].trim();
		if (pageData.get("xq_id").equals("0")) {
			pageData.put("semester", "一");
		}
		if (pageData.get("xq_id").equals("1")) {
			pageData.put("semester", "二");
		}
		pageData.put("classCode",classCode);
		//定义获取教学班id的主键
		PageData pData = new PageData();
		SetXnUtil.setXn(pageData);
		pData.put("xn", pageData.getString("xn").trim());
		pData.put("xq", pageData.getString("semester").trim());
		pData.put("kcid", pageData.getString("kcid").trim());
		pData.put("classCode", pageData.getString("classCode").trim());
		//定义获取教学班id
		PageData pd = new PageData();
		pd = studentSelectResultService.getTeachClassId(pData);
		if (null!=pd) {
			long teachClassId = Long.parseLong(pd.get("MAINID").toString());
			pageData.put("teachClassId", teachClassId);
			beforePd.put("teachClassId", teachClassId);
			try {
				pData.clear();
				pData = studentSelectResultService.getMainId(beforePd);
				if(pData!=null){//更新或者删除
					pageData.put("mainId", Integer.parseInt(pData.get("MAINID").toString()));
				}else{//新增
					PageData pdData = studentSelectResultService.getMaxId();
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
			
		}
		
		return pageData;
		
	}
	

}
