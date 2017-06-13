CREATE TABLE organizations
(id VARCHAR(20) PRIMARY KEY NOT NULL,
 org_name VARCHAR(200),
 last_login TIME,
 is_active BOOLEAN,
 pass VARCHAR(200) NOT NULL);
