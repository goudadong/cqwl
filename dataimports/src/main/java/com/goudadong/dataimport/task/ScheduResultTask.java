package com.goudadong.dataimport.task;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.goudadong.dataimport.service.Hwadee_OpTableService;
import com.goudadong.dataimport.service.ScheduResultService;
import com.goudadong.dataimport.service.TeacherClassService;
import com.goudadong.dataimport.util.Const;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;
import com.goudadong.dataimport.util.PropertiesUtil;

public class ScheduResultTask {

	
	//日志
		private Logger logger = LoggerFactory.getLogger(ScheduResultTask.class);
		//同步标志
		private static boolean syncFlag = true;
		@Resource(name="hwadee_OpTableService")
		private Hwadee_OpTableService hwadee_OpTableService;
		@Resource(name="scheduResultService")
		private ScheduResultService scheduResultService;
		@Resource(name = "teacherClassService")
		private TeacherClassService teacherClassService;
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
						pd.put("tableName", PropertiesUtil.getValueByKey(Const.SYSTEM_PROPERTIES_CONST, "T_4"));
						SyncData(hwadee_OpTableService.opTableList(pd));
						//切换数据库
						DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
						hwadee_OpTableService.updateCounter();
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
						result =saveData(pageData,1);//保存数据，1表示更新
						break;
					case "delete":
						result =saveData(pageData,2);//保存数据，2表示删除	
						break;
					case "insert":
						result =saveData(pageData,3);//保存数据，3表示插入
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
				PageData findPd = null;
				if (tableMainId.length>1) {
					String tempXn = tableMainId[0].split(":")[1].trim();
					String tempXq = tableMainId[1].split(":")[1].trim();
					String skbj = tableMainId[2].split(":")[1].trim();
					String bjdm = tableMainId[3].split(":")[1].trim();
					String jcz = tableMainId[4].split(":")[1].trim();
					String dsz = tableMainId[5].split(":")[1].trim();
					String jsm = tableMainId[6].split(":")[1].trim();
					String stimezc = tableMainId[7].split(":")[1].trim();
					String jcinfo = tableMainId[8].split(":")[1].trim();
					
					pd.put("xn", tempXn);
					pd.put("xq", tempXq);
					pd.put("skbj", skbj);
					pd.put("bjdm", bjdm);
					pd.put("jcz", jcz);
					pd.put("dsz", dsz);
					pd.put("jsm", jsm);
					pd.put("stimezc", stimezc);
					pd.put("jcinfo", jcinfo);
					findPd = hwadee_OpTableService.findByColums_schedul(pd);
				}
				//变之前的数据
				PageData beforePd = new PageData();
				if (tableMainIdNew.length>1) {
					String tempXn = tableMainIdNew[0].split(":")[1].trim();
					String tempXq = tableMainIdNew[1].split(":")[1].trim();
					String skbj = tableMainIdNew[2].split(":")[1].trim();
					String bjdm = tableMainIdNew[3].split(":")[1].trim();
					String jcz = tableMainIdNew[4].split(":")[1].trim();
					String dsz = tableMainIdNew[5].split(":")[1].trim();
					String jsm = tableMainIdNew[6].split(":")[1].trim();
					String stimezc = tableMainIdNew[7].split(":")[1].trim();
					String jcinfo = tableMainIdNew[8].split(":")[1].trim();
					
					beforePd.put("xn", tempXn);
					beforePd.put("xq", tempXq);
					beforePd.put("skbj", skbj);
					beforePd.put("bjdm", bjdm);
					beforePd.put("jcz", jcz);
					beforePd.put("dsz", dsz);
					beforePd.put("jsm", jsm);
					beforePd.put("stimezc", stimezc);
					beforePd.put("jcinfo", jcinfo);
				}
				logger.info("------------>获取到了数据"+findPd);
				//切换数据库
				DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
				logger.info("----------->开始处理数据！");
				if(findPd!=null && flag!=2){
					result = detalData(findPd,beforePd,flag);
				}else if(flag==2){
					result =  detalData(pd,beforePd,flag);
				}else{
					logger.info("在更新或者新增时没有查到源数据，请排查！查询字段："+pd);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			//切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			logger.info("----------->处理数据结束！");
			return result;	
		}


