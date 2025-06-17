package org.example.apitest.sqllog;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
public class SqlLogger {

    @Value("${sql.logging.csv.path:logs/sql_queries.csv}")
    private String csvFilePath;

    private final AtomicInteger counter = new AtomicInteger(1);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Bắt tất cả các controller methods trong package của bạn
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // Khởi tạo ThreadLocal để capture SQL queries
        SqlQueryCapture.initThread();

        // Lấy thông tin về request
        HttpServletRequest request = null;
        String requestBody = null;
        String endpoint = "Unknown";
        String httpMethod = "Unknown";

        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
                endpoint = request.getRequestURI();
                httpMethod = request.getMethod();

                // Lấy request body từ args
                Object[] args = joinPoint.getArgs();
                for (Object arg : args) {
                    if (arg != null && !(arg instanceof HttpServletRequest) &&
                            !(arg instanceof HttpServletResponse) &&
                            !isPrimitiveOrWrapper(arg.getClass()) &&
                            !arg.getClass().equals(String.class)) {
                        try {
                            requestBody = objectMapper.writeValueAsString(arg);
                            break;
                        } catch (Exception e) {
                            // Ignore
                        }
                    }
                }

                // Nếu không có request body, lấy query parameters
                if (requestBody == null && request.getQueryString() != null) {
                    requestBody = "Query params: " + request.getQueryString();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting request details: " + e.getMessage());
        }

        // Lấy tên API từ method name hoặc annotation
        String apiName = getApiName(joinPoint);

        // Thực thi method
        Object result = null;
        boolean success = true;
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            success = false;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            // Lấy response body
            String responseBody = null;
            if (result != null) {
                try {
                    responseBody = objectMapper.writeValueAsString(result);
                    // Giới hạn độ dài response để tránh CSV quá lớn
                    if (responseBody.length() > 500) {
                        responseBody = responseBody.substring(0, 500) + "...";
                    }
                } catch (Exception e) {
                    responseBody = "Response: " + result.getClass().getSimpleName();
                }
            }

            // Lấy tất cả SQL queries đã được capture
            List<SqlQueryCapture.SqlQueryInfo> sqlQueries = SqlQueryCapture.getQueries();

            // Log từng SQL query vào CSV
            if (sqlQueries.isEmpty()) {
                // Nếu không có SQL query nào, vẫn log API call
                logApiCallToCSV(
                        apiName,
                        httpMethod + " " + endpoint,
                        "-- No SQL queries executed",
                        requestBody != null ? requestBody : "No request body",
                        responseBody != null ? responseBody : (success ? "Thành công" : "Lỗi: " + errorMessage),
                        executionTime,
                        success,
                        errorMessage
                );
            } else {
                // Log từng SQL query
                for (SqlQueryCapture.SqlQueryInfo queryInfo : sqlQueries) {
                    logApiCallToCSV(
                            apiName,
                            httpMethod + " " + endpoint,
                            queryInfo.getSql(),
                            requestBody != null ? requestBody : "No request body",
                            responseBody != null ? responseBody : (success ? "Thành công" : "Lỗi: " + errorMessage),
                            queryInfo.getExecutionTime(),
                            success,
                            errorMessage
                    );
                }
            }

