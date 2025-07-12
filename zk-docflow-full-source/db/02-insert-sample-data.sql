-- Insert sample users
INSERT INTO user (username, password, full_name, role, email) VALUES
('admin', 'admin123', 'Quản trị viên', 'LANHDAO', 'admin@company.com'),
('nhanvien1', 'nv123', 'Nguyễn Văn A', 'NHANVIEN', 'nva@company.com'),
('nhanvien2', 'nv123', 'Trần Thị B', 'NHANVIEN', 'ttb@company.com'),
('lanhdao1', 'ld123', 'Lê Văn C', 'LANHDAO', 'lvc@company.com');

-- Insert sample documents
INSERT INTO document (title, content, document_type, created_by, status) VALUES
('Công văn số 01/2024', 'Nội dung công văn đi số 01', 'DI', 2, 'CHO_XU_LY'),
('Thông báo họp', 'Nội dung thông báo họp định kỳ', 'DEN', 3, 'CHO_XU_LY'),
('Báo cáo tháng 1', 'Báo cáo kết quả công việc tháng 1', 'DI', 2, 'HOAN_THANH');

-- Insert sample history
INSERT INTO document_history (document_id, user_id, action, comment) VALUES
(1, 2, 'TAO_MOI', 'Tạo công văn mới'),
(2, 3, 'TAO_MOI', 'Tạo thông báo họp'),
(3, 2, 'TAO_MOI', 'Tạo báo cáo'),
(3, 1, 'PHE_DUYET', 'Đã phê duyệt báo cáo');
