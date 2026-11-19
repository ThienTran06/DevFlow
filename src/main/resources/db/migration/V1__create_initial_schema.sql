CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE goals (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    description VARCHAR(255),
    status VARCHAR(255),
    title VARCHAR(255),
    updated_at DATETIME(6),
    user_id BIGINT,
    PRIMARY KEY (id),
    KEY idx_goals_user_id (user_id),
    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE tasks (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    deadline DATETIME(6),
    description VARCHAR(255),
    priority VARCHAR(255),
    status VARCHAR(255),
    title VARCHAR(255),
    updated_at DATETIME(6),
    goal_id BIGINT,
    user_id BIGINT,
    PRIMARY KEY (id),
    KEY idx_tasks_goal_id (goal_id),
    KEY idx_tasks_user_id (user_id),
    CONSTRAINT fk_tasks_goal FOREIGN KEY (goal_id) REFERENCES goals (id),
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE email_verification_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    expiry_date DATETIME(6) NOT NULL,
    token VARCHAR(255) NOT NULL,
    verified BIT(1) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_email_verification_token_token UNIQUE (token),
    KEY idx_email_verification_token_user_id (user_id),
    CONSTRAINT fk_email_verification_token_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE password_reset_token (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    expired_at DATETIME(6) NOT NULL,
    token VARCHAR(255) NOT NULL,
    used BIT(1) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_password_reset_token_token UNIQUE (token),
    KEY idx_password_reset_token_user_id (user_id),
    CONSTRAINT fk_password_reset_token_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    expired_at DATETIME(6) NOT NULL,
    revoked BIT(1) NOT NULL,
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
    KEY idx_refresh_tokens_user_id (user_id),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