            // Dọn dẹp ThreadLocal
            SqlQueryCapture.clearThread();
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz.equals(Boolean.class) || clazz.equals(Integer.class) ||
                clazz.equals(Character.class) || clazz.equals(Byte.class) ||
                clazz.equals(Short.class) || clazz.equals(Double.class) ||
                clazz.equals(Long.class) || clazz.equals(Float.class);
    }

    private String getApiName(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // Thử lấy tên từ các annotation
            if (method.isAnnotationPresent(RequestMapping.class)) {
                String name = method.getAnnotation(RequestMapping.class).name();
                return !name.isEmpty() ? name : method.getName();
            } else if (method.isAnnotationPresent(GetMapping.class)) {
                String name = method.getAnnotation(GetMapping.class).name();
                return !name.isEmpty() ? name : method.getName();
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                String name = method.getAnnotation(PostMapping.class).name();
                return !name.isEmpty() ? name : method.getName();
            } else if (method.isAnnotationPresent(PutMapping.class)) {
                String name = method.getAnnotation(PutMapping.class).name();
                return !name.isEmpty() ? name : method.getName();
            } else if (method.isAnnotationPresent(DeleteMapping.class)) {
                String name = method.getAnnotation(DeleteMapping.class).name();
                return !name.isEmpty() ? name : method.getName();
            } else if (method.isAnnotationPresent(PatchMapping.class)) {
                String name = method.getAnnotation(PatchMapping.class).name();
                return !name.isEmpty() ? name : method.getName();
            }

            // Nếu không có annotation name, lấy tên method
            return method.getName();
        } catch (Exception e) {
            return "Unknown API";
        }
    }

    private synchronized void logApiCallToCSV(
            String apiName,
            String endpoint,
            String sqlQuery,
            String requestData,
            String responseData,
            long executionTime,
            boolean success,
            String errorMessage) {

        try {
            Path path = Paths.get(csvFilePath);
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }

            // Xác định chức năng kiểm thử từ endpoint
            String testFunction = getTestFunctionFromEndpoint(endpoint, apiName);

            // Phân tích SQL query thực tế
            String assessment = analyzeRealSqlQuery(sqlQuery, success, errorMessage);

            // Giới hạn độ dài các field để tránh CSV quá lớn
            requestData = limitLength(requestData, 300);
            responseData = limitLength(responseData, 300);
            sqlQuery = limitLength(sqlQuery, 500);

            try (FileWriter writer = new FileWriter(csvFilePath, StandardCharsets.UTF_8, true)) {
                String csvLine = String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"",
                        counter.getAndIncrement(),
                        escapeForCsv(testFunction),
                        escapeForCsv(endpoint),
                        escapeForCsv(sqlQuery),
                        escapeForCsv(requestData),
                        escapeForCsv(responseData),
                        escapeForCsv(assessment),
                        "System Auto",
                        LocalDateTime.now().format(dateFormatter)
                );
                writer.write(csvLine + "\n");
                writer.flush();
                System.out.println("SQL logged: " + (sqlQuery.length() > 50 ? sqlQuery.substring(0, 50) + "..." : sqlQuery));
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV: " + e.getMessage());
        }
    }

    private String limitLength(String value, int maxLength) {
        if (value == null) return "";
        if (value.length() <= maxLength) return value;
        return value.substring(0, maxLength) + "...";
    }

    private String escapeForCsv(String value) {
        if (value == null) return "";
        return value.replace("\"", "\"\"").replace("\n", " ").replace("\r", " ");
    }

    private String getTestFunctionFromEndpoint(String endpoint, String apiName) {
        // Ưu tiên sử dụng apiName từ annotation
        if (apiName != null && !apiName.equals("Unknown API") && !apiName.isEmpty() && !apiName.equals("register") && !apiName.equals("login")) {
            return apiName;
        }

        // Phân tích endpoint để xác định chức năng kiểm thử
        String lowerEndpoint = endpoint.toLowerCase();

        if (lowerEndpoint.contains("register")) {
            return "Đăng ký người dùng";
        } else if (lowerEndpoint.contains("login")) {
            return "Đăng nhập";
        } else if (lowerEndpoint.contains("profile") && endpoint.contains("PUT")) {
            return "Cập nhật profile";
        } else if (lowerEndpoint.contains("password")) {
            return "Đổi mật khẩu";
        } else if (lowerEndpoint.contains("profile") && endpoint.contains("GET")) {
            return "Lấy thông tin profile";
        } else if (lowerEndpoint.contains("user") && endpoint.contains("DELETE")) {
            return "Xóa người dùng";
        } else if (lowerEndpoint.contains("users") && endpoint.contains("GET")) {
            return "Lấy danh sách users";
        }

        return "API: " + endpoint;
    }

    private String analyzeRealSqlQuery(String sql, boolean success, String errorMessage) {
        if (!success && errorMessage != null) {
            return "LỖI - " + errorMessage;
        }

        if (sql == null || sql.trim().isEmpty() || sql.startsWith("--")) {
            return "THÔNG TIN - Không có SQL query được thực thi";
        }

        String upperSql = sql.toUpperCase().trim();

        // Phân tích bảo mật
        if (sql.contains("?")) {
            // Prepared statement - tốt
            if (upperSql.startsWith("SELECT")) {
                if (upperSql.contains("PASSWORD")) {
                    return "CẢNH BÁO - Query select password field, có thể lộ thông tin nhạy cảm";
                }
                return "OK - SELECT query sử dụng prepared statement";
            } else if (upperSql.startsWith("INSERT")) {
                return "OK - INSERT query sử dụng prepared statement";
            } else if (upperSql.startsWith("UPDATE")) {
                return "OK - UPDATE query sử dụng prepared statement";
            } else if (upperSql.startsWith("DELETE")) {
                return "OK - DELETE query sử dụng prepared statement";
            }
            return "OK - Query sử dụng prepared statement";
        } else {
            // Không phải prepared statement - có thể có vấn đề
            if (upperSql.contains("'") || upperSql.contains("\"")) {
                return "CẢNH BÁO - Query sử dụng string literals, có nguy cơ SQL injection";
            }
            return "THÔNG TIN - Query không sử dụng parameters";
        }
    }
}
