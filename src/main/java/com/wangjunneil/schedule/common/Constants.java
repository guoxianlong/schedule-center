package com.wangjunneil.schedule.common;

/**
 * Created by wangjun on 7/28/16.
 */
public final class Constants {
    private Constants() {}

    public static final String JD_LINK_TOKEN_URL = "https://oauth.jd.com/oauth/authorize?response_type=code&client_id={0}&redirect_uri={1}&state={2}";
    public static final String JD_REQUEST_TOKEN_URL = "https://oauth.jd.com/oauth/token?grant_type=authorization_code&client_id={0}&redirect_uri={1}&code={2}&state={3}&client_secret={4}";
    public static final String JD_REFRESH_TOKEN_URL = "https://oauth.jd.com/oauth/token?client_id={0}&client_secret={1}&grant_type=refresh_token&refresh_token={2}";
    public static final String JD_SERVICE_URL = "https://api.jd.com/routerjson";
    public static final String JD_ORDER_OPTIONAL_FIELD = "vender_remark,balance_used,payment_confirm_time,waybill,logistics_id,pin,return_order,vat_invoice_info";
    public static final String JD_SYNC_ORDER_STATE = "WAIT_SELLER_STOCK_OUT,WAIT_GOODS_RECEIVE_CONFIRM,FINISHED_L,TRADE_CANCELED";
}
