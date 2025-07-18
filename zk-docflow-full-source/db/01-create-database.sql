-- Create database
CREATE DATABASE IF NOT EXISTS document_management;
USE document_management;

-- User table
CREATE TABLE user (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      full_name VARCHAR(100) NOT NULL,
                      role VARCHAR(50) NOT NULL
                          email VARCHAR(100),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Document table
CREATE TABLE document (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          title VARCHAR(255) NOT NULL,
                          content TEXT,
                          document_type ENUM('DI', 'DEN') NOT NULL,
                          status ENUM('CHO_XU_LY', 'DANG_XU_LY', 'HOAN_THANH', 'TU_CHOI') DEFAULT 'CHO_XU_LY',
                          created_by INT NOT NULL,
                          assigned_to INT,
                          address VARCHAR(255),
                          attachment VARCHAR(255);
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                                                     FOREIGN KEY (created_by) REFERENCES user(id),
                                                                     FOREIGN KEY (assigned_to) REFERENCES user(id)
                                                                     );

-- Document history table
CREATE TABLE document_history (
                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                  document_id INT NOT NULL,
                                  user_id INT NOT NULL,
                                  action ENUM('TAO_MOI', 'CHUYEN_TIEP', 'PHE_DUYET', 'TU_CHOI') NOT NULL,
                                  comment TEXT,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (document_id) REFERENCES document(id),
                                  FOREIGN KEY (user_id) REFERENCES user(id)
);

ALTER TABLE document_history
    ADD COLUMN deadline DATETIME NULL,
ADD COLUMN is_overdue BOOLEAN DEFAULT FALSE;

ALTER TABLE workflow_step
    ADD COLUMN duration_days INT DEFAULT 2; -- mặc định 2 ngày cho mỗi bước


CREATE TABLE workflow_step (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               step_order INT NOT NULL,
                               role_code VARCHAR(50) NOT NULL,
                               step_name VARCHAR(100) NOT NULL
);
CREATE TABLE document_attachment (
                                     id INT PRIMARY KEY AUTO_INCREMENT,
                                     document_id INT NOT NULL,
                                     filename VARCHAR(255) NOT NULL,          -- Tên lưu trên server (UUID_xxx)
                                     original_name VARCHAR(255) NOT NULL,     -- Tên gốc của file
                                     uploaded_by INT NOT NULL,
                                     uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (document_id) REFERENCES document(id),
                                     FOREIGN KEY (uploaded_by) REFERENCES user(id)
);