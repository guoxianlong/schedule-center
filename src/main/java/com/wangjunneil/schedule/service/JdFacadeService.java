package com.wangjunneil.schedule.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.domain.order.OrderSearchInfo;
import com.jd.open.api.sdk.domain.seller.ShopSafService.ShopJosResult;
import com.jd.open.api.sdk.request.order.OrderSopOutstorageRequest;
import com.jd.open.api.sdk.response.order.OrderSearchResponse;
import com.jd.open.api.sdk.response.order.OrderSopOutstorageResponse;
import com.jd.open.api.sdk.response.seller.SellerVenderInfoGetResponse;
import com.jd.open.api.sdk.response.seller.VenderShopQueryResponse;
import com.wangjunneil.schedule.common.Constants;
import com.wangjunneil.schedule.entity.Job;
import com.wangjunneil.schedule.entity.Page;
import com.wangjunneil.schedule.entity.jd.*;
import com.wangjunneil.schedule.utility.DateTimeUtil;
import com.wangjunneil.schedule.utility.HttpUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <code>JdFacadeService</code>类提供简明一致的接口处理,隐藏子系统的复杂性,主要封装数据库操作及接口API调用的部分
 * 逻辑代码
 *
 * @author <a href="mailto:wangjunneil@gmail.com">jun.wang</a>
 * @see JdInnerService
 * @see JdApiService
 */
@Service
public class JdFacadeService {

    /**
     * 日志定义
     */
    private static Logger log = Logger.getLogger(JdFacadeService.class.getName());

    /**
     * 京东业务层服务接口
     */
    @Autowired
    private JdInnerService jdInnerService;

    /**
     * 京东API层服务接口
     */
    @Autowired
    private JdApiService jdApiService;

    @Autowired
    private JobService jobService;

    // --------------------------------------------------------------------------------------------------- public method

    /**
     * 获取京东商家运营的店铺基本信息
     *
     * @return  JSON格式的错误信息或者店铺基本信息
     */
    public String getOnlineShop() {
        // 从mongodb中获取京东店铺信息
        JdOnlineStore onlineStore = jdInnerService.getOnlineShop();

        String returnJson = null;
        // 若mongodb没有京东店铺信息则执行接口调用
        if (onlineStore == null) {
            try {
                // 调用店铺信息查询接口
                VenderShopQueryResponse shopQueryResponse = jdApiService.shopQueryRequest();
                ShopJosResult result = shopQueryResponse.getShopJosResult();

                // 组装内部实体对象
                onlineStore = new JdOnlineStore();
                onlineStore.setBrief(result.getBrief());
                onlineStore.setCate_main(result.getCategoryMain());
                onlineStore.setCate_main_name(result.getShopName());
                onlineStore.setLogo_url(result.getLogoUrl());
                onlineStore.setOpen_time(result.getOpenTime());
                onlineStore.setShop_id(result.getShopId());
                onlineStore.setShop_name(result.getShopName());
                onlineStore.setVender_id(result.getVenderId());

                // 调用查询商家基本信息接口以获取商家类型
                SellerVenderInfoGetResponse venderInfoGetResponse = jdApiService.venderInfoRequest();
                int colType = venderInfoGetResponse.getVenderInfoResult().getColType();
                onlineStore.setCol_type(colType);

                // 存储到mongodb中为下次使用
                jdInnerService.addOnlineShop(onlineStore);
                returnJson = JSONObject.toJSONString(onlineStore);
            } catch (JdException e) {
                log.error(e.toString());
                returnJson = "{\"error\":\"调用京东接口错误\",\"errorMessage\":\"" + e.toString() + "\"}";
            }
        }

        returnJson = JSONObject.toJSONString(onlineStore);
        return returnJson;
    } // end method getOnlineShop

    public String getCrmMember() {


        return null;
    } // end method getOnlineShop

    /**
     * 京东订单出库服务
     *
     * @param request 订单出库请求对象
     * @return JSON格式的错误信息或者出库结果
     */
    public String orderOutStock(OrderSopOutstorageRequest request) {
        String returnJson = null;
        try {
            OrderSopOutstorageResponse response = jdApiService.orderOutStock(request);
            returnJson = response.getMsg();
        } catch (JdException e) {
            log.error(e.toString());
            returnJson = "{\"error\":\"调用京东接口错误\",\"errorMessage\":\"" + e.getMessage() + "\"}";
        }
        return returnJson;
    } // end method getOnlineShop

    public String getHistoryOrder(JdOrderRequest jdOrderRequest, Page<JdCrmOrder> page) {
        Page<JdCrmOrder> returnPage = jdInnerService.getHistoryOrder(jdOrderRequest, page);
        return JSONObject.toJSONString(returnPage);
    }

