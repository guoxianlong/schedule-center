package com.wangjunneil.schedule.controller.sys;

import com.alibaba.fastjson.JSON;
import com.wangjunneil.schedule.common.Constants;
import com.wangjunneil.schedule.entity.jd.JdAccessToken;
import com.wangjunneil.schedule.entity.jd.JdAuthorize;
import com.wangjunneil.schedule.entity.Menu;
import com.wangjunneil.schedule.service.JdInnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by wangjun on 7/28/16.
 */
@Controller
@RequestMapping("/menu")
public class MenuController {

    @Autowired
    private JdInnerService jdInnerService;

    @RequestMapping(value = "/policy.php", method = RequestMethod.GET)
    public String getToken(PrintWriter out, HttpServletResponse resp) {
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=UTF-8");

        List<Menu> menus = new ArrayList<>();

        Menu dashboardMenu = new Menu();
        dashboardMenu.setMenuName("Dashboard");
        dashboardMenu.setMenuAction("#/");
        dashboardMenu.setMenuIcon("fa fa-dashboard");
        menus.add(dashboardMenu);

        JdAccessToken jdAToken = jdInnerService.getAccessToken();
        Menu jdMenu = new Menu();
        if (jdAToken == null) {
            jdMenu.setMenuName("JD Authorize");
            jdMenu.setMenuIcon("fa fa-expeditedssl");

            JdAuthorize jdAuthorize = jdInnerService.getAuthorize();
            String action = MessageFormat.format(Constants.JD_LINK_TOKEN_URL, jdAuthorize.getAppKey(), jdAuthorize.getCallback(), "112233");
            jdMenu.setMenuAction(action);
            menus.add(jdMenu);
        } else {
            jdMenu.setMenuName("JD Platform");
            jdMenu.setMenuIcon("fa fa-connectdevelop");

            Menu storeMenu = new Menu("Online Running Store", "#/jdStore");
            Menu orderMenu = new Menu("History Order Center", "#/jdOrder");
            Menu partyMenu = new Menu("History Party Center", "#/jdParty");

            List<Menu> jdMenus = new ArrayList<>();
            jdMenus.add(storeMenu);
            jdMenus.add(orderMenu);
            jdMenus.add(partyMenu);
            jdMenu.setMenu(jdMenus);

            menus.add(jdMenu);
        }

        Menu configMenu = new Menu("Configuration Center", "#/config", "fa fa-gears");
        menus.add(configMenu);

        out.println(JSON.toJSONString(menus));
        out.close();
        return null;
    }
}
