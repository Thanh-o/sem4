package org.example.apitest.sqllog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SqlQueryCapture {

    private static final ThreadLocal<List<SqlQueryInfo>> threadLocalQueries = new ThreadLocal<>();

    public static void initThread() {
        threadLocalQueries.set(new ArrayList<>());
    }

    public static void addQuery(String sql, long executionTime, String endpoint) {
        List<SqlQueryInfo> queries = threadLocalQueries.get();
        if (queries != null) {
            queries.add(new SqlQueryInfo(sql, executionTime, endpoint, LocalDateTime.now()));
        }
    }

    public static List<SqlQueryInfo> getQueries() {
        List<SqlQueryInfo> queries = threadLocalQueries.get();
        return queries != null ? new ArrayList<>(queries) : new ArrayList<>();
    }

    public static void clearThread() {
        threadLocalQueries.remove();
    }

    public static class SqlQueryInfo {
        private final String sql;
        private final long executionTime;
        private final String endpoint;
        private final LocalDateTime timestamp;

        public SqlQueryInfo(String sql, long executionTime, String endpoint, LocalDateTime timestamp) {
            this.sql = sql;
            this.executionTime = executionTime;
            this.endpoint = endpoint;
            this.timestamp = timestamp;
        }

        public String getSql() { return sql; }
        public long getExecutionTime() { return executionTime; }
        public String getEndpoint() { return endpoint; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
