package org.seckill.service;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;

import java.util.List;

/**
 * Created by CHEN on 2016/5/21.
 * 业务接口：使用者的角度设计接口，让别人好用
 * 1.方法定义的粒度明确；
 * 2.参数越简练越好
 * 3.返回类型，友好明确，看了就知道而不是map return 类型/异常
 */
public interface SeckillService {
    /**
     * 查询所有的秒杀
     * @return
     */
    List<Seckill> getSeckillList() ;

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址
     * 否则输出系统时间和秒杀时间
     * 当我们的秒杀没有开启 谁都不知道
     * @param seckillId
     * dto 就是返回类型的数据
     */
    Exposer exportSeckillUrl(long seckillId);


    /**
     *
     * @param seckillId
     * @param userPhone
     * @param md5 比较md5的值 判断是否允许秒杀
     * 用不同的异常情况告诉用户相应情况
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException,RepeatKillException,SeckillCloseException;

    /**
     *  执行秒杀操作by存储过程
     * @param seckillId
     * @param userPhone
     * @param md5 比较md5的值 判断是否允许秒杀
     * 用不同的异常情况告诉用户相应情况
     */
    SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5)
            throws SeckillException,RepeatKillException,SeckillCloseException;



}
