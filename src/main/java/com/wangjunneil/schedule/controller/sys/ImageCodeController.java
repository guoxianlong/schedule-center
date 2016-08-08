package com.wangjunneil.schedule.controller.sys;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 *
 * Created by wangjun on 8/8/16.
 */
@Controller
@RequestMapping("/sys")
public class ImageCodeController {

    public static final int IMAGE_CODE_HEIGHT = 40;
    public static final int IMAGE_CODE_WEIGHT = 120;
    public static final int IMAGE_LINE_COUNT = 10;
    public static final String RANDOM_STRING = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @RequestMapping(value = "/imageCode.php")
    public String imageCode(PrintWriter out, HttpServletResponse response, HttpServletRequest request) throws IOException {
        // 创建图片缓冲区对象
        BufferedImage image = new BufferedImage(IMAGE_CODE_WEIGHT, IMAGE_CODE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        // 填充背景色为白色
        g.setColor(Color.white);
        g.fillRect(0, 0, IMAGE_CODE_WEIGHT, IMAGE_CODE_HEIGHT);

        // 设置验证码图片边框颜色
        g.setColor(Color.RED);
        g.drawRect(0, 0, IMAGE_CODE_WEIGHT - 1, IMAGE_CODE_HEIGHT - 1);

        // 图片写入内容定义
        Random r = new Random();
        int x = 14; // 定义第一个字符的x坐标
        for (int i = 0; i < 4; i++) {
            // 获取随机内容字符
            char ch = RANDOM_STRING.charAt(r.nextInt(RANDOM_STRING.length()));

            // 设置字体样式
            g.setFont(new Font("宋体", Font.BOLD, 28));
            g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));

            // 设置字符旋转
            int k = r.nextInt(90);
            double rad = (90 - 60) * Math.PI / 180;
            g.rotate(rad, x, 25);

            // 内容字符写到缓冲区
            g.drawString(Character.toString(ch), x, 25);

            // 旋转复原
            g.rotate(-rad, x, 25);

            // 下一个字符x坐标
            x = x + 25;
        }

        // 设置干扰线
        for (int i = 0; i < IMAGE_LINE_COUNT; i++) {
            g.setColor(new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255)));
            g.drawLine(r.nextInt(IMAGE_CODE_WEIGHT), r.nextInt(IMAGE_CODE_HEIGHT),
                r.nextInt(IMAGE_CODE_WEIGHT), r.nextInt(IMAGE_CODE_HEIGHT));
        }

        // 输出到浏览器中
        ImageIO.write(image, "png", response.getOutputStream());
        return null;
    }
}
