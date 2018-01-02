/**   
* @Title: ScheduResultController.java 
* @Package com.goudadong.dataimport.contorler 
* @Description: TODO
* @author goudadong
* @date 2017年9月14日 下午1:53:35 
* @version V1.0   
*/
package com.goudadong.dataimport.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.goudadong.dataimport.service.ScheduResultService;
import com.goudadong.dataimport.service.TeacherClassService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.PageData;

/**
 * @author goudadong
 *
 */
@Controller
@RequestMapping(value = "scheduResult")
public class ScheduResultController {

	@Resource(name = "scheduResultService")
	private ScheduResultService scheduResultService;
	@Resource(name = "teacherClassService")
	private TeacherClassService teacherClassService;

	@RequestMapping(value = "list")
	public ModelAndView scheduResultList() throws Exception {
		// 切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> list = scheduResultService.scheduResultList(null);
		ModelAndView mv = new ModelAndView();
		mv.addObject("list", list);
		mv.addObject("url", "savescheduResult");
		mv.setViewName("index");
		return mv;
	}

	@RequestMapping(value = "savescheduResult")
	public ModelAndView savescheduResult() throws Exception {
		Calendar calendar = Calendar.getInstance();
		int endyear = calendar.get(Calendar.YEAR)+1;
		int startYear = 2000;
		for(int i =startYear;i<=endyear;i++){
			System.err.println(i);
			PageData xnpd = new PageData();
			xnpd.put("xn", i);
			DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
			List<PageData> list = scheduResultService.scheduResultList(xnpd);
			for (PageData pageData : list) {
				
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
				if (null != teaClassId) {
					
					pageData.put("TEACHCLASSID", Integer.parseInt(teaClassId.get("MAINID").toString()));
					pageData.put("classCode", classCode);
					pageData.put("ISVALID", 1);
					pageData.put("ISDELETED", 0);
					// 解析周次，节次，上课时间
					try {
						Analyse(pageData);
					} catch (Exception e) {
						e.printStackTrace();
					}
					//设置周次节次格式并导入数据
					//setZcJcWeek(pageData);
				}
			}
		}
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		List<PageData> o_List = scheduResultService.o_scheduResult(null);
		ModelAndView mv = new ModelAndView();
	//	mv.addObject("list", list);
		mv.addObject("o_list", o_List);
		mv.setViewName("success");
		return mv;
	}
	
	
	/**
	 * @param pageData
	 * @param zc :周次
	 * @throws Exception
	 */
	private void saveData(PageData pageData, int zc ) throws Exception{
		// 切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		PageData pdData = scheduResultService.getMaxId();
		int maxid = 0;
		if (pdData!=null) {
			Object object = pdData.get("MAX_ID");
			maxid = Integer.parseInt(object.toString());
		}
		maxid++;
		pageData.put("WEEKORDER", zc);// 周次
		pageData.put("mainId", maxid);
		// 解析并设置上课时间
		setJcTime(pageData);
	}
	
