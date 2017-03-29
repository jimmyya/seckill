<%@ page contentType="text/html;charset=utf-8" language="java"%>
<!--引入jstl-->
<%@ include file="common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <!--静态包含，合成一个servlet-->
    <%@include file="common/head.jsp"%>
</head>
<body>
    <div class="container">
        <div class="panel panel-default text-center">
            <div class="panel-heading">
                <h1>${seckill.name}</h1>
            </div>
            <div class="panel-body">
                <h2 class="text-danger">
                    <!---显示time图标-->
                    <span class="glyphicon glyhicon-time"></span>
                    <!--展示计时面板-->
                    <span class="glyphicon" id="seckill-box"></span>
                </h2>
            </div>
        </div>
    </div>
    <div id="killPhoneModal" class="modal fade">
        <div class="modal-dialog"><!--modal的对话框-->
            <div class="modal-content"><!--modal的内容--->
                <div class="modal-header"><!--modal的首部--->
                    <h3 class="modal-title text-center">
                        <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                    </h3>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-8 col-xs-offset-2">
                            <input type="text" name="killPhone"id="killPhoneKey"
                            placeholder="填写手机号码" class="form-control"/>
                        </div>
                    </div>
                </div>
                <div class="modal-footer"><!---错误信息-->
                    <div class="row">
                        <span id="killPhoneMessage" class="glyphicon"></span>
                        <button type="button"id="killPhoneBtn" class="btn btn-success">
                            <span class="glyphicon glyphicon-phone"></span>
                            Submit
                        </button>
                    </div>
                </div>
            </div>
        </div>

    </div>
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
    <script src="http://apps.bdimg.com/libs/jquery/2.0.0/jquery.min.js"></script>

    <!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
    <script src="http://apps.bdimg.com/libs/bootstrap/3.3.0/js/bootstrap.min.js"></script>

    <!--CDN 获得公共的资源http://www.bootcdn.cn/-->
    <!--CDN 并发--->
    <script src="http://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
    <script src="http://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
    <!--交互逻辑-->
<script src="/resources/script/seckill.js" type="text/javascript"></script>
<script type="text/javascript">
    $(function() {
        //EL表达式传入参数
        seckill.detail.init({
            seckillId:${seckill.seckillId},
            startTime:${seckill.startTime.time},//毫秒
            endTime:${seckill.endTime.time}
        });
    });
</script>
</body>
</html>