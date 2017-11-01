package com.myspringboot.userinfo;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.myspringboot.daosource.commondao.BaseDao;
import com.myspringboot.module.ModuleInfo;
import com.myspringboot.module.ModuleService;
import com.myspringboot.redis.IRedisService;
import com.myspringboot.sqlmanage.SqlFactory;


@Service
public class UserServiceImpl implements UserService {
	@Autowired
	@Qualifier("baseDAO")
	private BaseDao baseDAO;
	@Autowired
	private ModuleService moduleService;
	@Resource(name="redisServiceImpl")
	private IRedisService redisService;
	/**
	 * 根据账号Account查询当前用户
	 * @param account
	 * @return
	 */
	@Cacheable
	public UserInfo findByAccount(String account) {
		
		String sql = SqlFactory.getSqlContext("findAccount", "shiro");
		UserInfo user =  (UserInfo)baseDAO.query2Object(sql, new Object[]{account}, UserInfo.class);
		redisService.set("userinfo", JSON.toJSONString(user));
		String userjson = redisService.get("userinfo");
		return JSON.parseObject(userjson, UserInfo.class);
	}

	/**
	 * 获取资源集合
	 * @param account
	 * @return
	 */
	public Set<String> findPermissions(String account) {
		Set<String> set = Sets.newHashSet();
		UserInfo user = findByAccount(account);
		List<ModuleInfo>modules = moduleService.findModuleByUserId(user.getId());
		
		for(ModuleInfo info: modules) {
			set.add(info.getModuleKey());
		}
		return set;
	}

	/**
	 * 获取URL权限
	 * @param account
	 * @return
	 */
	public List<String> findPermissionUrl(String account) {
		List<String> list = Lists.newArrayList();
		UserInfo user = findByAccount(account);
		List<ModuleInfo>modules = moduleService.findModuleByUserId(user.getId());
		
		for(ModuleInfo info: modules) {
			if(info.getModuleType() == ModuleInfo.URL_TYPE) {
				list.add(info.getModulePath());
			}
		}
		return list;
	}
}