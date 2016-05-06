import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import redis.clients.jedis.Jedis;

/**
 * 
 * <p>
 * <b>Demo</b> 是 redis测试
 * </p>
 * 
 * @author <a href="mailto:wentian.he@qq.com">hewentian</a>
 * @since 2016年5月6日
 */
public class Demo {
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

	public static void main(String[] args) {
		Jedis jedis = new Jedis(host, port);

		// 密码验证，如果你没有设置redis密码可不验证即可使用相关命令
		// jedis.auth("abcdefg");

		// 简单的key-value存储
		jedis.set("redis", "myredis");
		System.out.println(jedis.get("redis"));

		// 在原有值的基础上添加，如若之前没有该key，则导入该key
		// 之前已经设置了redis对应的"myredis",此句执行便会使redis对应"myredisyourredis"
		jedis.append("redis", " yourredis");
		jedis.append("content", "rabbit");
		System.out.println(jedis.get("redis"));
		System.out.println(jedis.get("content"));

		// mset是设置多个key-value值，参数(key1, value1, key2, value2,..., keyn, valuen)
		// mget是获取多个key所对应的value，参数(key1, key2, key3, ..., keyn)返回的是个list
		jedis.mset("name1", "tim1", "name2", "tim2", "name3", "tim3");
		System.out.println(jedis.mget("name1", "name2", "name3"));

		// map
		Map<String, String> user = new HashMap<String, String>();
		user.put("name", "cd");
		user.put("password", "123456");
		// map 存入redis
		jedis.hmset("user", user);
		// mapkey个数
		System.out.println(String.format("len: %d", jedis.hlen("user")));
		// map中的所有键值
		System.out.println(String.format("keys: %s", jedis.hkeys("user")));
		// map中的所有value
		System.out.println(String.format("values: %s", jedis.hvals("user")));
		// 取出map中的name字段
		List<String> rsmap = jedis.hmget("user", "name", "password");
		System.out.println(rsmap);
		// 删除map中的某一个键值password
		jedis.hdel("user", "password");
		System.out.println(jedis.hmget("user", "name", "password"));

		// list
		jedis.del("listDemo");
		System.out.println(jedis.lrange("listDemo", 0, -1));
		jedis.lpush("listDemo", "A");
		jedis.lpush("listDemo", "B");
		jedis.lpush("listDemo", "C");
		System.out.println(jedis.lrange("listDemo", 0, -1));
		System.out.println(jedis.lrange("listDemo", 0, 1));

		jedis.sadd("sname", "h");
		jedis.sadd("sname", "w");
		jedis.sadd("sname", "t");
		System.out.println(String.format("set num: %d", jedis.scard("sname")));
		System.out.println(String.format("all members: %s",
				jedis.smembers("sname")));
		System.out.println(String.format("is member: %B",
				jedis.sismember("sname", "h")));
		System.out.println(String.format("rand member: %s",
				jedis.srandmember("sname")));

		// 删除一个对象
		jedis.srem("sname", "t");
		System.out.println(String.format("all members: %s",
				jedis.smembers("sname")));

		jedis.disconnect();
		jedis.close();
	}
}