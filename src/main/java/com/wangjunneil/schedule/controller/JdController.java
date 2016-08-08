package com.wangjunneil.schedule.controller;

import com.wangjunneil.schedule.entity.Page;
import com.wangjunneil.schedule.entity.jd.JdCrmOrder;
import com.wangjunneil.schedule.entity.jd.JdOrderRequest;
import com.wangjunneil.schedule.service.JdFacadeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 *
 * Created by wangjun on 7/28/16.
 */
@Controller
@RequestMapping("/jd")
public class JdController {

    /**
     * 日志定义
     */
    private static Logger log = Logger.getLogger(JdController.class.getName());

    /**
     * 京东门面服务类
     */
    @Autowired
    private JdFacadeService jdFacadeService;

    // --------------------------------------------------------------------------------------------------- public method

    /**
     * 获取京东商家运营的店铺基本信息
     *
     * @param out   响应输出流对象
     * @param resp  浏览器响应对象
     * @return  JSON格式的错误信息或者店铺基本信息
     */
    @RequestMapping(value = "/getOnlineShop.php", method = RequestMethod.GET)
    public String getOnlineShop(PrintWriter out, HttpServletResponse resp) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=UTF-8");

        String returnJson = jdFacadeService.getOnlineShop();
        out.println(returnJson);
        out.close();
        return null;
    }

    /**
     * 获取京东历史订单数据
     *
     * @param out   响应输出流对象
     * @param resp  浏览器响应对象
     * @return  JSON格式的错误信息或者店铺基本信息
     */
    @RequestMapping(value = "/getHistoryOrder.php")
    public String getHistoryOrder(PrintWriter out, HttpServletResponse resp, HttpServletRequest req) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=UTF-8");

        // 构建条件请求对象
        JdOrderRequest jdOrderRequest = new JdOrderRequest();
        jdOrderRequest.setSkuId(req.getParameter("skuId"));
        jdOrderRequest.setOrderId(req.getParameter("orderId"));
        jdOrderRequest.setOrderState(req.getParameter("orderState"));
        jdOrderRequest.setProductName(req.getParameter("productName"));
        jdOrderRequest.setTelephone(req.getParameter("telephone"));
        jdOrderRequest.setStartDate(req.getParameter("startDate"));
        jdOrderRequest.setEndDate(req.getParameter("endDate"));
        // jdOrderRequest.setWayBill(req.getParameter("wayBill")); // TODO 运单号暂时有问题

        // 构建分页对象
        Page<JdCrmOrder> page = new Page<>();
        String currentPage = req.getParameter("currentPage");
        if (currentPage == null || "".equals(currentPage))
            currentPage = "1";
        String pageSize = req.getParameter("pageSize");
        if (pageSize != null && !"".equals(pageSize))
            page.setPageSize(Integer.parseInt(pageSize));
        page.setCurrentPage(Integer.parseInt(currentPage));

        String returnJson = jdFacadeService.getHistoryOrder(jdOrderRequest, page);
        try {
            out.println(new String(returnJson.getBytes(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        out.close();
        return null;
    }

    @RequestMapping(value = "/getCrmMember.php", method = RequestMethod.GET)
    public String getCrmMember(PrintWriter out, HttpServletResponse resp, HttpServletRequest req) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=UTF-8");

        String returnJson = jdFacadeService.getCrmMember();
        out.println(returnJson);
        out.close();
        return null;
    }

    /**
     * 京东授权回调处理,接受京东传入的code,换取有效的token信息
     *
     * @param out   响应输出流对象
     * @param resp  浏览器响应对象
     * @param req   客户端请求对象
     * @return
     */
    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public String callback(PrintWriter out, HttpServletResponse resp, HttpServletRequest req) {
        resp.setCharacterEncoding("utf-8");

        // 先获取error判断是否出错
        String error = req.getParameter("error");
        if (error != null) {
            String errorDesc = req.getParameter("error_description");
            log.error("callback error, " + errorDesc);

            out.println("<script>alert('" + errorDesc + "');location.href='/console/#/';</script>");
            out.close();
            return null;
        }

        String code = req.getParameter("code");
        String state = req.getParameter("state");

        try {
            // 调用回调处理
            jdFacadeService.callback(code, state);

            // 重定向到首页
            resp.sendRedirect("/console/#/");
        } catch (Exception e) {
            log.error(e.toString());
            out.println("<script>alert('系统错误');location.href='/console/#/';</script>");
            out.close();
        }

        return null;
    }
}
