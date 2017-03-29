package org.seckill.service.imp;

import org.apache.commons.collections.MapUtils;
import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CHEN on 2016/5/21.
 */

//@Component ：组件的示例 包括 @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService{
    private Logger logger= LoggerFactory.getLogger(this.getClass());



    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //md5盐值字符串，用于混淆md5
    private final String slat="safsljflanf&*(fjs&*sfs^*";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }


    public Exposer exportSeckillUrl(long seckillId) {
        //通过redis进行缓存
        /**
         * get from cache
         * if null
         *  get db
         *  else
         *          put cache
         *  locgoin
         */
        //优化点：缓存优化:超时的基础上维护一致性
        //1. 访问redis
        Seckill seckill=redisDao.getSeckill(seckillId);
        if(null==seckill) {
        seckill =seckillDao.queryById(seckillId);
            if(null==seckill) {
                return new Exposer(false,seckillId);
            } else {
                redisDao.putSeckill(seckill);
            }

        }
        Date startTime=seckill.getStartTime();
        Date endTime=seckill.getEndTime();

        //当前系统时间
        Date nowTime=new Date();
        if(nowTime.getTime()<startTime.getTime()
                ||nowTime.getTime()>endTime.getTime()) {
            return new Exposer(false,seckillId,nowTime.getTime(),
                    startTime.getTime(),endTime.getTime());
        }
        //转化成不可逆的特定字符串
        String md5=getMD5(seckillId);
        return new Exposer(true,md5,seckillId);
    }

    /**
     * 可复用
     * @param seckillId
     * @return
     */
    private String getMD5 (long seckillId) {
        String base=seckillId+"/"+slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     *
     * @param seckillId
     * @param userPhone
     * @param md5 比较md5的值 判断是否允许秒杀
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    /**
     * 使用注解控制事务方法的优点
     * 1.开发团队达成一致的约定，明确标注事务方法的编程风格，看到@Transactional就知道是事务方法
     * 2.保证事务方法的执行时间尽可能短，避免一直的update，不要穿插其他的网络操作RPC/HTTP，如缓存等，
     * 如果还是必要的话就要进行剥离，建一个高层操作，操作只留下干净的数据库操作和网络方法
     * 3.不是所有的方法都需要事务，如只有一条修改操作，添加修改删除或者查看。
     * 看看mysql行级锁，不需要多个操作就不要spring多层封装
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(md5==null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存+记录购买行为
        Date nowTime =new Date();

        try {
            //成功减库存 记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //数据库中的联合主键
            if (insertCount <= 0) {
                throw new RepeatKillException("seckill repeated");
            } else {
                int updateCount =seckillDao.reduceNumber(seckillId,nowTime);
                if (updateCount <= 0) {
                    //没有更新记录
                    throw new SeckillCloseException("seckill is close");
                } else {
                //秒杀成功
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }


        }catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        }
        catch (Exception e) {
            logger.error(e.getMessage(),e);
            //所有的编译器异常转换成运行期异常
            //因为spring能帮我们把运行期异常回滚
            throw new SeckillException("seckill inner error:"+e.getMessage());
        }
    }

    /**
     * @param seckillId
     * @param userPhone
     * @param md5 比较md5的值 判断是否允许秒杀
     * @return
     * @throws SeckillException
     * @throws RepeatKillException
     * @throws SeckillCloseException
     */
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if(null==md5 || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId,SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime=new Date();
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        try {
            seckillDao.killByProcedure(map);
            //获得之前的result
            int result=  MapUtils.getInteger(map,"result",-2);
            if(result==1) {
                SuccessKilled sk=successKilledDao
                        .queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStateEnum.SUCCESS,sk);
            } else {
                return new SeckillExecution(seckillId,SeckillStateEnum.stateOf(result));
            }
        }catch (Exception e) {
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
        }
    }
}