	/**
	 * 设置周次节次
	 * @param pageData
	 * @throws Exception 
	 */
	private void setZcJc(PageData pageData) throws Exception{
		String []zcs = pageData.getString("tempzc").split("\\-"); //解析4-10
			if (zcs.length>1) {//表示为4-10这种情况的周次
				for(int i = Integer.parseInt(zcs[0]);i<=Integer.parseInt(zcs[1]);i++){
					if (pageData.getString("dsz").equals("0")) {//不分单双周
						saveData(pageData,i);
					}
					if (i%2!=0 && pageData.getString("dsz").equals("1")) {//单周
						saveData(pageData,i);
					}
					if (i%2==0  && pageData.getString("dsz").equals("2")) {//双周
						saveData(pageData,i);
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
	}

	/**
	 * 设置周次，节次，上课时间
	 * 
	 * @param pageData
	 * @throws Exception
	 */
	private void Analyse(PageData pageData) throws Exception {
		
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
		
		/**周次**/
		if (tempzcs.length>1) {//存在多个周次的情况如：01,4-10,12-18
			//循环周次
			for (String zc : tempzcs) {
				pageData.put("tempzc", zc);
				setZcJc(pageData);
			}
			
		}else{//数据是18或者2-16的时候zcs只有一个
			pageData.put("tempzc", tempzcs[0]);
			setZcJc(pageData);
		}
		
	}

	/**
	 * @param pageData
	 * @param tempjc
	 * @throws Exception
	 */
	private void setJcTime(PageData pageData) throws Exception {
		// 切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.SQLSERVER);
		List<PageData> xlList = scheduResultService.findSchoolCalendar(null);
		for (PageData pData : xlList) {
			// 设置学年
			//SetXnUtil.setXn(pData);
			String xn_1 = pData.getString("xn").trim();
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
				System.err.println(pageData);
				scheduResultService.scheduResult_insert(pageData);
			}
		}
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

	/**
	 * 分析周次，节次，上课时间: [13周]星期三[5-6节]/知津-D304@[1,3-4,7-18周]星期四[7-8节]/知津-D304
	 * 
	 * @param pageData
	 *//*
	private void Analyse(PageData pageData) {
		System.err.println("解析前的原始数据："+pageData+"-------------------");
		String[] skzcjc = pageData.getString("SkbjZCJC").split("\\@");
		List<String[]> zcs = new ArrayList<>();
		List<String[]>jcs = new ArrayList<>();
		for (String str : skzcjc) {
			String[] temp = str.split("星期");// 去掉星期
			zcs.add(AnalyseZc(temp[0]));// 周次：1,3-4,7-18
			jcs.add(AnalyseJc(temp[1]));// 节次：4,7-8 (第一个数为星期4)
			pageData.put("zcs", zcs);
			pageData.put("jcs", jcs);
		}
	}

	*//**
	 * 解析周次
	 * 
	 * @param tempZc:
	 *            [1,3-4,7-18周]
	 * @return
	 *//*
	private String[] AnalyseZc(String tempZc) {
		System.err.println("解析前的原始周次："+tempZc);
		String[] zcs = tempZc.split(",");// [1,3-4,7-18周]
		if (zcs.length>1) {
			zcs[0] = zcs[0].substring(1, zcs[0].length());// 去掉[ :[1
			zcs[zcs.length - 1] = zcs[zcs.length - 1].substring(0, zcs[zcs.length - 1].length() - 2);// 去掉周]:7-18周]
		}else{
			zcs[0] = zcs[0].substring(1, zcs[0].length());// 去掉[ :[1
			zcs[0] = zcs[0].substring(0, zcs[0].length()-2);// 去掉周] :7-18周]
		}
		for (String string : zcs) {
			System.err.println("解析后的周次："+string);
		}														
		return zcs;

	}

	*//**
	 * 解析节次
	 * 
	 * @param tempZc:
	 *            四[7-8节]/知津-D304
	 * @return
	 *//*
	private String[] AnalyseJc(String tempJc) {
		System.err.println("解析前的原始节次："+tempJc);
		String[] temps = tempJc.split("\\/");// 四[7-8节]/知津-D304
		String[] jcs = temps[0].split("\\[");// 四[7-8节]
		jcs[0] = setWeek(jcs[0]);
		jcs[1] = jcs[1].substring(0, jcs[1].length() - 2);// 去掉节] :7-8节]
		for (String string : jcs) {
			System.err.println("解析后的星期/节次："+string);
		}
		return jcs;
	}
*//*
	*//**
	 * 解析上课天数
	 * 
	 * @param week
	 * @return
	 *//*
	private String setWeek(String week) {
		switch (week) {
		case "一":
			week = "1";
			break;
		case "二":
			week = "2";
			break;
		case "三":
			week = "3";
			break;
		case "四":
			week = "4";
			break;
		case "五":
			week = "5";
			break;
		case "六":
			week = "6";
			break;
		case "七":
			week = "7";
			break;
		case "日":
			week = "7";
			break;
		default:
			break;
		}
		return week;
	}*/

}
