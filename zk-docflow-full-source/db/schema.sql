CREATE DATABASE IF NOT EXISTS document_workflow;
USE document_workflow;

CREATE TABLE user (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      full_name VARCHAR(100) NOT NULL,
                      role ENUM('EMPLOYEE', 'LEADER') NOT NULL,
                      email VARCHAR(100),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE document (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          document_number VARCHAR(50) NOT NULL UNIQUE,
                          title VARCHAR(255) NOT NULL,
                          content TEXT,
                          type ENUM('OUTGOING', 'INCOMING') NOT NULL,
                          recipient_place VARCHAR(255),
                          sender_place VARCHAR(255),
                          issue_date DATE,
                          receive_date DATE,
                          creator_id INT NOT NULL,
                          status ENUM('PENDING', 'PROCESSING', 'COMPLETED') NOT NULL DEFAULT 'PENDING',
                          attachment_path VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (creator_id) REFERENCES user(id),
                          FULLTEXT (title, content)
);

CREATE TABLE document_history (
                                  id INT PRIMARY KEY AUTO_INCREMENT,
                                  document_id INT NOT NULL,
                                  user_id INT NOT NULL,
                                  action ENUM('CREATED', 'APPROVED', 'FORWARDED', 'REJECTED') NOT NULL,
                                  comments TEXT,
                                  action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  next_handler_id INT,
                                  FOREIGN KEY (document_id) REFERENCES document(id),
                                  FOREIGN KEY (user_id) REFERENCES user(id),
                                  FOREIGN KEY (next_handler_id) REFERENCES user(id)
);

INSERT INTO user (username, password, full_name, role, email) VALUES
                                                                  ('admin', 'password123', 'Admin User', 'LEADER', 'admin@example.com'),
                                                                  ('employee1', 'password123', 'John Doe', 'EMPLOYEE', 'john@example.com');