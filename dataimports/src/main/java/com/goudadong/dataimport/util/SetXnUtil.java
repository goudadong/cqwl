/**   
* @Title: SetXnUtil.java 
* @Package com.goudadong.dataimport.util 
* @Description: TODO
* @author goudadong
* @date 2017年9月13日 下午7:00:41 
* @version V1.0   
*/
package com.goudadong.dataimport.util;

/**
 * @author goudadong
 *
 */
public class SetXnUtil {

	/**
	 * 设置学年
	 * @param pageData
	 */
	public static void setXn(PageData pageData) {

		switch (pageData.getString("xn").trim()) {
		case "2000":
			pageData.put("xn", "2000-2001");
			break;
		case "2001":
			pageData.put("xn", "2001-2002");
			break;
		case "2002":
			pageData.put("xn", "2002-2003");
			break;
		case "2003":
			pageData.put("xn", "2003-2004");
			break;
		case "2004":
			pageData.put("xn", "2004-2005");
			break;
		case "2005":
			pageData.put("xn", "2005-2006");
			break;
		case "2006":
			pageData.put("xn", "2006-2007");
			break;
		case "2007":
			pageData.put("xn", "2007-2008");
			break;
		case "2008":
			pageData.put("xn", "2008-2009");
			break;
		case "2009":
			pageData.put("xn", "2009-2010");
			break;
		case "2010":
			pageData.put("xn", "2010-2011");
			break;
		case "2011":
			pageData.put("xn", "2011-2012");
			break;
		case "2012":
			pageData.put("xn", "2012-2013");
			break;	
		case "2013":
			pageData.put("xn", "2013-2014");
			break;
		case "2014":
			pageData.put("xn", "2014-2015");
			break;
		case "2015":
			pageData.put("xn", "2015-2016");
			break;
		case "2016":
			pageData.put("xn", "2016-2017");
			break;
		case "2017":
			pageData.put("xn", "2017-2018");
			break;
		default:
			break;
		}
	}
}
