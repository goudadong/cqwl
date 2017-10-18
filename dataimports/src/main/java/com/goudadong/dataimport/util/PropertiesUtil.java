/**   
* @Title: PropertiesUtil.java 
* @Package cn.wargroup.web.bigdata.util 
* @Description: 读取属性文件工具类
* @author goudadong
* @date 2017年2月15日 下午10:07:55 
* @version V1.0   
*/
package com.goudadong.dataimport.util;

import java.io.IOException;
import java.util.Properties;

/** 
* @ClassName: PropertiesUtil 
* @Description: 读取属性文件工具类 
* @author goudadong
* @date 2017年2月15日 下午10:07:55 
*  
*/

public class PropertiesUtil {

	/** 
	* @Title: getValueByKey 
	* @Description: 根据属性文件的key值获取value值 
	* @param fileName 属性文件名
	* @param keyName 属性文件对应的key值
	* @return  参数说明 
	* @return String    返回类型  返回得到的value值
	* @throws 
	*/
	public static String getValueByKey(String fileName,String keyName){
		Properties props = new Properties();
		try {
			props.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props.getProperty(keyName);
	}
	
}
