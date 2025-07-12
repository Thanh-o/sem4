-- 📄 db/schema.sql

-- Bảng tài khoản người dùng
CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      username VARCHAR(50) NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      role VARCHAR(20) NOT NULL -- NHANVIEN, LANHDAO
);

INSERT INTO user (username, password, role) VALUES
                                                ('nhanvien1', '123456', 'NHANVIEN'),
                                                ('lanhdao1', '123456', 'LANHDAO');


-- Bảng thông tin văn bản
CREATE TABLE document (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          type VARCHAR(10) NOT NULL, -- DI, DEN
                          title VARCHAR(255) NOT NULL,
                          content TEXT,
                          code VARCHAR(50) NOT NULL,
                          date DATE NOT NULL,
                          created_by INT,
                          receiver_or_sender VARCHAR(255),
                          status VARCHAR(20) DEFAULT 'CHO_XU_LY',
                          FOREIGN KEY (created_by) REFERENCES user(id)
);

-- FULLTEXT INDEX cho tìm kiếm toàn văn
-- (áp dụng nếu dùng MySQL InnoDB hoặc MyISAM)
ALTER TABLE document ADD FULLTEXT(title, content);

-- Bảng lịch sử xử lý văn bản
CREATE TABLE document_history (
                                  id INT AUTO_INCREMENT PRIMARY KEY,
                                  document_id INT NOT NULL,
                                  handler_id INT NOT NULL,
                                  action VARCHAR(50) NOT NULL, -- PHE_DUYET, CHUYEN_TIEP, TU_CHOI
                                  time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  note TEXT,
                                  FOREIGN KEY (document_id) REFERENCES document(id),
                                  FOREIGN KEY (handler_id) REFERENCES user(id)
);

-- Dữ liệu mẫu
INSERT INTO document (type, title, content, code, date, created_by, receiver_or_sender, status)
VALUES
    ('DI', 'Thư mời họp tổng kết', 'Nội dung cuộc họp tổng kết cuối năm', 'CV-01', CURDATE(), 1, 'Sở Giáo dục', 'CHO_XU_LY'),
    ('DEN', 'Báo cáo tài chính quý I', 'Chi tiết báo cáo doanh thu', 'CV-02', CURDATE(), 2, 'Sở Tài chính', 'DANG_XU_LY');

INSERT INTO document_history (document_id, handler_id, action, note)
VALUES
    (1, 1, 'TAO_MOI', 'Khởi tạo văn bản'),
    (2, 2, 'CHUYEN_TIEP', 'Chuyển tiếp cho lãnh đạo xử lý');
