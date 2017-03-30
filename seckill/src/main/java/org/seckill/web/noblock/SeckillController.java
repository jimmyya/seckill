package org.seckill.web.noblock;

import jdk.nashorn.internal.ir.RuntimeNode;
import org.seckill.service.noblobk.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.Semaphore;


/**
 * Created by CHEN on 2017/3/30.
 * <p>
 * 现在重新定义游戏规则：
 * 首先如果商品只有十个
 * 那么第一批过滤进入执行队列的最多只能留下一百人
 * 然后一百人再进行乐观锁竞争
 * 但是对同一个乐观锁竞争将造成多次尝试失败的现象
 * 所以采用热点分布的设计
 * 模块组成：
 *
 * @see org.seckill.web.noblock.SeckillController
 */
@Controller
@RequestMapping("/noblock/seckill")
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static Semaphore semaphore = new Semaphore(100);//只有一百允许同时进入
    int count = 0;

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/seckill", method = RequestMethod.GET)
    public String seckill() {
        return "/noblock/seckill";
    }

    @RequestMapping(value = "/seckill", method = RequestMethod.POST)
    public String executeSeckill(@RequestParam("id") int id) {
        if (!semaphore.tryAcquire()) {//排在一百名之后
            System.out.println("请求失败");
            System.out.println("请求数" + count);
            return "/noblock/fail";
        } else {//进行资源争夺
//            //执行减库存
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            if (!seckillService.executeSeckill()) {
                System.out.println("数据库已经没有存货");
            } else {
                seckillService.addSerckill(id);
            }
            synchronized (SeckillController.class) {
                count++;
                System.out.println("请求成功" + count);
            }
            return "/noblock/success";
        }
    }


}
