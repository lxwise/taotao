package com.taotao.test.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	//测试单机版
	@Test
	public void testjedis() {
		//创建jedis对象，需要指定连接地址和端口
		Jedis jedis =new Jedis("192.168.25.128",6379);
		//直接操作jedis set
		jedis.set("key1234", "12345");
		System.out.println(jedis.get("key1234"));
		//关闭jedis
		jedis.close();
	}
	/**
	 * 8.2.	使用连接池连接单机版
	 */
	@Test
	public void testJedisPool() {
		//创建jedispool对象 对象需要制定地址和端口
		JedisPool pool =new JedisPool("192.168.25.128",6379);
		//获取jedis的对象
		Jedis jedis = pool.getResource();
		//直接操作redis
		jedis.set("keypool", "keypool");
		System.out.println(jedis.get("keypool"));
		//关闭redis （释放资源到连接池）
		jedis.close();
		//关闭连接池（应用系统关闭的时候才关闭）
		pool.close();
		
	}
	//测试集群版
	@Test
	public void testjediscluster() {
		Set<HostAndPort> nodes = new HashSet<>();
		nodes.add(new HostAndPort("192.168.25.128", 7001));
		nodes.add(new HostAndPort("192.168.25.128", 7002));
		nodes.add(new HostAndPort("192.168.25.128", 7003));
		nodes.add(new HostAndPort("192.168.25.128", 7004));
		nodes.add(new HostAndPort("192.168.25.128", 7005));
		nodes.add(new HostAndPort("192.168.25.128", 7006));
		//创建jedisCluster对象
		JedisCluster cluster = new JedisCluster(nodes );
		//直接根据jedisCluster对象操作redis集群
		cluster.set("keycluster", "cluseter的value");
		System.out.println(cluster.get("keycluster"));
		//关闭jedisCluster对象（应用系统关闭的时候才关闭）封装了连接池
		cluster.close();
	}
	
}
