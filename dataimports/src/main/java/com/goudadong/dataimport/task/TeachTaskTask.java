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
			try {
				logger.info(pageData.toString());
				pageData.put("opState", 1);
				switch (pageData.getString("opType")) {
				case "update":
					saveData(pageData, 1);// 保存数据，1表示更新
					hwadee_OpTableService.hwadee_OpTable_update(pageData);
					break;
				case "delete":
					saveData(pageData, 2);// 保存数据，2表示删除
					hwadee_OpTableService.hwadee_OpTable_update(pageData);
					break;
				case "insert":
					saveData(pageData, 3);// 保存数据，3表示插入
					hwadee_OpTableService.hwadee_OpTable_update(pageData);
					break;
				default:
					break;
				}

			} catch (Exception e) {
				logger.error("同步出现异常" + e.getMessage());
				pageData.put("opState", -1);
				hwadee_OpTableService.hwadee_OpTable_update(pageData);
			}
		}
	}

	/**
	 * @Title: saveData @Description: 保存数据 @param pData @param flag
	 * 1：更新，2：删除，3：新增 @throws Exception 参数说明 @return void 返回类型 @throws
	 */
	private void saveData(PageData pData, int flag) throws Exception {
		String[] tableMainId = pData.getString("tableMainId").split("\\|");// tableMainId=XH:2004|XQ_ID:0|KCDM:171019
																			// |T_SKBJ:171019-005
																			// |BJDM:2001050303
		if (tableMainId.length > 1) {
			PageData pd = new PageData();
			String tempXn = tableMainId[0].split(":")[1].trim();
			String tempXq = tableMainId[1].split(":")[1].trim();
			String xh = tableMainId[2].split(":")[1].trim();

			pd.put("xn", tempXn);
			pd.put("xq", tempXq);
			pd.put("xh", xh);

			pd = hwadee_OpTableService.findByColums_task(pd);
			logger.info("------------>获取到了数据" + pd);
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			logger.info("----------->开始处理数据！");
			try {
				detalData(pd, flag);
				// 切换数据库
				DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			} catch (Exception e) {
				// 切换数据库
				DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
				logger.error("同步出现异常" + e.getMessage());
				pData.put("opState", -1);
				hwadee_OpTableService.hwadee_OpTable_update(pData);
			}
			logger.info("----------->处理数据结束！");

		}
	}

	/**
	 * @throws Exception @throws Exception @Title: detalData @Description:
	 * 处理数据 @param pd @param flag 1：更新，2：删除，3：新增 @return void 返回类型 @throws
	 */
	private void detalData(PageData data,int flag) throws Exception {
		PageData pageData = new PageData();
		if (flag==1) {
			pageData =  getOracleData(data);
			logger.info("开始处理数据:"+pageData.toString());
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			List<PageData>  bjdms =  teacherClassService.getBjdm(pageData);
			for (PageData bjdm : bjdms) { 
				// 切换数据库
				DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
				int teachClassId = Integer.parseInt(pageData.get("mainId").toString());
				pageData.put("teachClassId", teachClassId);
				pageData.put("natureClassId", teacherClassService.getOrgClassId(bjdm));
				teacherClassService.natureClass_update(pageData);
			}
			
			// 学年
			SetXnUtil.setXn(pageData);teacherClassService.teacherClass_update(pageData);
			scheduleMethod_update(pageData);
		}
		if (flag==2) {
			pageData =  getOracleData(data);
			teacherClassService.natureClass_delete(pageData);
			teacherClassService.teacherClass_delete(pageData);
			scheduleMethod_delete(pageData);
		}
		if (flag==3) {
			pageData = getOracleData(data);
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			List<PageData>  bjdms =  teacherClassService.getBjdm(pageData);
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
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
				teacherClassService.insertTeachNature(pageData);
			}
			// 学年
			SetXnUtil.setXn(pageData);
			teacherClassService.teacherClass_insert(pageData);
			//插入理论学时排课方式，实践排课不插入
			scheduleMethod_insert(pageData);
			
		}
	}

	/**
	 * @Title: update @Description: 获取更新数据 @param pageData @return @throws
	 * Exception 参数说明 @return PageData 返回类型 @throws
	 */
	private PageData getOracleData(PageData pageData) throws Exception {

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
		// 学生人数
		int bjrs = 0;
		String campusCode ="";
		PageData bjrspd = teacherClassService.getBjrs(pageData);
		if (bjrspd != null) {
			bjrs = Integer.parseInt(bjrspd.get("bjrs").toString());
			if(bjrspd.containsKey("xq")){
				campusCode = bjrspd.getString("xq");
			}
		}
		pageData.put("stuNum", bjrs);
		pageData.put("campusCode", campusCode);
		pageData.put("reasonCode", "0");
		//// 班级人数
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
		try {
			PageData pData = new PageData();
			pData = teacherClassService.getMainId(pageData);
			if (pData != null) {// 更新或者删除
				pageData.put("mainId", Integer.parseInt(pData.get("MAINID").toString()));
			} else {// 新增
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
	 * @throws Exception
	 */
	private void scheduleMethod_delete(PageData pageData) throws Exception {
		PageData scheduleMethodData = new PageData();
		scheduleMethodData.put("teacherClassId", pageData.get("mainId"));
		scheduleMethodService.scheduleMethod_delete(scheduleMethodData);
		
	}
	
	/**
	 * 更新排课方式
	 * @param pageData
	 * @throws Exception 
	 */
	private void scheduleMethod_update(PageData pageData) throws Exception {
		PageData scheduleMethodData = new PageData();
		scheduleMethodData.put("teacherClassId", pageData.get("mainId"));
		scheduleMethodData.put("startWeek", pageData.get("KSZ"));
		scheduleMethodData.put("endWeek", pageData.get("JSZ"));
		scheduleMethodData.put("capacity", pageData.get("stuNum"));
		scheduleMethodData.put("dataRights", pageData.get("manageWeekHours")); ////排课系统周学时--暂时存此字段
		
		scheduleMethodService.scheduleMethod_update(scheduleMethodData);
	}

	/**插入数据到排课方式表
	 * 
	 * @param classData
	 * @throws Exception 
	 */
	public void scheduleMethod_insert(PageData classData) throws Exception {
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
		
		
		scheduleMethodService.scheduleMethod_insert(scheduleMethodData);
	}
	


}