    public List<JdCrmOrder> syncOrder(int whenDayBefore) {
        // 调用检索订单接口服务
        OrderSearchResponse orderSearchResponse = jdApiService.orderSearchReuest(whenDayBefore);
        if (orderSearchResponse == null)
            return null;

        /* 存储等待出库的订单链 */
        List<JdCrmOrder> waitOutStockOrders = new ArrayList<>();
        /* 存储等待出库、确认收货、完成和取消的订单链 */
        List<JdCrmOrder> crmOrders = new ArrayList<>();

        JdCrmOrder jdCrmOrder = null;
        String orderState = null;
        for (OrderSearchInfo orderSearchInfo : orderSearchResponse.getOrderInfoResult().getOrderInfoList()) {
            jdCrmOrder = new JdCrmOrder();
            jdCrmOrder.setOrder_id(orderSearchInfo.getOrderId());
            jdCrmOrder.setOrder_source(orderSearchInfo.getOrderSource());
            jdCrmOrder.setCustoms(orderSearchInfo.getCustoms());
            jdCrmOrder.setCustoms_model(orderSearchInfo.getCustomsModel());
            jdCrmOrder.setVender_id(orderSearchInfo.getVenderId());
            jdCrmOrder.setPay_type(orderSearchInfo.getPayType());
            jdCrmOrder.setOrder_total_price(orderSearchInfo.getOrderTotalPrice());
            jdCrmOrder.setOrder_seller_price(orderSearchInfo.getOrderSellerPrice());
            jdCrmOrder.setOrder_payment(orderSearchInfo.getOrderPayment());
            jdCrmOrder.setFreight_price(orderSearchInfo.getFreightPrice());
            jdCrmOrder.setSeller_discount(orderSearchInfo.getSellerDiscount());
            jdCrmOrder.setOrder_state_remark(orderSearchInfo.getOrderStateRemark());
            jdCrmOrder.setDelivery_type(orderSearchInfo.getDeliveryType());
            jdCrmOrder.setInvoice_info(orderSearchInfo.getInvoiceInfo());
            jdCrmOrder.setOrder_remark(orderSearchInfo.getOrderRemark());
            jdCrmOrder.setOrder_start_time(DateTimeUtil.parseDateString(orderSearchInfo.getOrderStartTime()));
            jdCrmOrder.setOrder_end_time(orderSearchInfo.getOrderEndTime());
            jdCrmOrder.setModified(orderSearchInfo.getModified());
            jdCrmOrder.setVender_remark(orderSearchInfo.getVenderRemark());
            jdCrmOrder.setBalance_used(orderSearchInfo.getBalanceUsed());
            jdCrmOrder.setPayment_confirm_time(orderSearchInfo.getPaymentConfirmTime());
            jdCrmOrder.setWaybill(orderSearchInfo.getWaybill());
            jdCrmOrder.setLogistics_id(orderSearchInfo.getLogisticsId());
            jdCrmOrder.setParent_order_id(orderSearchInfo.getParentOrderId());
            jdCrmOrder.setPin(orderSearchInfo.getPin());
            jdCrmOrder.setReturn_order(orderSearchInfo.getReturnOrder());
            jdCrmOrder.setConsignee_info(orderSearchInfo.getConsigneeInfo());
            jdCrmOrder.setVat_invoice_info(orderSearchInfo.getVatInvoiceInfo());
            jdCrmOrder.setItem_info_list(orderSearchInfo.getItemInfoList());
            jdCrmOrder.setCoupon_detail_list(orderSearchInfo.getCouponDetailList());

            orderState = orderSearchInfo.getOrderState();
            jdCrmOrder.setOrder_state(orderState);

            // 等待出库的订单需要同步到中台
            if ("WAIT_SELLER_STOCK_OUT".equals(orderState))
                waitOutStockOrders.add(jdCrmOrder);
            crmOrders.add(jdCrmOrder);
        }

        // 更新同步的京东订单到mongodb
        int size = crmOrders.size();
        if (size != 0) {
            jdInnerService.addSyncOrder(crmOrders);
            log.info("the new batch of orders(" + crmOrders.size() + ") store completion");

            // int waitOutStockSize = waitOutStockOrders.size();
            // long waitConfirmSize = crmOrders.stream().filter(p -> p.getOrder_state().equals("WAIT_GOODS_RECEIVE_CONFIRM")).count();
            // long finishSize = crmOrders.stream().filter(p -> p.getOrder_state().equals("FINISHED_L")).count();
            // long cancelSize = crmOrders.stream().filter(p -> p.getOrder_state().equals("TRADE_CANCELED")).count();

            // log.info("");
        }

        return waitOutStockOrders;
    } // end method syncOrder

    /**
     * 京东授权回调处理,接受京东传入的code,换取有效的token信息
     *
     * @param code  回调code码
     * @param state 续传state
     */
    public void callback(String code, String state) {
        // 从mongodb获取授权基本信息
        JdAuthorize jdAuthorize = jdInnerService.getAuthorize();
        String appKey = jdAuthorize.getAppKey();
        String appSecret = jdAuthorize.getAppSecret();
        String callbackUrl = jdAuthorize.getCallback();

        // 拼接请求地址
        String tokenUrl = MessageFormat.format(Constants.JD_REQUEST_TOKEN_URL, appKey, callbackUrl, code, state, appSecret);
        // Get请求获取token
        String returnJson = HttpUtil.get(tokenUrl);

        // token入库
        JdAccessToken jdAccessToken = JSONObject.parseObject(returnJson, JdAccessToken.class);
        jdInnerService.addAccessToken(jdAccessToken);
    }
}
