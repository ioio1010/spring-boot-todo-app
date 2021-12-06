CREATE TABLE users_roles
(
    users_id    BIGINT                      NOT NULL,
    roles_id    BIGINT                      NOT NULL,
    CONSTRAINT pk_users_roles PRIMARY KEY (users_id, roles_id)
);

ALTER TABLE users_roles
    ADD CONSTRAINT FK_USERS_ROLES_ON_USER FOREIGN KEY (users_id) REFERENCES users (id)
        ON DELETE CASCADE;;
ALTER TABLE users_roles
    ADD CONSTRAINT FK_USERS_ROLES_ON_ROLE FOREIGN KEY (roles_id) REFERENCES user_roles (id)
        ON DELETE CASCADE;;