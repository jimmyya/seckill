package org.seckill.service.noblobk.imp;

import org.seckill.dao.noblock.SeckillDao;
import org.seckill.service.noblobk.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by CHEN on 2017/3/30.
 */
@Service
public class SeckillServiceImpl implements SeckillService{

    @Autowired
    private SeckillDao seckillDao;

    @Override
    public boolean executeSeckill() {
         return seckillDao.executeSeckill();
    }

    @Override
    public boolean addSerckill(int id) {
        return seckillDao.addSerckill(id);
    }
}
