package org.example.apitest.sqllog;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class CustomP6SpyFormatter implements MessageFormattingStrategy {

    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        if (sql != null && !sql.trim().isEmpty() &&
                !sql.contains("information_schema") &&
                !sql.contains("performance_schema") &&
                !sql.contains("mysql") &&
                !sql.toLowerCase().contains("show ") &&
                !sql.toLowerCase().contains("select @@")) {

            // Lưu SQL query vào ThreadLocal để SqlLogger có thể lấy
            SqlQueryCapture.addQuery(sql, elapsed, getCurrentEndpoint());
        }

        return String.format("ConnectionId: %d | Execution Time: %d ms | %s",
                connectionId, elapsed, sql);
    }

    private String getCurrentEndpoint() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getMethod() + " " + request.getRequestURI();
            }
        } catch (Exception e) {
            // Ignore - might be called outside of HTTP request
        }
        return "Unknown";
    }
}
