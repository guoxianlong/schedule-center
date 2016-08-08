package com.wangjunneil.schedule.service;

import com.alibaba.fastjson.JSONObject;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.order.OrderSearchRequest;
import com.jd.open.api.sdk.request.order.OrderSopOutstorageRequest;
import com.jd.open.api.sdk.request.seller.SellerVenderInfoGetRequest;
import com.jd.open.api.sdk.request.seller.VenderShopQueryRequest;
import com.jd.open.api.sdk.response.order.OrderSearchResponse;
import com.jd.open.api.sdk.response.order.OrderSopOutstorageResponse;
import com.jd.open.api.sdk.response.seller.SellerVenderInfoGetResponse;
import com.jd.open.api.sdk.response.seller.VenderShopQueryResponse;
import com.wangjunneil.schedule.common.Constants;
import com.wangjunneil.schedule.entity.Job;
import com.wangjunneil.schedule.entity.jd.JdAccessToken;
import com.wangjunneil.schedule.entity.jd.JdAuthorize;
import com.wangjunneil.schedule.utility.DateTimeUtil;
import com.wangjunneil.schedule.utility.HttpUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;

/**
 * Created by wangjun on 8/1/16.
 */
@Service
public class JdApiService {

    /**
     * 日志定义
     */
    private static Logger log = Logger.getLogger(JdApiService.class.getName());

    @Autowired
    private JdInnerService jdInnerService;

    @Autowired
    private JobService jobService;

    public VenderShopQueryResponse shopQueryRequest() throws JdException {
        VenderShopQueryRequest shopQueryRequest = new VenderShopQueryRequest();
        VenderShopQueryResponse shopQueryResponse = getJdClient().execute(shopQueryRequest);
        return shopQueryResponse;
    }

    public SellerVenderInfoGetResponse venderInfoRequest() throws JdException {
        SellerVenderInfoGetRequest venderInfoGetRequest = new SellerVenderInfoGetRequest();
        SellerVenderInfoGetResponse venderInfoGetResponse = getJdClient().execute(venderInfoGetRequest);
        return venderInfoGetResponse;
    }

    public OrderSearchResponse orderSearchReuest(int whenDayBefore) {
        String nowTime = DateTimeUtil.nowDateString("yyyy-MM-dd HH:mm:ss");
        String condTime = null;

        Job job = jobService.getJob();
        if (job == null) {
            // 首次同步, 以当前时间向前whenDayBefore天为开始时间, 当前时间为截止时间
            condTime = DateTimeUtil.preDateString("yyyy-MM-dd HH:mm:ss", whenDayBefore);
        } else {
            // 非首次同步, 上次同步时间为开始时间, 当前时间为截止时间
            Date preExecuteTime = job.getExecuteTime();
            condTime = DateTimeUtil.dateFormat(preExecuteTime, "yyyy-MM-dd HH:mm:ss");
        }

        OrderSearchRequest request = new OrderSearchRequest();
        request.setStartDate(condTime);
        request.setEndDate(nowTime);
        request.setDateType("2");
        request.setOrderState(Constants.JD_SYNC_ORDER_STATE);  // 只同步等待出库的订单
        // request.setOptionalFields(Constants.JD_ORDER_OPTIONAL_FIELD);

        // TODO 这里咱未考虑超出100条订单的情况
        // 首次同步时间为一天的数据, 默认认为一天的交易量没有达到100个订单数目
        // 非首次同步为5分钟的订单量, 默认认为5分钟内不能达到100个订单数目
        request.setPage("1");
        request.setPageSize("100");

        Date now = new Date();
        job = new Job("jd", "syncOrder", now);
        try {
            OrderSearchResponse orderSearchResponse = getJdClient().execute(request);
            String code = orderSearchResponse.getCode();
            if (!"0".equals(code))
                throw new JdException(orderSearchResponse.getEnDesc());

            job.setStatus("success");
            return orderSearchResponse;
        } catch (JdException e) {
            log.error(e.toString());

            job.setStatus("failure");
            job.setMsg(e.getMessage());

            return null;
        } finally {
            jobService.addJob(job);
            log.info("job update. " + request.getStartDate() + " ~ " + request.getEndDate());
        }
    }

    public OrderSopOutstorageResponse orderOutStock(OrderSopOutstorageRequest request) throws JdException {
        OrderSopOutstorageResponse outstorageResponse = getJdClient().execute(request);
        String code = outstorageResponse.getCode();
        if (!"0".equals(code))
            throw new JdException(outstorageResponse.getEnDesc());
        return outstorageResponse;
    }


    private JdClient getJdClient() throws JdException {
        JdAccessToken token = jdInnerService.getAccessToken();
        JdAuthorize authorize = jdInnerService.getAuthorize();

        if (token == null) {
            log.error("jd token_info is empty, authorize has not finished.");
            throw new JdException("商户还未进行授权操作");
        }

        String refreshToken = token.getRefresh_token();
        String appKey = authorize.getAppKey();
        String appSecret = authorize.getAppSecret();

        Date now = new Date();
        Date expireDate = token.getExpire_Date();
        if (now.before(expireDate)) {   // 若当前时间小于失效时间则刷新token
            String tokenUrl = MessageFormat.format(Constants.JD_REFRESH_TOKEN_URL, appKey, appSecret, refreshToken);
            String returnJson = HttpUtil.get(tokenUrl);
            token = JSONObject.parseObject(returnJson, JdAccessToken.class);
            jdInnerService.addAccessToken(token);
        }

        String accessToken = token.getAccess_token();
        JdClient jdClient = new DefaultJdClient(Constants.JD_SERVICE_URL, accessToken, appKey, appSecret);
        return jdClient;
    }
}
