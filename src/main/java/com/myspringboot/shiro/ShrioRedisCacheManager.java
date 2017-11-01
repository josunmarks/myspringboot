/**
 * 
 */
package com.myspringboot.shiro;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author wangjoun
 *
 */
public class ShrioRedisCacheManager extends AbstractCacheManager {
	private RedisTemplate redisTemplate;
	public ShrioRedisCacheManager(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	/* (non-Javadoc)
	 * @see org.apache.shiro.cache.AbstractCacheManager#createCache(java.lang.String)
	 */
	@Override
	protected Cache createCache(String name) throws CacheException {
		return new ShrioRedisCache(redisTemplate, name);
	}

}
