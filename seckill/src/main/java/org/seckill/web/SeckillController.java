package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.dto.SeckillResult;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by CHEN on 2016/5/21.
 */
@Controller//放在Spring中
//一级url
@RequestMapping("/seckill")//url:/模块/资源/｛id｝/细分/seckill/list
public class SeckillController {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    //二级url
    @Autowired
    private SeckillService seckillService;
    @RequestMapping(value="/list",method= RequestMethod.GET)
    public String list(Model model) {
        //list.jsp+model=modelAndView
        List<Seckill> list=seckillService.getSeckillList();
        model.addAttribute("list",list);//存放数据
        return "list";//WEB-INF/jsp/list.jsp
    }

    @RequestMapping(value="/{seckillId}/detail",method=RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId,Model model) {
        if(seckillId==null) {
            return "redirect:/seckill/list";//返回上面那个，最终返回列表页
        }
        Seckill seckill=seckillService.getById(seckillId);
        if(seckill==null) {
            return "forwark:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }

    //ajax 直接输出json
    @RequestMapping(value="/{seckillId}/exposer",
            method=RequestMethod.POST,
    produces={"application/json;charset=UTF-8"})//指定编码格式
    @ResponseBody//封装成json
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result=new SeckillResult<Exposer>(true,exposer);
        }catch(Exception e) {
            logger.error(e.getMessage(),e);
            result=new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }
    @RequestMapping(value="/{seckillId}/{md5}/execution",
    method=RequestMethod.POST,
    produces={"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                  @PathVariable("md5") String md5,
                                                  @CookieValue(value="killPhone",required=false)Long phone) {
        //springmvc valid验证
        if(phone==null) {
          return new SeckillResult<SeckillExecution>(false,"未注册");
      }
        SeckillResult<SeckillExecution> result;
        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        }catch(RepeatKillException e1){
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(false,execution);
        }catch (SeckillCloseException e2){
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.END);
            return new SeckillResult<SeckillExecution>(false,execution);
        }catch (Exception e) {//其他的异常
            logger.error(e.getMessage(),e);
            SeckillExecution execution=new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(false,execution);
        }
    }

    @RequestMapping(value="/time/now",method=RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now= new Date();
        return new SeckillResult(true,now.getTime());
    }
}
