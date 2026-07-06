package com.worktime.service.impl;

import com.worktime.exception.BusinessException;
import com.worktime.service.CaptchaService;
import com.worktime.vo.CaptchaVO;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 登录验证码业务实现类：使用内存保存短期验证码，不改动数据库结构。
@Service
public class CaptchaServiceImpl implements CaptchaService {

    // 验证码有效分钟数，超过后需要重新获取。
    private static final int EXPIRE_MINUTES = 5;

    // 验证码长度。
    private static final int CODE_LENGTH = 4;

    // 验证码图片宽度。
    private static final int IMAGE_WIDTH = 128;

    // 验证码图片高度。
    private static final int IMAGE_HEIGHT = 42;

    // 去掉 0/O/1/I 等容易看混的字符，降低用户输入误判。
    private static final String CODE_CHARS = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ";

    // 随机数生成器，用于生成验证码字符和干扰线。
    private final SecureRandom random = new SecureRandom();

    // 内存验证码缓存：key 是 captchaId，value 是答案和过期时间。
    private final Map<String, CaptchaRecord> captchaMap = new ConcurrentHashMap<>();

    // 生成新的登录验证码。
    @Override
    public CaptchaVO createCaptcha() {
        clearExpiredCaptchas();

        String captchaCode = createRandomCode();
        String captchaId = UUID.randomUUID().toString();

        captchaMap.put(
                captchaId,
                new CaptchaRecord(captchaCode, LocalDateTime.now().plusMinutes(EXPIRE_MINUTES))
        );

        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaId(captchaId);
        captchaVO.setCaptchaImage(createCaptchaImage(captchaCode));
        return captchaVO;
    }

    // 校验登录验证码，成功或失败都会移除该验证码，避免重复尝试。
    @Override
    public void validateCaptcha(String captchaId, String captchaCode) {
        if (captchaId == null || captchaId.isBlank()) {
            throw new BusinessException(400, "请刷新验证码");
        }

        CaptchaRecord captchaRecord = captchaMap.remove(captchaId);
        if (captchaRecord == null || captchaRecord.expireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException(400, "验证码已过期，请刷新后重试");
        }

        if (captchaCode == null || !captchaRecord.answer().equals(captchaCode.trim().toUpperCase())) {
            throw new BusinessException(400, "验证码错误");
        }
    }

    // 生成随机字母数字验证码。
    private String createRandomCode() {
        StringBuilder codeBuilder = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int charIndex = random.nextInt(CODE_CHARS.length());
            codeBuilder.append(CODE_CHARS.charAt(charIndex));
        }

        return codeBuilder.toString();
    }

    // 把验证码字符绘制成图片，并转换成前端可直接展示的 base64 data URL。
    private String createCaptchaImage(String captchaCode) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(new Color(246, 250, 255));
            graphics.fillRoundRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 8, 8);

            drawNoiseLines(graphics);
            drawCaptchaChars(graphics, captchaCode);
            drawNoiseDots(graphics);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException exception) {
            throw new IllegalStateException("生成验证码图片失败", exception);
        } finally {
            graphics.dispose();
        }
    }

    // 绘制干扰线，增加验证码图片的基础识别难度。
    private void drawNoiseLines(Graphics2D graphics) {
        graphics.setStroke(new BasicStroke(1.3F));

        for (int i = 0; i < 8; i++) {
            graphics.setColor(randomSoftColor(130, 230));
            int startX = random.nextInt(IMAGE_WIDTH);
            int startY = random.nextInt(IMAGE_HEIGHT);
            int endX = random.nextInt(IMAGE_WIDTH);
            int endY = random.nextInt(IMAGE_HEIGHT);
            graphics.drawLine(startX, startY, endX, endY);
        }
    }

    // 绘制验证码字符，每个字符有轻微旋转和颜色差异。
    private void drawCaptchaChars(Graphics2D graphics, String captchaCode) {
        graphics.setFont(new Font("Arial", Font.BOLD, 26));

        for (int i = 0; i < captchaCode.length(); i++) {
            String currentChar = String.valueOf(captchaCode.charAt(i));
            int x = 16 + i * 27;
            int y = 29 + random.nextInt(5);
            double rotate = Math.toRadians(random.nextInt(28) - 14);

            graphics.rotate(rotate, x + 8, y - 10);
            graphics.setColor(randomSoftColor(20, 120));
            graphics.drawString(currentChar, x, y);
            graphics.rotate(-rotate, x + 8, y - 10);
        }
    }

    // 绘制少量噪点，避免图片过于干净。
    private void drawNoiseDots(Graphics2D graphics) {
        for (int i = 0; i < 36; i++) {
            graphics.setColor(randomSoftColor(120, 240));
            graphics.fillOval(random.nextInt(IMAGE_WIDTH), random.nextInt(IMAGE_HEIGHT), 2, 2);
        }
    }

    // 生成指定颜色范围内的随机颜色。
    private Color randomSoftColor(int minValue, int maxValue) {
        int range = maxValue - minValue;
        int red = minValue + random.nextInt(range);
        int green = minValue + random.nextInt(range);
        int blue = minValue + random.nextInt(range);
        return new Color(red, green, blue);
    }

    // 清理已经过期的验证码，避免内存中长期堆积无效数据。
    private void clearExpiredCaptchas() {
        LocalDateTime now = LocalDateTime.now();
        Iterator<Map.Entry<String, CaptchaRecord>> iterator = captchaMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, CaptchaRecord> entry = iterator.next();
            if (entry.getValue().expireTime().isBefore(now)) {
                iterator.remove();
            }
        }
    }

    // 验证码缓存记录：保存答案和过期时间。
    private record CaptchaRecord(String answer, LocalDateTime expireTime) {
    }
}
