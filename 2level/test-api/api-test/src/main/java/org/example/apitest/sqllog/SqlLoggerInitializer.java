package org.example.apitest.sqllog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class SqlLoggerInitializer implements CommandLineRunner {

    @Value("${sql.logging.csv.path:logs/sql_queries.csv}")
    private String csvFilePath;

    @Override
    public void run(String... args) throws Exception {
        // Đảm bảo file CSV được tạo khi ứng dụng khởi động
        ensureFileExists();
        System.out.println("=== SQL Logger Initialized ===");
        System.out.println("CSV file location: " + csvFilePath);
        System.out.println("Ready to capture SQL queries from your APIs!");
    }

    private void ensureFileExists() throws IOException {
        Path path = Paths.get(csvFilePath);

        // Tạo thư mục nếu chưa tồn tại
        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
            System.out.println("Created directory: " + path.getParent());
        }

        // Tạo file CSV với header nếu chưa tồn tại
        if (!Files.exists(path)) {
            try (FileWriter writer = new FileWriter(path.toFile(), StandardCharsets.UTF_8)) {
                writer.write("\uFEFF"); // UTF-8 BOM for Excel
                writer.write("STT,Chức năng kiểm thử,Bước thao tác,Câu lệnh SQL thực tế,Dữ liệu đầu vào,Dữ liệu trả về,Nhận xét/Phát hiện lỗi,Người kiểm tra,Ngày kiểm tra\n");
                System.out.println("Created CSV file with headers: " + path);
            }
        }
    }
}
