import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 
 * <p>
 * <b>Test</b> 是 redis测试
 * </p>
 * 
 * @author <a href="mailto:wentian.he@qq.com">hewentian</a>
 * @since 2014年10月9日
 */
public class Test {
	private static Properties p = new Properties();
	private static String host;
	private static int port;

	static {
		try {
			p.load(Test.class.getClassLoader().getResourceAsStream(
					"redis.properties"));

			host = p.getProperty("host");
			port = Integer.valueOf(p.getProperty("port"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		test1();
		// testPool();
	}

	/**
	 * 简单测试
	 */
	public static void test1() {
		Jedis jedis = new Jedis(host, port);

		jedis.set("name", "tim");
		jedis.append("name", " is an excellenct software engieer");
		System.out.println(jedis.get("name"));
		System.out.println(jedis.exists("name"));
		System.out.println(jedis.exists("school"));

		jedis.disconnect();
		jedis.close();
	}

	/**
	 * 测试连接池
	 */
	public static void testPool() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(Integer.valueOf(p
				.getProperty("redis.maxTotal")));
		jedisPoolConfig.setMaxIdle(Integer.valueOf(p
				.getProperty("redis.maxIdle")));
		jedisPoolConfig.setMinIdle(Integer.valueOf(p
				.getProperty("redis.minIdle")));
		jedisPoolConfig.setMaxWaitMillis(Long.valueOf(p
				.getProperty("redis.maxWaitMillis")));
		jedisPoolConfig.setMinEvictableIdleTimeMillis(Long.valueOf(p
				.getProperty("redis.minEvictableIdleTimeMillis")));
		jedisPoolConfig.setNumTestsPerEvictionRun(Integer.valueOf(p
				.getProperty("redis.numTestsPerEvictionRun")));
		jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(Long.valueOf(p
				.getProperty("redis.softMinEvictableIdleTimeMillis")));
		jedisPoolConfig.setTimeBetweenEvictionRunsMillis(Long.valueOf(p
				.getProperty("redis.timeBetweenEvictionRunsMillis")));
		jedisPoolConfig.setBlockWhenExhausted(Boolean.valueOf(p
				.getProperty("redis.blockWhenExhausted")));

		JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port);

		Jedis jedis = jedisPool.getResource();
		try {
			jedis.set("name2", "hewentian");
			String name2 = jedis.get("name2");
			System.out.println(name2);
			System.out.println(jedis.exists("name2"));

			jedis.zadd("zset", 0, "car");
			jedis.zadd("zset", 2, "bike");
			Set<String> sose = jedis.zrange("zset", 0, -1);
			Iterator<String> it = sose.iterator();
			while (it.hasNext()) {
				System.out.println(it.next());
			}
		} catch (JedisConnectionException e) {
			if (null != jedis) {
				jedisPool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			if (null != jedis) {
				jedisPool.returnResource(jedis);
			}
		}

		jedisPool.destroy();
	}
}