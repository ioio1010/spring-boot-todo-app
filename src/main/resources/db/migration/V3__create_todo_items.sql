CREATE TABLE todo_items
(
    id         BIGINT                      NOT NULL,
    content    VARCHAR(255)                NOT NULL,
    user_id    BIGINT                      NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_todo_items PRIMARY KEY (id)
);

ALTER TABLE todo_items
    ADD CONSTRAINT FK_TODO_ITEMS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE;