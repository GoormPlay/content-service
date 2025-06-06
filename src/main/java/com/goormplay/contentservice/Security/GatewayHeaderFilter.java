package com.goormplay.contentservice.Security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

@Slf4j
public class GatewayHeaderFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        log.info("request : 요청 들어옴");
        String fromGateway = request.getHeader("X-From-Gateway");
        log.info("request fromGateway : "+ fromGateway);
        if (!"true".equalsIgnoreCase(fromGateway)) {
            log.warn("❌ Gateway 헤더 누락: 직접 접근 시도");
            ((HttpServletResponse) res).sendError(HttpServletResponse.SC_FORBIDDEN, "접근이 허용되지 않았습니다.");
            return;
        }
        chain.doFilter(req, res); // 다음 필터로 요청 전달
    }
}