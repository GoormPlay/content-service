package com.goormplay.contentservice.Security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public class AuthUtil {
    public static String getMemberId(Authentication authentication) {
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof Map<?, ?> map) {
            Object memberId = map.get("memberId");
            if (memberId instanceof String) {
                return (String) memberId;
            }
        }
        return null;
    }
}
