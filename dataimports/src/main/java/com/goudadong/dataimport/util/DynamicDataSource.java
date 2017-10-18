/**   
* @Title: DynamicDataSource.java 
* @Package com.goudadong.dataimport.util 
* @Description: 建立动态数据源
* @author goudadong
* @date 2017年9月11日 下午2:46:59 
* @version V1.0   
*/
package com.goudadong.dataimport.util;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author goudadong
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource  {

	@Override
	protected Object determineCurrentLookupKey() {
		  // 在进行DAO操作前，通过上下文环境变量，获得数据源的类型
		  return DataSourceContextHolder.getDataSourceType();
	}

}
