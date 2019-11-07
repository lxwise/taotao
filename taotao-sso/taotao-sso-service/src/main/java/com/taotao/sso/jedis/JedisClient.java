package com.taotao.sso.jedis;

public interface JedisClient {

	String set(String key, String value);
	String get(String key);
	Boolean exists(String key);//看存不存在
	Long expire(String key, int seconds);//过期时间设置
	Long ttl(String key);//过期时间时间点还剩多少
	Long incr(String key);
	Long hset(String key, String field, String value);
	String hget(String key, String field);	
	Long hdel(String key,String... field);//删除hkey
	
}
