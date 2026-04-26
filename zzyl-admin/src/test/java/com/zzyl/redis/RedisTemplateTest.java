package com.zzyl.redis;

import cn.hutool.core.thread.ThreadUtil;
import com.zzyl.common.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 的 java
 *
 * @Author: Zhy
 * @Date: 2025-02-27 10:56
 * @Version: 1.0
 */
@SpringBootTest
public class RedisTemplateTest {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	/**
	 * String 类型
	 */
	@Test
	public void testString() {
		ValueOperations<Object, Object> valueOperations = redisTemplate.opsForValue();

		// 设置值
		valueOperations.set("name", "Jack");
		// 设置值并设置过期时间，单位：分钟
		valueOperations.set("age", 25, 2, TimeUnit.SECONDS);
		// 设置值，如果不存在则设置
		valueOperations.setIfAbsent("gender", 1);
		valueOperations.setIfAbsent("gender", 2);

		// 获取值
		String name = (String) valueOperations.get("name");
		System.out.println("name = " + name);
		System.out.println("valueOperations.get(\"age\") = " + valueOperations.get("age"));
		ThreadUtil.sleep(2500);
		System.out.println("valueOperations.get(\"age\") = " + valueOperations.get("age"));

		// 自增值
		valueOperations.set("num", 20);
		valueOperations.increment("num");
		System.out.println("valueOperations.get(\"num\") = " + valueOperations.get("num"));
		valueOperations.increment("num", 5);
		System.out.println("valueOperations.get(\"num\") = " + valueOperations.get("num"));

		// 删除值
		redisTemplate.delete("name");
		redisTemplate.delete("age");
	}

	/**
	 * Hash 类型
	 */
	@Test
	public void testHash() {
		HashMap<String, Object> userMap = new HashMap<>();
		userMap.put("age", 25);
		userMap.put("gender", 1);
		userMap.put("birthday", DateUtils.getNowDate());

		// 保存数据
		redisTemplate.opsForHash().put("user", "name", "Tom");
		redisTemplate.opsForHash().putAll("user", userMap);

		// 获取数据
		Object birthday = redisTemplate.opsForHash().get("user", "birthday");
		System.out.println("birthday = " + birthday);

		// 获取所有字段
		Set<Object> set = redisTemplate.opsForHash().keys("user");
		System.out.println("set = " + set);
		// 获取所有值
		List<Object> user = redisTemplate.opsForHash().values("user");
		System.out.println("user = " + user);
		// 获取所有键值对
		Map<Object, Object> map = redisTemplate.opsForHash().entries("user");
		System.out.println("map = " + map);
	}

	/**
	 * List 类型
	 */
	@Test
	public void testList() {
		// 保存数据
		redisTemplate.opsForList().leftPush("list", "a");
		redisTemplate.opsForList().leftPushAll("list", "b", "c");
		redisTemplate.opsForList().rightPush("list", "d");

		// 获取数据
		List<Object> users = redisTemplate.opsForList().range("list", 0, -1);
		System.out.println("users = " + users);
		// 获取指定索引的数据
		Object user = redisTemplate.opsForList().index("list", 2);
		System.out.println("user = " + user);
		// 弹出数据
		Object n = redisTemplate.opsForList().leftPop("list");
		System.out.println("n = " + n);
	}

	/**
	 * set 类型
	 */
	@Test
	public void testSet() {
		SetOperations<Object, Object> setOperations = redisTemplate.opsForSet();
		// 保存数据
		setOperations.add("hobbies", "java", "C", "C#", "swim");
		Set<Object> hobbies = setOperations.members("hobbies");
		System.out.println("hobbies = " + hobbies);

		// 元素是否存在
		System.out.println(setOperations.isMember("hobbies", "java"));
		System.out.println(setOperations.isMember("hobbies", "C++"));
		// 随机弹出元素
		System.out.println(setOperations.pop("hobbies"));
		// 大小
		System.out.println(setOperations.size("hobbies"));

		setOperations.add("h1", "java", "C", "C#", "swim", "html");
		setOperations.add("h2", "java", "C++", "C#", "swim", "PHP");
		// 交集
		System.out.println(setOperations.intersect("h1", "h2"));
		// 并集
		setOperations.unionAndStore("h1", "h2", "h3");
		System.out.println(setOperations.members("h3"));
		// 差集
		System.out.println(setOperations.difference("h2", "h1"));
	}

	/**
	 * zSet 类型
	 */
	@Test
	public void testZSet() {
		ZSetOperations<Object, Object> zSetOperations = redisTemplate.opsForZSet();
		// 保存数据
		zSetOperations.add("course", "java", 80);
		zSetOperations.add("course", "C", 90);
		zSetOperations.add("course", "C#", 70);
		zSetOperations.add("course", "html", 95);

		// 获取数据
		Set<Object> course = zSetOperations.reverseRange("course", 0, 2);
		assert course != null;
		course.forEach(System.out::println);
	}

	/**
	 * 通用方法
	 */
	@Test
	public void testCommon() {
		// 输出所有 key
		Objects.requireNonNull(redisTemplate.keys("*")).forEach(System.out::println);
		// 设置过期时间，单位：分钟
		redisTemplate.expire("course", Duration.ofMinutes(10));
		// 输出 key 类型
		System.out.println(redisTemplate.type("hobbies"));
		// 是否存在
		System.out.println(redisTemplate.hasKey("hobbies"));
		// 删除 key
		System.out.println(redisTemplate.delete("num"));
	}

}
