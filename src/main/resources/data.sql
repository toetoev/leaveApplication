--  Auto-generated SQL script #202006251623
INSERT IGNORE INTO roles (name)
	VALUES ('ROLE_ADMINISTRATIVE_STAFF');
INSERT IGNORE INTO roles (name)
	VALUES ('ROLE_PROFESSIONAL_STAFF');
INSERT IGNORE INTO roles (name)
	VALUES ('ROLE_ADMIN');
INSERT IGNORE INTO roles (name)
	VALUES ('ROLE_MANAGER');

INSERT IGNORE INTO users (id, name, username, password) VALUES ("02c76432-bb6e-11ea-8d8b-0242ac110002", 'Admin','admin','$2a$10$HmwZebguqxttEraUTFvwpuErunkGhk.5oFSVnRnl0esn3tpy.TXBG');
INSERT IGNORE INTO user_roles (user_id, role_id) VALUES ("02c76432-bb6e-11ea-8d8b-0242ac110002",'ROLE_ADMIN');
