CREATE TABLE user_roles
(
    id         BIGINT                      NOT NULL,
    role_type  VARCHAR(255)                NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (id)
);

ALTER TABLE user_roles
    ADD CONSTRAINT uc_user_roles_role_type UNIQUE (role_type);