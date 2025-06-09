INSERT INTO users (username, token, token_expire_time, created_at, updated_at) VALUES
('admin', 'admin-token', DATEADD('HOUR', 1, NOW()), NOW(), NOW()),
('user1', 'user1-token', DATEADD('HOUR', 1, NOW()), NOW(), NOW()),
('user2', 'user2-token', DATEADD('HOUR', 1, NOW()), NOW(), NOW()),
('testuser', 'test-token', DATEADD('HOUR', 1, NOW()), NOW(), NOW());
