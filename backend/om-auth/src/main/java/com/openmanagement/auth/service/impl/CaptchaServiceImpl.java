package com.openmanagement.auth.service.impl;

import com.openmanagement.auth.dto.CaptchaResponse;
import com.openmanagement.auth.service.CaptchaService;
import com.openmanagement.common.constant.CommonConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CHARS = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    @Override
    public CaptchaResponse generateCaptcha() {
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        String code = generateCode();
        String imageBase64 = generateImage(code);
        stringRedisTemplate.opsForValue().set(
                CommonConstants.CAPTCHA_CACHE_KEY + captchaKey,
                code.toLowerCase(),
                5,
                TimeUnit.MINUTES
        );
        return new CaptchaResponse(captchaKey, "data:image/png;base64," + imageBase64);
    }

    @Override
    public boolean validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) return false;
        String redisKey = CommonConstants.CAPTCHA_CACHE_KEY + captchaKey;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) return false;
        boolean valid = storedCode.equalsIgnoreCase(captchaCode);
        if (valid) {
            stringRedisTemplate.delete(redisKey);
        }
        return valid;
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    private String generateImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        Random random = new Random();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 5; i++) {
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT),
                    random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }
        Font font = new Font("Arial", Font.BOLD, 24);
        g.setFont(font);
        int charWidth = WIDTH / CODE_LENGTH;
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.drawString(String.valueOf(code.charAt(i)), i * charWidth + 5, 28 + random.nextInt(8) - 4);
        }
        g.dispose();
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate captcha image", e);
        }
    }
}
