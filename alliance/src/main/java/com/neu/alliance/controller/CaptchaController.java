package com.neu.alliance.controller;
import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Title: CaptchaController
 * @Author  曦
 * @Date  2025/6/17 16:27
 * @description:
*/


@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class CaptchaController {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_COUNT = 4;  // 验证码字符数
    private static final int FONT_HEIGHT = 30;

    @GetMapping("/captcha/image")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 生成随机验证码字符串
        String captcha = generateCaptchaCode(CODE_COUNT);

        // 保存验证码到session，后续注册时验证
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captcha);

        // 创建图片
        BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();

        // 背景填充
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体
        Font font = new Font("Arial", Font.BOLD, FONT_HEIGHT);
        g.setFont(font);

        // 画干扰线
        drawInterferenceLines(g);

        // 画验证码字符
        drawCaptchaChars(g, captcha);

        // 禁止缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        // 设置响应内容类型为图片
        response.setContentType("image/png");

        // 写图片数据到响应输出流
        ImageIO.write(bufferedImage, "png", response.getOutputStream());

        g.dispose();
    }

    // 生成随机字符串，数字+大写字母
    private String generateCaptchaCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for(int i=0; i<length; i++){
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // 绘制验证码字符
    private void drawCaptchaChars(Graphics2D g, String captcha) {
        g.setColor(Color.BLACK);
        int x = 15;
        int y = 30;
        for (int i = 0; i < captcha.length(); i++) {
            // 旋转少量角度，避免机器识别
            double angle = (Math.random() - 0.5) * 0.4;  // -0.2 ~ 0.2 弧度
            g.rotate(angle, x, y);
            g.drawString(String.valueOf(captcha.charAt(i)), x, y);
            g.rotate(-angle, x, y);
            x += 25;
        }
    }

    // 画干扰线
    private void drawInterferenceLines(Graphics2D g) {
        Random random = new Random();
        g.setColor(Color.GRAY);
        for (int i = 0; i < 5; i++) {
            int xStart = random.nextInt(WIDTH);
            int yStart = random.nextInt(HEIGHT);
            int xEnd = random.nextInt(WIDTH);
            int yEnd = random.nextInt(HEIGHT);
            g.drawLine(xStart, yStart, xEnd, yEnd);
        }
    }
}

