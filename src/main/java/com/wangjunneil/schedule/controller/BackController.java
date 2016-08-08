package com.wangjunneil.schedule.controller;

import com.jd.open.api.sdk.request.order.OrderSopOutstorageRequest;
import com.wangjunneil.schedule.service.JdFacadeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * Created by wangjun on 8/3/16.
 */
@Controller
@RequestMapping("/b")
public class BackController {

    private static Logger log = Logger.getLogger(BackController.class.getName());

    /**
     * 京东门面服务类
     */
    @Autowired
    private JdFacadeService jdFacadeService;

    @RequestMapping(value = "/outStock", method = RequestMethod.GET)
    public String getOnlineShop(PrintWriter out, HttpServletResponse resp, HttpServletRequest req) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=UTF-8");

        String logisticsId = req.getParameter("logisticsId");
        String wayBill = req.getParameter("wayBill");
        String orderId = req.getParameter("orderId");
        String tradeNo = req.getParameter("tradeNo");

        OrderSopOutstorageRequest request = new OrderSopOutstorageRequest();
        request.setLogisticsId(logisticsId);
        request.setWaybill(wayBill);
        request.setOrderId(orderId);
        request.setTradeNo(tradeNo);

        String returnJson = jdFacadeService.orderOutStock(request);
        out.println(returnJson);
        out.close();
        return null;
    }
}
