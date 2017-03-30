package org.seckill;

/**
 * Created by CHEN on 2016/6/2.
 */

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class TestTest {

    private Jedis jedis;

    @Before

    public void setup() {

        //连接redis服务器，虚拟机的ip地址192.168.20.128:6379

        jedis = new  Jedis("192.168.32.128",6379);
        jedis.auth("123");

        //权限认证

        // jedis.auth("root");

    }

    /**

     * redis存储字符串

     */

    @Test
    public void testString() {

        //-----添加数据----------

        jedis.set("name","xinxin");//向key-->name中放入了value-->xinxin
        System.out.println(jedis.get("name"));//执行结果：xinxin


        jedis.append("name", " is my lover"); //拼接
        System.out.println(jedis.get("name"));

        jedis.del("name"); //删除某个键
        System.out.println(jedis.get("name"));
        //设置多个键值对
        jedis.mset("name","liuling","age","23","qq","476777XXX");
        jedis.incr("age"); //进行加1操作
        System.out.println(jedis.get("name") + "-" + jedis.get("age") + "-" + jedis.get("qq"));
    }
}
