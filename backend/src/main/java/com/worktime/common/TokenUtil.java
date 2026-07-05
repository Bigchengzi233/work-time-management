package com.worktime.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;

// token 工具类：负责生成和校验登录 token。
@Component
public class TokenUtil {

    // token 签名密钥，从配置文件读取。
    private final String tokenSecret;

    // token 有效小时数，从配置文件读取。
    private final long expireHours;

    public TokenUtil(
            @Value("${app.auth.token-secret}") String tokenSecret,
            @Value("${app.auth.token-expire-hours}") long expireHours) {
        this.tokenSecret = tokenSecret;
        this.expireHours = expireHours;
    }

    // 根据用户编号、角色和部门生成 token。
    public String generateToken(Integer userId, String userRole, Integer deptId) {
        long expireAt = System.currentTimeMillis() + Duration.ofHours(expireHours).toMillis();
        String payload = userId + ":" + userRole + ":" + deptId + ":" + expireAt;
        String encodedPayload = base64UrlEncode(payload.getBytes(StandardCharsets.UTF_8));
        String signature = sign(encodedPayload);
        return encodedPayload + "." + signature;
    }

    // 解析并校验 token，校验失败返回 null。
    public TokenPayload parseToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) {
            return null;
        }

        String encodedPayload = parts[0];
        String signature = parts[1];
        String expectedSignature = sign(encodedPayload);

        if (!MessageDigest.isEqual(signature.getBytes(StandardCharsets.UTF_8), expectedSignature.getBytes(StandardCharsets.UTF_8))) {
            return null;
        }

        try {
            String payload = new String(Base64.getUrlDecoder().decode(encodedPayload), StandardCharsets.UTF_8);
            String[] payloadParts = payload.split(":");
            if (payloadParts.length != 4) {
                return null;
            }

            Integer userId = Integer.valueOf(payloadParts[0]);
            String userRole = payloadParts[1];
            Integer deptId = Integer.valueOf(payloadParts[2]);
            Long expireAt = Long.valueOf(payloadParts[3]);

            if (expireAt < System.currentTimeMillis()) {
                return null;
            }

            return new TokenPayload(userId, userRole, deptId, expireAt);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    // 对 payload 进行 HMAC-SHA256 签名。
    private String sign(String encodedPayload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(tokenSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(encodedPayload.getBytes(StandardCharsets.UTF_8));
            return base64UrlEncode(signatureBytes);
        } catch (Exception exception) {
            throw new IllegalStateException("生成 token 签名失败", exception);
        }
    }

    // 使用 URL 安全的 Base64 编码，避免 token 中出现特殊字符。
    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
