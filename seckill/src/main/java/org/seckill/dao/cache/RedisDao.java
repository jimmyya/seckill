package org.seckill.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import org.seckill.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.J;

/**
 * Created by CHEN on 2016/6/1.
 * 用来暴露接口
 */
public class RedisDao {
    private final JedisPool jedisPool;
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    private String password;
    public RedisDao(String ip,int port,String password) {
        jedisPool = new JedisPool(ip,port);//相当于数据库连接池
        this.password=password;
    }

    private RuntimeSchema<Seckill>schema=RuntimeSchema.createFrom(Seckill.class);

    public Seckill getSeckill(long seckillId) {
        //redis操作
        Jedis jedis =jedisPool.getResource();//相当于链接
        jedis.auth(password);
        String key="seckill:"+seckillId;
        //没有实现内存序列化
        //get ->byte[] 获得的就是二进制数据 ->反序列化 ->Object(Seckill)
        //采用自定义的方式序列化
        //protostuff :pojo
        byte[] bytes=jedis.get(key.getBytes());
        //从缓存获取的
        try {
            if(bytes!=null) {
                //空对象
                Seckill seckill=schema.newMessage();
                //注入空对象
                ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                //seckill被反序列
                return seckill;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        } finally{
            jedis.close();
        }
        return null;
    }

    public String putSeckill(Seckill seckill) {
        //set Object ->byte 其实就是一个序列化的过程
        String key="seckill:"+seckill.getSeckillId();

        Jedis jedis = jedisPool.getResource();
        String result="false";
        try {
            byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                    LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            int timeout=60*60;//1小时
            result=jedis.setex(key.getBytes(),timeout,bytes);
        }catch (Exception e){
            logger.error(e.getMessage(),e);
        } finally {
            jedis.close();
            return result;
        }
    }


}
