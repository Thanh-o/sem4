-- Create database
CREATE DATABASE IF NOT EXISTS document_management;
USE document_management;

-- User table
CREATE TABLE user (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      full_name VARCHAR(100) NOT NULL,
                      role ENUM('NHANVIEN', 'LANHDAO') NOT NULL,
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