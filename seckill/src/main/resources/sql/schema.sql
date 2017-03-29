--数据库初始化脚本

--创建数据库
CREATE database seckill default character set utf8;
--使用数据库
use seckill;
--创建秒杀库存表
create table seckill(
`seckill_id` bigint NOT NULL AUTO_INCREMENT comment '商品库存id',
`name` varchar(120) not null comment '商品名称',
`number` int not null comment '库存数量',
`create_time` TIMESTAMP  default CURRENT_TIMESTAMP  not null comment '创建时间',
`start_time` TIMESTAMP  not null comment '秒杀开启时间',
`end_time` TIMESTAMP  not null comment '秒杀结束时间',

PRIMARY KEY(seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 comment='秒杀库存表';

--初始化数据
insert into seckill (name,number,start_time,end_time)
VALUES
('1000元秒杀iphone6s',100,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
('500元秒杀iphone3s',200,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
('300元秒杀iphone2s',300,'2015-11-01 00:00:00','2015-11-02 00:00:00'),
('10元秒杀iphone1s',40,'2015-11-01 00:00:00','2015-11-02 00:00:00');

--秒杀成功明细表
--用户登陆认证相关信息
create table sucess_killed(
`seckill_id` bigint not null comment '秒杀成功明细表',
`user_phone` bigint not null comment  '用户手机号',
`state` tinyint not null DEFAULT  -1 comment '状态：-1 无效；0 成功；1 已付款',
`create_time` TIMESTAMP  not null comment '创建时间',
PRIMARY KEY(seckill_id,user_phone),/*联合主键*//*过滤重复的秒杀*/
key idx_create_time(create_time)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8 comment='秒杀成功明细表';


--链接数据库控制台
mysql -uroot -p123456


-- 上线v1.1



