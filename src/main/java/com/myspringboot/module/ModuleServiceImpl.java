package com.myspringboot.module;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.myspringboot.daosource.commondao.BaseDao;
import com.myspringboot.sqlmanage.SqlFactory;



@Service("moduleServiceImpl")
public class ModuleServiceImpl implements ModuleService{
	@Autowired
	@Qualifier("baseDAO")
	private BaseDao baseDAO;
	/**
	 * 获取角色模块
	 * @param userId
	 * @return
	 */
	@SuppressWarnings(value={"rawtypes","unchecked"})
	public List findModuleByUserId(int userId) {
		String sql = SqlFactory.getSqlContext("findModuleByUserid", "shiro");
		return baseDAO.queryForList2Object(sql, new Object[]{ userId}, ModuleInfo.class);
	}
}
