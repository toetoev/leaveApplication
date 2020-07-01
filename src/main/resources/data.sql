--  Auto-generated SQL script #202006251623
INSERT IGNORE INTO roles (name)
	VALUES ('ROLE_ADMINISTRATIVE_STAFF');
INSERT IGNORE INTO roles (name)
	VALUES ('ROLE_PROFESSIONAL_STAFF');
INSERT IGNORE INTO roles (name)
	VALUES ('ROLE_ADMIN');
INSERT IGNORE INTO roles (name)
	VALUES ('ROLE_MANAGER');

SET @UUID = (SELECT UUID() AS UUID);
INSERT IGNORE INTO users (id, name, username, password) VALUES (@UUID, 'Admin','admin','$2a$10$HmwZebguqxttEraUTFvwpuErunkGhk.5oFSVnRnl0esn3tpy.TXBG');
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES (@UUID,'ROLE_ADMIN');
