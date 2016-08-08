package com.wangjunneil.schedule.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangjun on 8/3/16.
 */
public class AccessController extends HandlerInterceptorAdapter {

    private String whitelist;

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (whitelist != null || !"".equals(whitelist)) {
            List<String> list = null;
            if (whitelist.indexOf(",") != -1) {
                String [] whites = whitelist.split(",");
                list = Arrays.asList(whites);
            } else {
                list = new ArrayList<>();
                list.add(whitelist);
            }
        }

        System.out.println("......11" + request.getRemoteAddr());
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return false;
    }
}
