package com.goudadong.dataimport.task;

import java.text.MessageFormat;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goudadong.dataimport.service.Hwadee_OpTableService;
import com.goudadong.dataimport.service.ScheduleMethodService;
import com.goudadong.dataimport.service.TeacherClassService;
import com.goudadong.dataimport.service.TheoryCourseService;
import com.goudadong.dataimport.util.Const;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;
import com.goudadong.dataimport.util.PropertiesUtil;
import com.goudadong.dataimport.util.SetXnUtil;

public class TeachTaskTask {

	// 日志
	private Logger logger = LoggerFactory.getLogger(TeacherClassTask.class);
	// 同步标志
	private static boolean syncFlag = true;
	@Resource(name = "hwadee_OpTableService")
	private Hwadee_OpTableService hwadee_OpTableService;
	private PageData pd = new PageData();
	@Resource(name = "teacherClassService")
	private TeacherClassService teacherClassService;
	@Resource(name = "theoryCourseService")
	private TheoryCourseService theoryCourseService;

	@Resource(name = "scheduleMethodService")
	private ScheduleMethodService scheduleMethodService;

	/**
	 * 同步操作
	 */
	public void job() {
		synchronized (this) {
			if (syncFlag) {
				logger.info("-------------------->开始同步");
				syncFlag = false;
				try {
					// 切换数据库
					DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
					pd.put("tableName", PropertiesUtil.getValueByKey(Const.SYSTEM_PROPERTIES_CONST, "T_5"));
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
	 * @throws Exception @Title: SyncData @Description: 同步数据 @param list
	 * 参数说明 @return void 返回类型 @throws
	 */
	private void SyncData(List<PageData> list) throws Exception {
		for (PageData pageData : list) {
			int result =0 ;
			try {
				logger.info(pageData.toString());
				switch (pageData.getString("opType")) {
				case "update":
					result = saveData(pageData, 1);// 保存数据，1表示更新
					break;
				case "delete":
					result = saveData(pageData, 2);// 保存数据，2表示删除
					break;
				case "insert":
					result = saveData(pageData, 3);// 保存数据，3表示插入
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
	 * @return 
	 * @Title: saveData @Description: 保存数据 @param pData @param flag
	 * 1：更新，2：删除，3：新增 @throws Exception 参数说明 @return void 返回类型 @throws
	 */
	private int saveData(PageData pData, int flag) throws Exception {
		
		int result = 0;
		try {
			String []tableMainId = pData.getString("tableMainId").split("\\|");//修改之后的数据
			String []tableMainIdNew = {};
			if(pData.containsKey("tableMainIdNew")){
				tableMainIdNew= pData.getString("tableMainIdNew").split("\\|");//修改之前的数据
			}
			//变之后的数据
			PageData pd =new PageData();
			PageData findPd = null;																// |T_SKBJ:171019-005
			if (tableMainId.length > 1) {
				String tempXn = tableMainId[0].split(":")[1].trim();
				String tempXq = tableMainId[1].split(":")[1].trim();
				String xh = tableMainId[2].split(":")[1].trim();
				String skbj = tableMainId[3].split(":")[1].trim();
				
				pd.put("xn", tempXn);
				pd.put("xq", tempXq);
				pd.put("xh", xh);
				pd.put("skbj", skbj);
	
				findPd = hwadee_OpTableService.findByColums_task(pd);
			}
			//变之前的数据
			PageData beforePd = new PageData();
			if (tableMainIdNew.length > 1) {
				String tempXn = tableMainIdNew[0].split(":")[1].trim();
				String tempXq = tableMainIdNew[1].split(":")[1].trim();
				String xh = tableMainIdNew[2].split(":")[1].trim();
				String skbj = tableMainIdNew[3].split(":")[1].trim();
				
				beforePd.put("xn", tempXn);
				beforePd.put("xq", tempXq);
				beforePd.put("xh", xh);
				beforePd.put("skbj", skbj);
			}
			
			logger.info("------------>获取到了数据"+findPd);
			//切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			logger.info("----------->开始处理数据！");
			if(findPd!=null&&flag!=2){
				result = detalData(findPd,beforePd,flag);
			}else if(flag==2){
				result = detalData(pd,beforePd,flag);
			}else{
				logger.info("在更新或者新增时没有查到源数据，请排查！查询字段："+pd);
			}
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("----------->处理数据结束！");
		return result;
	}

	/**
	 * @param beforePd 
	 * @return 
	 * @throws Exception @throws Exception @Title: detalData @Description:
	 * 处理数据 @param pd @param flag 1：更新，2：删除，3：新增 @return void 返回类型 @throws
	 */
	private int detalData(PageData data,PageData beforePd, int flag) throws Exception {
		int result = 0;
		PageData pageData = new PageData();
		if (flag==1) {
			pageData =  getOracleData(data,beforePd);
			logger.info("开始处理数据:"+pageData.toString());
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			pageData.put("xn_", pageData.getString("xn").split("\\-")[0]);
			
			List<PageData>  bjdms =  teacherClassService.getBjdm(pageData);
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			if (pageData.containsKey("mainId")) {
				for (PageData bjdm : bjdms) { 
					int teachClassId = Integer.parseInt(pageData.get("mainId").toString());
					pageData.put("teachClassId", teachClassId);
					pageData.put("natureClassId", teacherClassService.getOrgClassId(bjdm));
					result = teacherClassService.natureClass_update(pageData);
				}
				logger.info("受影响行数"+result+",执行方法natureClass_update");
				result = teacherClassService.teacherClass_update(pageData);
				logger.info("受影响行数"+result+",执行方法teacherClass_update");
				result = scheduleMethod_update(pageData);
				logger.info("受影响行数"+result+",执行方法scheduleMethod_update");
			}else{
				logger.error("没有查到对应的教学班！");
				result = 0;
			}
		}
		if (flag==2) {
			pageData =  getDelOracleData(data);
			result = teacherClassService.natureClass_delete(pageData);
			logger.info("受影响行数"+result+",执行方法natureClass_delete");
			result = teacherClassService.teacherClass_delete(pageData);
			logger.info("受影响行数"+result+",执行方法teacherClass_delete");
			result = scheduleMethod_delete(pageData);
			logger.info("受影响行数"+result+",执行方法scheduleMethod_delete");
		}
		if (flag==3) {
			pageData = getOracleData(data,beforePd);
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			pageData.put("xn_", pageData.getString("xn").split("\\-")[0]);
			List<PageData>  bjdms =  teacherClassService.getBjdm(pageData);
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			if(pageData.containsKey("mainId")){
				for (PageData bjdm : bjdms) { 
					int teachClassId = Integer.parseInt(pageData.get("mainId").toString());
					PageData pdData = teacherClassService.getNatureMaxId();
					int maxid = 0 ;
					if(pdData != null){
						Object object = pdData.get("MAX_ID");
						maxid = Integer.parseInt(object.toString());
					}
					maxid++;
					pageData.put("naMainid", maxid);
					pageData.put("teachClassId", teachClassId);
					pageData.put("natureClassId", teacherClassService.getOrgClassId(bjdm));
					result = teacherClassService.insertTeachNature(pageData);
				}
				logger.info("受影响行数"+result+",执行方法insertTeachNature");
				result = teacherClassService.teacherClass_insert(pageData);
				logger.info("受影响行数"+result+",执行方法teacherClass_insert");
				//插入理论学时排课方式，实践排课不插入
				result =  scheduleMethod_insert(pageData);
				logger.info("受影响行数"+result+",执行方法scheduleMethod_insert");
			}else{
				logger.error("没有查到对应的教学班！");
				result = 0;
			}
			
			
		}
		return result;
	}

	/**
	 * 删除原有数据
	 * @param pageData
	 * @return
	 * @throws Exception
	 */
	private PageData getDelOracleData(PageData pageData) throws Exception {
		//学期
		if (pageData.get("xq").equals("0")) {
			pageData.put("semester", "一");
		}
		if (pageData.get("xq").equals("1")) {
			pageData.put("semester", "二");
		}
		//学年
		SetXnUtil.setXn(pageData);
		//课程代码
		pageData.put("KCDM", pageData.get("skbj").toString().split("\\-")[0]);
		// 截取班号：如013120-002 ，截取002
		String classCode = pageData.get("skbj").toString().split("\\-")[1]
				.trim();
		pageData.put("classCode", classCode);
		pageData = teacherClassService.getMainId(pageData);
		if(pageData!=null){//删除
			pageData.put("mainId", Integer.parseInt(pageData.get("MAINID").toString()));
		}else{
			pageData=null;
		}
		return pageData;
	}

	/**
	 * @param beforePd 
	 * @Title: update @Description: 获取更新数据 @param pageData @return @throws
	 * Exception 参数说明 @return PageData 返回类型 @throws
	 */
	private PageData getOracleData(PageData pageData, PageData beforePd) throws Exception {

		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		String courseCode = pageData.getString("KCDM").trim();

		// 学期
		if (pageData.get("xq_id").equals("0")) {
			pageData.put("semester", "一");
		}
		if (pageData.get("xq_id").equals("1")) {
			pageData.put("semester", "二");
		}

		// 课程代码
		pageData.put("KCDM", courseCode);
		// 截取班号：如013120-002 ，截取002
		String classCode = pageData.get("T_SKBJ").toString().split("\\-")[1].trim();

		pageData.put("classCode", classCode);
		// 班级名称=课程名称_班级代码
		PageData courseNamePd = new PageData();
		courseNamePd = teacherClassService.getCourseName(courseCode);
		if (courseNamePd != null && courseNamePd.containsKey("COURSENAME")) {
			String courseName = courseNamePd.getString("COURSENAME");
			pageData.put("className", courseName + "_" + classCode);
		} else {
			pageData.put("className", classCode);
		}

		// 切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		pageData.put("reasonCode", "0");
		int zxs = 0; // 青果总学时
		int zzxs = 0; // 青果周学时
		if (pageData.containsKey("ZZXS")) {
			zzxs = (int) Double.parseDouble(pageData.get("ZZXS").toString().trim());
		}
		if (pageData.containsKey("ZXS")) {
			zxs = (int) Double.parseDouble(pageData.get("ZXS").toString().trim());
		}
		// 切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		// 设置排课系统代码：理论学时不为0则作为理论排课系统"001"，否则，作为实践排课系统"002"
		PageData courseData = theoryCourseService.getCourseByCode(courseCode);
		if (courseData != null) {
			// 处理学时
			int theoryHours = 0; // 文理理论学时

			if (courseData.containsKey("THEORYHOURS")) {
				theoryHours = Integer.parseInt(courseData.get("THEORYHOURS").toString().trim());
				if (theoryHours > 0) {
					pageData.put("manageSysCode", "001"); // 理论排课系统
					pageData.put("manageHours", theoryHours);// 理论排课系统总学时
					// 计算理论排课系统周学时
					if (zxs > 0) {
						int manageWeekHours = (int) Math.ceil((theoryHours / zxs) * zzxs); // 向上取整
						pageData.put("manageWeekHours", manageWeekHours);// 理论排课系统周学时
					} else {
						pageData.put("manageWeekHours", 0);// 理论排课系统周学时
					}

				} else {
					pageData.put("manageSysCode", "002"); // 实践排课系统
					pageData.put("manageHours", zxs); // 总学时
					pageData.put("manageWeekHours", zzxs); // 周学时
				}
			}
		} else {
			pageData.put("manageSysCode", "001"); // 如果没找到该课程，则默认理论排课系统
			pageData.put("manageHours", zxs); // 总学时
			pageData.put("manageWeekHours", zzxs); // 周学时

		}
		// 学年
		SetXnUtil.setXn(pageData);
		try {
			PageData pData = new PageData();
			if(!beforePd.isEmpty()){
				pData = getDelOracleData(beforePd);
				if (pData != null) {// 更新
					pageData.put("mainId", Integer.parseInt(pData.get("mainId").toString()));
				}
			}else {// 新增
				PageData pdData = teacherClassService.getMaxId();
				int maxid = 0;
				if (pdData != null) {
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

	
	/**
	 * 删除排课方式
	 * @param pageData
	 * @return 
	 * @throws Exception
	 */
	private int scheduleMethod_delete(PageData pageData) throws Exception {
		PageData scheduleMethodData = new PageData();
		scheduleMethodData.put("teacherClassId", pageData.get("mainId"));
		return scheduleMethodService.scheduleMethod_delete(scheduleMethodData);
		
	}
	
	/**
	 * 更新排课方式
	 * @param pageData
	 * @throws Exception 
	 */
	private int scheduleMethod_update(PageData pageData) throws Exception {
		PageData scheduleMethodData = new PageData();
		scheduleMethodData.put("teacherClassId", pageData.get("mainId"));
		scheduleMethodData.put("startWeek", pageData.get("KSZ"));
		scheduleMethodData.put("endWeek", pageData.get("JSZ"));
		scheduleMethodData.put("capacity", pageData.get("stuNum"));
		scheduleMethodData.put("dataRights", pageData.get("manageWeekHours")); ////排课系统周学时--暂时存此字段
		
		return scheduleMethodService.scheduleMethod_update(scheduleMethodData);
	}

	/**插入数据到排课方式表
	 * 
	 * @param classData
	 * @return 
	 * @throws Exception 
	 */
	public int scheduleMethod_insert(PageData classData) throws Exception {
		PageData scheduleMethodData = new PageData();
		
		PageData object  = scheduleMethodService.getMaxId();
		int maxId = 0;
		if(object!=null){
			maxId=Integer.parseInt(object.get("MAX_ID").toString());
		}
		maxId++;
		scheduleMethodData.put("mainId", maxId);
		scheduleMethodData.put("teacherClassId", classData.get("mainId"));
		scheduleMethodData.put("studyTimeType", "01");//学时类型：理论学时
		scheduleMethodData.put("startWeek", classData.get("KSZ"));
		scheduleMethodData.put("endWeek", classData.get("JSZ"));
		scheduleMethodData.put("assignCode", "");
		scheduleMethodData.put("teachPlaceType", "01"); //教室类型：一般教室
		scheduleMethodData.put("capacity", classData.get("stuNum"));
		scheduleMethodData.put("isValid", "1");
		scheduleMethodData.put("isDeleted", "0");
		scheduleMethodData.put("dataRights", classData.get("manageWeekHours")); ////排课系统周学时--暂时存此字段
		
		
		return scheduleMethodService.scheduleMethod_insert(scheduleMethodData);
	}
	


}
