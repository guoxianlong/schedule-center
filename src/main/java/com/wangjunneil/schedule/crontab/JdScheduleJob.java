package com.wangjunneil.schedule.crontab;

import com.alibaba.fastjson.JSONObject;
import com.wangjunneil.schedule.activemq.QueueMessageProducer;
import com.wangjunneil.schedule.entity.jd.JdCrmOrder;
import com.wangjunneil.schedule.service.JdFacadeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;


/**
 *
 * Created by wangjun on 8/2/16.
 */
@Component
public class JdScheduleJob {

    private static Logger log = Logger.getLogger(JdScheduleJob.class.getName());

    @Autowired
    private JdFacadeService jdFacadeService;

    @Autowired
    private QueueMessageProducer producer;

    /**
     * 定时任务,同步订单
     */
    public void syncOrder() {
        log.info("[BEG] schedule sync JD order");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 调用同步订单服务
        int whenDayBefore = -30;
        List<JdCrmOrder> orders = jdFacadeService.syncOrder(whenDayBefore);
        if (orders == null || orders.size() == 0) { // 没有新的订单增加
            log.info("it has no new orders be schedule, waiting for ......");
        } else {
            // 封装成JSON格式并发送MQ报文
            String messageJson = JSONObject.toJSONString(orders);
            producer.sendJDOrderMessage(messageJson);
            log.info("order notify message(" + orders.size() + ") send finished");
        }

        stopWatch.stop();
        log.info("[END] schedule sync JD order, waster time " + stopWatch.getTotalTimeSeconds() + " second");
    }
}
