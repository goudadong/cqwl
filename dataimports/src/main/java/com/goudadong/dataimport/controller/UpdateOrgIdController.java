package com.goudadong.dataimport.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.goudadong.dataimport.service.UpdateOrgIdService;
import com.goudadong.dataimport.util.DataSourceConst;
import com.goudadong.dataimport.util.DataSourceContextHolder;
import com.goudadong.dataimport.util.InstitutionUtil;
import com.goudadong.dataimport.util.PageData;

@Controller
@RequestMapping(value="updateOrgId")
public class UpdateOrgIdController {
	
	@Resource(name="updateOrgIdService")
	private UpdateOrgIdService updateOrgIdService;
	
	@RequestMapping(value="update")
	public void updateOrgId() throws Exception{
		
		//切换数据库
		DataSourceContextHolder.setDataSourceType(DataSourceConst.ORACLE);
		InstitutionUtil inUtil = new InstitutionUtil();
		Map<String, String> map = inUtil.map;//承担单位对应的组织结构代码
		PageData pageData = new PageData();
		for (Map.Entry<String, String> entry:map.entrySet()) {
			if(!entry.getValue().isEmpty()){
				pageData.put("key", entry.getKey());
				pageData.put("value", entry.getValue());
				updateOrgIdService.update(pageData);
			}
		}
		
	}

}
