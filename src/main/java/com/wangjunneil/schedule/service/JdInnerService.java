package com.wangjunneil.schedule.service;

import com.wangjunneil.schedule.entity.Page;
import com.wangjunneil.schedule.entity.jd.*;
import com.wangjunneil.schedule.utility.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by wangjun on 7/28/16.
 */
@Service
public class JdInnerService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public JdAccessToken getAccessToken() {
        Query query = new Query(Criteria.where("platform").is("jd"));
        JdAccessToken jdAccessToken = mongoTemplate.findOne(query, JdAccessToken.class);
        return jdAccessToken;
    }

    public void addAccessToken(JdAccessToken jdAccessToken) {
        // 计算token到期时间
        long time = Long.parseLong(jdAccessToken.getTime());
        int expire_in = jdAccessToken.getExpires_in();
        Date expireDate = DateTimeUtil.getExpireDate(time, expire_in);
        jdAccessToken.setExpire_Date(expireDate);

        Query query = new Query(Criteria.where("platform").is("jd"));
        Update update = new Update()
            .set("access_token", jdAccessToken.getAccess_token())
            .set("expires_in", jdAccessToken.getExpires_in())
            .set("refresh_token", jdAccessToken.getRefresh_token())
            .set("token_type", jdAccessToken.getToken_type())
            .set("time", jdAccessToken.getTime())
            .set("uid", jdAccessToken.getUid())
            .set("user_nick", jdAccessToken.getUser_nick())
            .set("expire_Date", jdAccessToken.getExpire_Date());
        mongoTemplate.upsert(query, update, JdAccessToken.class);
    }

    public JdAuthorize getAuthorize() {
        Query query = new Query(Criteria.where("platform").is("jd"));
        JdAuthorize jdAuthorize = mongoTemplate.findOne(query, JdAuthorize.class);
        return jdAuthorize;
    }

    public JdOnlineStore getOnlineShop() {
        Query query = new Query(Criteria.where("platform").is("jd"));
        JdOnlineStore jdOnlineStore = mongoTemplate.findOne(query, JdOnlineStore.class);
        return jdOnlineStore;
    }

    public void addOnlineShop(JdOnlineStore jdOnlineStore) {
        mongoTemplate.insert(jdOnlineStore);
    }

    public void addSyncOrder(List<JdCrmOrder> orders) {
        for (JdCrmOrder order : orders) {
            Query query = new Query(Criteria.where("order_id").is(order.getOrder_id()));
            Update update = new Update().set("_class", JdCrmOrder.class.getName())
                .set("order_source", order.getOrder_source())
                .set("customs", order.getCustoms())
                .set("customs_model", order.getCustoms_model())
                .set("vender_id", order.getVender_id())
                .set("pay_type", order.getPay_type())
                .set("order_total_price", order.getOrder_total_price())
                .set("order_seller_price", order.getOrder_seller_price())
                .set("order_payment", order.getOrder_payment())
                .set("freight_price", order.getFreight_price())
                .set("seller_discount", order.getSeller_discount())
                .set("order_state", order.getOrder_state())
                .set("delivery_type", order.getDelivery_type())
                .set("invoice_info", order.getInvoice_info())
                .set("order_remark", order.getOrder_remark())
                .set("order_start_time", order.getOrder_start_time())
                .set("modified", order.getModified())
                .set("consignee_info", order.getConsignee_info())
                .set("item_info_list", order.getItem_info_list())
                .set("coupon_detail_list", order.getCoupon_detail_list());
            mongoTemplate.upsert(query, update, JdCrmOrder.class);
        }
    }

    public Page<JdCrmOrder> getHistoryOrder(JdOrderRequest jdOrderRequest, Page<JdCrmOrder> page) {
        Criteria criatira = new Criteria();

        String orderId = jdOrderRequest.getOrderId();
        if (orderId != null && !"".equals(orderId))
            criatira.andOperator(Criteria.where("order_id").is(orderId));
        String orderState = jdOrderRequest.getOrderState();
        if (orderState != null && !"".equals(orderState))
            criatira.andOperator(Criteria.where("order_state").is(orderState));
        String telephone = jdOrderRequest.getTelephone();
        if (telephone != null && !"".equals(telephone))
            criatira.andOperator(Criteria.where("consignee_info.telephone").is(telephone));
        String startDate = jdOrderRequest.getStartDate();
        String endDate = jdOrderRequest.getEndDate();
        if (startDate != null && !"".equals(startDate) && endDate != null && !"".equals(endDate))
            criatira.andOperator(Criteria.where("order_start_time")
                .gte(DateTimeUtil.formatDateString(startDate, "yyyy-MM-dd HH:mm:ss"))
                .lt(DateTimeUtil.formatDateString(endDate, "yyyy-MM-dd HH:mm:ss")));
        String productName = jdOrderRequest.getProductName();
        if (productName != null && !"".equals(productName))
            criatira.andOperator(Criteria.where("item_info_list").elemMatch(Criteria.where("skuName").regex(".*?" + productName + ".*")));
        String skuId = jdOrderRequest.getSkuId();
        if (skuId != null && !"".equals(skuId))
            criatira.andOperator(Criteria.where("item_info_list").elemMatch(Criteria.where("skuId").is(skuId)));

        // 查询条件定义
        Query query = new Query(criatira).limit(page.getPageSize()).skip(page.getCurrentPage());
        // 计算总记录数
        long count = mongoTemplate.count(query, JdCrmOrder.class);
        page.setTotalNum(count);

        List<JdCrmOrder> orders = mongoTemplate.find(query, JdCrmOrder.class);
        page.setPageDataList(orders);

        return page;
    }
}
