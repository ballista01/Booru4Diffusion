## Getting Started
### Prepare Database
```
-- User and database creation.

CREATE ROLE testuser3 WITH
	LOGIN
	SUPERUSER
	CREATEDB
	CREATEROLE
	INHERIT
	NOREPLICATION
	CONNECTION LIMIT -1
	PASSWORD '88888888';

CREATE DATABASE testdb3
    WITH
    OWNER = testuser3
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

-- Create tables

CREATE TABLE roles
(
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(255)
);

CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    username VARCHAR(255) UNIQUE
);

CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_roles
        FOREIGN KEY(role_id)
        REFERENCES roles(id),
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE TABLE images
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
    user_id BIGINT NOT NULL,
    title VARCHAR(255),
    description VARCHAR(65535),
    url VARCHAR(255),
    timestamp_created TIMESTAMP,
    timestamp_updated TIMESTAMP,
    published BOOLEAN,
    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE TABLE tags
(
    id BIGSERIAL PRIMARY KEY NOT NULL,
		name VARCHAR(255) UNIQUE
);

CREATE TABLE images_tags
(
		image_id BIGINT NOT NULL,
		tag_id BIGINT NOT NULL,
		PRIMARY KEY(image_id, tag_id),
		CONSTRAINT fk_image
				FOREIGN KEY(image_id) REFERENCES images(id),
		CONSTRAINT fk_tag
				FOREIGN KEY(tag_id) REFERENCES tags(id)
);

-- Insert some roles for auth
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```

Insert some sample data
