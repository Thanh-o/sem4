INSERT INTO user (username, password, full_name, role, email) VALUES
                                                                  ('khoitao', '123456', 'Người khởi tạo', 'NGUOI_KHOI_TAO', 'khoitao@example.com'),
                                                                  ('truongphong1', '123456', 'Trưởng phòng A', 'TRUONG_PHONG', 'truongphong1@example.com'),
                                                                  ('giamdoc1', '123456', 'Giám đốc A', 'GIAM_DOC', 'giamdoc1@example.com'),
                                                                  ('admin', '123456', 'Quản trị hệ thống', 'ADMIN', 'admin@example.com');

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

INSERT INTO workflow_step (step_order, role_code, step_name) VALUES
                                                                 (1, 'NGUOI_KHOI_TAO', 'Người khởi tạo'),
                                                                 (2, 'TRUONG_PHONG', 'Trưởng phòng phê duyệt'),
                                                                 (3, 'GIAM_DOC', 'Giám đốc phê duyệt'),
                                                                 (4, 'KET_THUC', 'Hoàn thành');
