##执行秒杀存储过程
##分号变成$$
DELIMITER $$
##定义存储过程
##in 表示输入参数
##out 表示输出参数
##row_count() 0:未修改数据 >0成功修改 <0错误
CREATE PROCEDURE `seckill`.`execute_seckill`
  (IN v_seckill_id BIGINT,IN v_phone BIGINT,
  IN v_kill_time TIMESTAMP,OUT r_result INT)
  BEGIN
    DECLARE insert_count INT DEFAULT 0;
##开启事务
    START TRANSACTION;
    INSERT IGNORE INTO success_killed
    (seckill_id,user_phone,create_time)
    VALUES (v_seckill_id,v_phone,v_kill_time);
##放回上一条sql（diu）类型的影响
    SELECT ROW_COUNT() INTO insert_count;
##重复秒杀
    IF(insert_count = 0) THEN
    ROLLBACK ;
    SET r_result = -1;
##系统异常
    ELSEIF (insert_count<0) THEN
    ROLLBACK;
    SET r_result=-2;
    ELSE
      UPDATE seckill SET number=number-1
      WHERE seckill_id =v_seckill_id
        AND end_time > v_kill_time
        AND start_time < v_kill_time
        AND number>0;
      SELECT ROW_COUNT() INTO insert_count;
      IF(insert_count =0) THEN
        ROLLBACK ;
        SET r_result =0;
      ELSEIF(insert_count <0) THEN
        ROLLBACK ;
        SET r_result= -2;
      ELSE
        COMMIT;
        SET r_result=1;
      END IF;
    END IF;
  END
  $$
  ##结束

DELIMITER ;
##定义一个变量
set @r_result =-3;
##执行储存
call execute_seckill(1003,15521381260,now(),@r_result);
##获得
select @r_result;

##存储过程优化： 事务行级锁持有时间
##不要过度依赖存储过程
##简单逻辑可以使用应用存储过程