		/**
		 * @return 
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
		private int detalData(PageData data,PageData beforePd, int flag) throws Exception {
			int result = 0;
			if (flag==1) {
				result = getOracleData(data,beforePd);
			}
			if (flag==2) {
				result = deleteOracle(data);
			}
			if (flag==3) {
				result = getOracleData(data,beforePd);
			}
			return result;
		}
		
		/**
		 * 删除原有数据
		 * @param pageData
		 * @throws Exception 
		 */
		private int  deleteOracle(PageData pageData) throws Exception {
			
			int result =0;
			
			// 转换学期
			if (pageData.get("xq").equals("0")) {
				pageData.put("semester", "1");
			}
			if (pageData.get("xq").equals("1")) {
				pageData.put("semester", "2");
			}
			// 设置学年
			//SetXnUtil.setXn(pageData);
			// 截取班号：如013120-002 ，截取002
			String classCode = pageData.get("skbj").toString().split("\\-")[1].trim();
			String courseCode = pageData.get("skbj").toString().split("\\-")[0].trim();
			pageData.put("teachPlaceCode", pageData.getString("jsm"));
			pageData.put("classCode", classCode);
			pageData.put("courseCode", courseCode);
			//节次： 3-4
			String []tempjcs = pageData.getString("jcinfo").split("\\-");
			
			/**节次和星期**/
			if (tempjcs.length>1) {//如果存在单节n的格式情况设置为1-n节
				pageData.put("BEGINSECTION", tempjcs[0]);// 开始节次
				pageData.put("ENDSECTION", tempjcs[1]);// 结束节次
			}else{
				pageData.put("BEGINSECTION", tempjcs[0]);// 开始节次
				pageData.put("ENDSECTION", tempjcs[0]);// 结束节次
			}
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			List<PageData>	mainids = scheduResultService.getMainId(pageData);
			if(mainids.size()>0){//更新或者删除
				for (PageData mainid : mainids) {
					try {
						result = scheduResultService.scheduResult_delete(mainid);
						logger.info("受影响行数"+result+",执行方法scheduResult_delete");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			return result;
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
		private int getOracleData(PageData pageData, PageData beforePd) throws Exception{
			
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			// 转换学期
			if (pageData.get("xq_id").equals("0")) {
				pageData.put("semester", "1");
			}
			if (pageData.get("xq_id").equals("1")) {
				pageData.put("semester", "2");
			}
			// 设置学年
			//SetXnUtil.setXn(pageData);
			// 截取班号：如013120-002 ，截取002
			String classCode = pageData.get("SKBJ").toString().split("\\-")[1].trim();
			// 定义获取教学班id的主键
			PageData pData = new PageData();
			pData.put("xn", pageData.getString("xn").trim());
			pData.put("xq", pageData.getString("semester").trim());
			pData.put("kcid", pageData.getString("courseCode").trim());
			pData.put("classCode", classCode.trim());
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			
			// 班级名称=课程名称_班级代码
			PageData courseNamePd = new PageData();
			courseNamePd = teacherClassService.getCourseName(pageData.getString("courseCode").trim());
			if (courseNamePd != null && courseNamePd.containsKey("COURSENAME")) {
				String courseName = courseNamePd.getString("COURSENAME");
				pageData.put("className", courseName + "_" + classCode);
			} else {
				pageData.put("className", classCode);
			}
			
			// 获取教学班id
			PageData teaClassId = new PageData();
			teaClassId = scheduResultService.getTeachClassId(pData);
			int result = 0;
			if (null != teaClassId) {
				pageData.put("TEACHCLASSID", Integer.parseInt(teaClassId.get("MAINID").toString()));
				pageData.put("classCode", classCode);
				try {
					// 解析周次，节次，上课时间
					result  = Analyse(pageData,beforePd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return result;
			
		}
		
		
		/**
		 * @param pageData
		 * @param zc :周次
		 * @return 
		 * @throws Exception
		 */
		private int savesData(PageData pageData, int zc ) throws Exception{
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
			pageData.put("WEEKORDER", zc);// 周次
			// 解析并设置上课时间
			return setJcTime(pageData);
		}
		
		/**
		 * 设置周次节次
		 * @param pageData
		 * @return 
		 * @throws Exception 
		 */
		private int setZcJc(PageData pageData) throws Exception{
			int result =0;
			String []zcs = pageData.getString("tempzc").split("\\-"); //解析4-10
				if (zcs.length>1) {//表示为4-10这种情况的周次
					for(int i = Integer.parseInt(zcs[0]);i<=Integer.parseInt(zcs[1]);i++){
						if (pageData.getString("dsz").equals("0")) {//不分单双周
							result = savesData(pageData,i);
						}
						if (i%2!=0 && pageData.getString("dsz").equals("1")) {//单周
							result = savesData(pageData,i);
						}
						if (i%2==0  && pageData.getString("dsz").equals("2")) {//双周
							result = savesData(pageData,i);
						}
					}
				}else{//表示只有一周的情况如18周
					int j = Integer.parseInt(zcs[0]);
					if (pageData.getString("dsz").equals("0")) {//不分单双周
						saveData(pageData,j);
					}
					if (j%2!=0 && pageData.getString("dsz").equals("1")) {//单周
						saveData(pageData,j);
					}
					if (j%2==0  && pageData.getString("dsz").equals("2")) {//双周
						saveData(pageData,j);
					}
				}
			return result ;
		}

		/**
		 * 设置周次，节次，上课时间
		 * 
		 * @param pageData
		 * @param beforePd 
		 * @throws Exception
		 */
		private int  Analyse(PageData pageData, PageData beforePd) throws Exception {
			
			int result =0;
			//周次： 01,4-10,12-18 或者12-18 或者 18
			String []tempzcs = pageData.getString("stimezc").split(",");
			//节次： 3-4
			String []tempjcs = pageData.getString("jcinfo").split("\\-");
			
			/**节次和星期**/
			if (tempjcs.length>1) {//如果存在单节n的格式情况设置为1-n节
				pageData.put("BEGINSECTION", tempjcs[0]);// 开始节次
				pageData.put("ENDSECTIONS", tempjcs[1]+"-");// 结束节次
				pageData.put("ENDSECTION", tempjcs[1]);// 结束节次
			}else{
				pageData.put("BEGINSECTION", tempjcs[0]);// 开始节次
				pageData.put("ENDSECTIONS", tempjcs[0]+"-");// 结束节次
				pageData.put("ENDSECTION", tempjcs[0]);// 结束节次
			}
			//设置星期几开始上课
			pageData.put("DAYORDER", pageData.getString("JCz").substring(1, 2));// 星期几
			/**节次和星期**/
			//删除原有的数据
			if(!beforePd.isEmpty()){
				deleteOracle(beforePd);
				/**周次**/
				if (tempzcs.length>1) {//存在多个周次的情况如：01,4-10,12-18
					//循环周次
					for (String zc : tempzcs) {
						pageData.put("tempzc", zc);
						result = setZcJc(pageData);
					}
					
				}else{//数据是18或者2-16的时候zcs只有一个
					pageData.put("tempzc", tempzcs[0]);
					result = setZcJc(pageData);
				}
				
			}
			else{//新增
				/**周次**/
				if (tempzcs.length>1) {//存在多个周次的情况如：01,4-10,12-18
					//循环周次
					for (String zc : tempzcs) {
						pageData.put("tempzc", zc);
						result = setZcJc(pageData);
					}
					
				}else{//数据是18或者2-16的时候zcs只有一个
					pageData.put("tempzc", tempzcs[0]);
					result = setZcJc(pageData);
				}
			}
			return result;
			
		}

		/**
		 * @param pageData
		 * @param tempjc
		 * @throws Exception
		 */
		private int setJcTime(PageData pageData) throws Exception {
			int result = 0;
			// 切换数据库
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			List<PageData> xlList = scheduResultService.findSchoolCalendar(null);
			for (PageData pData : xlList) {
				// 设置学年
				//SetXnUtil.setXn(pData);
				String xn_1 = pData.getString("xn");
				String xn_2 = pageData.getString("xn");
				String xq_1 = pData.getString("xq_id").trim();
				String xq_2 = pageData.getString("xq_id");
				if (xn_1.equals(xn_2)
						&& xq_1.equals(xq_2)) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					//开始时间
					String startTime =  getJcTime(pageData,pData,pageData.get("BEGINSECTION").toString());
					//上课结束时间
					String endTime =  getJcTime(pageData,pData,pageData.get("ENDSECTIONS").toString());
					
					pageData.put("STARTTIME", format.parse(startTime));
					pageData.put("ENDTIME",  format.parse(endTime));
					
					// 切换数据库
					DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
				    //新增
					PageData pdData = scheduResultService.getMaxId();
					int maxid = 0 ;
					if(pdData != null){
						Object object = pdData.get("MAX_ID");
						maxid = Integer.parseInt(object.toString());
					}
					maxid++;
					pageData.put("mainId", maxid);
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					result = scheduResultService.scheduResult_insert(pageData);
					logger.info("受影响行数"+result+",执行方法scheduResult_insert");
				}
			}
			return result ;
		}
		
		/**
		 * 获取jc 的最终时间
		 * @param pageData
		 * @param pData
		 * @param jc
		 * @return
		 * @throws ParseException
		 */
		private String getJcTime(PageData pageData , PageData pData,String jc) throws ParseException{
			String time = "";
			switch (jc) {
			case "1":
				time = setWeekTime(pageData,pData)+" 08:10:00";
				break;
			case "1-":
				time = setWeekTime(pageData,pData)+" 08:55:00";
				break;
			case "2":
				time = setWeekTime(pageData,pData)+" 09:05:00";
				break;
			case "2-":
				time = setWeekTime(pageData,pData)+" 09:50:00";
				break;
			case "3":
				time = setWeekTime(pageData,pData)+" 10:20:00";
				break;
			case "3-":
				time = setWeekTime(pageData,pData)+" 11:05:00";
				break;
			case "4":
				time = setWeekTime(pageData,pData)+" 11:15:00";
				break;
			case "4-":
				time = setWeekTime(pageData,pData)+" 12:00:00";
				break;
			case "5":
				time = setWeekTime(pageData,pData)+" 14:30:00";
				break;
			case "5-":
				time = setWeekTime(pageData,pData)+" 15:15:00";
				break;
			case "6":
				time = setWeekTime(pageData,pData)+" 15:25:00";
				break;
			case "6-":
				time = setWeekTime(pageData,pData)+" 16:10:00";
				break;
			case "7":
				time = setWeekTime(pageData,pData)+" 16:20:00";
				break;
			case "7-":
				time = setWeekTime(pageData,pData)+" 17:05:00";
				break;
			case "8":
				time = setWeekTime(pageData,pData)+" 17:15:00";
				break;
			case "8-":
				time = setWeekTime(pageData,pData)+" 18:00:00";
				break;
			case "9":
				time = setWeekTime(pageData,pData)+" 19:20:00";
				break;
			case "9-":
				time = setWeekTime(pageData,pData)+" 20:05:00";
				break;
			case "10":
				time = setWeekTime(pageData,pData)+" 20:15:00";
				break;
			case "10-":
				time = setWeekTime(pageData,pData)+" 21:00:00";
				break;
			case "11":
				time = setWeekTime(pageData,pData)+" 21:10:00";
				break;
			case "11-":
				time = setWeekTime(pageData,pData)+" 21:55:00";
				break;
			case "12":
				time = setWeekTime(pageData,pData)+" 22:05:00";
				break;
			case "12-":
				time = setWeekTime(pageData,pData)+" 22:50:00";
				break;
			default:
				break;
			}
			return time;
		}

		/**
		 * 获取上课时间精确日期如： 2016-10-23
		 * @param pageData
		 * @param pData
		 * @throws ParseException
		 */
		private String setWeekTime(PageData pageData, PageData pData) throws ParseException {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date date = df.parse(((Timestamp)pData.get("kxrq")).toString().substring(0, 11));
			//System.err.println("开学时间："+df.format(date));
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			int week = cal.get(Calendar.DAY_OF_WEEK)-1; // 获取开学第一天日期为星期几
			if (week<0) {
				week=0;
			}
			int curweek = Integer.parseInt(pageData.getString("DAYORDER"));// 当前上课时间为星期几
			int temp = 7*(Integer.parseInt(pageData.get("WEEKORDER").toString())-1)+curweek - week;//计算开学第一天到n周的星期几有多少天
			String time = df.format(new Date(date.getTime() +(long) temp * 24 * 60 * 60 * 1000)); // 获取本周上课时间
			return time;
		}
}
