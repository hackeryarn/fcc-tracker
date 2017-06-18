CREATE TABLE members
(organization VARCHAR(20) NOT NULL,
 fcc_username VARCHAR(200) NOT NULL,
 name VARCHAR(200) NOT NULL,
 PRIMARY KEY(organization, fcc_username));
