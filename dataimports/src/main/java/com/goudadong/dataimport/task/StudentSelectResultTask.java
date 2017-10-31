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
	private int  saveData(PageData pData,int flag) {
		int result = 0;
		try {
			String []tableMainId = pData.getString("tableMainId").split("\\|");//修改之后的数据
			String []tableMainIdNew = {};
			if(pData.containsKey("tableMainIdNew")){
				tableMainIdNew= pData.getString("tableMainIdNew").split("\\|");//修改之前的数据
			}
			//变之后的数据
			PageData pd =new PageData();
			PageData findPd = null;
			if (tableMainId.length>0) {
				String tempXn = tableMainId[0].split(":")[1].trim();
				String tempXq = tableMainId[1].split(":")[1].trim();
				String courseCode = tableMainId[2].split(":")[1].trim();
				String xh = tableMainId[3].split(":")[1].trim();
				
				pd.put("xn", tempXn);
				pd.put("xq", tempXq);
				pd.put("kc_id", courseCode);
				pd.put("xh", xh);
				String o_xh = hwadee_OpTableService.findByColums_beforeStuSelect(pd);
				pd.put("studentNo", o_xh);
				findPd = hwadee_OpTableService.findByColums_stuselect(pd);
			}
			//变之前的数据
			PageData beforePd = new PageData();
			if(tableMainIdNew.length>0){
				String beforeXn = tableMainIdNew[0].split(":")[1].trim();
				String beforeXq = tableMainIdNew[1].split(":")[1].trim();
				String beforecourseCode = tableMainIdNew[2].split(":")[1].trim();
				String beforexh = tableMainIdNew[3].split(":")[1].trim();
				beforePd.put("xn", beforeXn);
				beforePd.put("xq", beforeXq);
				beforePd.put("kc_id", beforecourseCode);
				beforePd.put("xh", beforexh);
				
				String o_xh = hwadee_OpTableService.findByColums_beforeStuSelect(beforePd);
				beforePd.put("studentNo", o_xh);
			}
			
			logger.info("------------>获取到了数据"+findPd);
			//切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			logger.info("----------->开始处理数据！");
			if(findPd!=null){
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
	 * @throws Exception 
	 * @throws Exception  
	* @Title: detalData 
	* @Description:  处理数据
	* @param pd
	* @param flag  1：更新，2：删除，3：新增
	* @return void    返回类型 
	* @throws 
	*/
	private int  detalData(PageData data,PageData beforePd,int flag) throws Exception {
		int result = 0;
		PageData pageData = new PageData();
		if (flag==1) {
			pageData =  getOracleData(data,beforePd);
			result= studentSelectResultService.studentSelectResult_update(pageData);
			logger.info("受影响行数"+result+",执行方法studentSelectResult_update");
		}
		if (flag==2) {
			pageData =  getDelOracleData(data);
			result = studentSelectResultService.studentSelectResult_delete(pageData);
			logger.info("受影响行数"+result+",执行方法studentSelectResult_delete");
		}
		if (flag==3) {
			pageData =getOracleData(data,beforePd);
			result= studentSelectResultService.studentSelectResult_insert(pageData);
			logger.info("受影响行数"+result+",执行方法studentSelectResult_insert");
		}
		return result;
	}
	
	/**
	 * 删除
	 * @param pageData
	 * @return
	 * @throws Exception
	 */
	private PageData getDelOracleData(PageData pageData) throws Exception {
		
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		//设置班号
		String coursecode = pageData.get("kc_id").toString().split("\\-")[0].trim();
		String classCode = pageData.get("kc_id").toString().split("\\-")[1].trim();
		if (pageData.get("xq").equals("0")) {
			pageData.put("xq", "一");
		}
		if (pageData.get("xq").equals("1")) {
			pageData.put("xq", "二");
		}
		pageData.put("classCode",classCode);//班号
		pageData.put("kcid", coursecode);//课程代码
		SetXnUtil.setXn(pageData);
		//定义获取教学班id
		PageData pd = studentSelectResultService.getTeachClassId(pageData);
		if (null!=pd) {
			long teachClassId = Long.parseLong(pd.get("MAINID").toString());
			pageData.put("teachClassId", teachClassId);
		}
		PageData pData = studentSelectResultService.getMainId(pageData); 
		if(pData!=null){
			pageData.put("mainId", Integer.parseInt(pData.get("MAINID").toString()));
		}else{
			pageData=null;
		}
		return pageData;
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
				if(!beforePd.isEmpty()){//判断是否为空，为空的情况为新增的情况
					pData = getDelOracleData(beforePd);
					if(pData!=null){//更新或者删除
						pageData.put("mainId", Integer.parseInt(pData.get("mainId").toString()));
					}
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
