--1) ROLES
CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);


-- 2) USERS
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    username     VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    is_active    BOOLEAN      NOT NULL
);

-- 3) USERS <-> ROLES (M2M)
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT unique_user_role UNIQUE (user_id, role_id)
);



-- 4) TRAINING TYPES
CREATE TABLE IF NOT EXISTS training_types (
    id BIGSERIAL PRIMARY KEY,
    training_type_name VARCHAR(100) NOT NULL UNIQUE
);

-- 5) TRAINEES <-> USERS (O2O)
CREATE TABLE IF NOT EXISTS trainees (
    id BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    address       VARCHAR(255),

    CONSTRAINT fk_trainees_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 6) TRAINERS <-> USERS (O2O)
CREATE TABLE IF NOT EXISTS trainers (
    id BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL UNIQUE,
    specialization_id BIGINT NOT NULL,

    CONSTRAINT fk_trainers_user
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT fk_trainers_specialization
        FOREIGN KEY (specialization_id) REFERENCES training_types(id)
);

-- 7) TRAINEES <-> TRAINERS (M2M)
CREATE TABLE IF NOT EXISTS trainee_trainer (
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,

    CONSTRAINT pk_trainee_trainer PRIMARY KEY (trainee_id, trainer_id),

    CONSTRAINT fk_tt_trainee
        FOREIGN KEY (trainee_id) REFERENCES trainees(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_tt_trainer
        FOREIGN KEY (trainer_id) REFERENCES trainers(id)
);

-- 8) TRAININGS
CREATE TABLE IF NOT EXISTS trainings (
    id BIGSERIAL PRIMARY KEY,
    trainee_id        BIGINT       NOT NULL,
    trainer_id        BIGINT       NOT NULL,
    training_name     VARCHAR(200) NOT NULL,
    training_type_id  BIGINT       NOT NULL,
    training_date     DATE         NOT NULL,
    training_duration INTEGER      NOT NULL CHECK (training_duration > 0),

    CONSTRAINT fk_trainings_trainee
        FOREIGN KEY (trainee_id) REFERENCES trainees(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_trainings_trainer
        FOREIGN KEY (trainer_id) REFERENCES trainers(id),

    CONSTRAINT fk_trainings_type
        FOREIGN KEY (training_type_id) REFERENCES training_types(id)
);
