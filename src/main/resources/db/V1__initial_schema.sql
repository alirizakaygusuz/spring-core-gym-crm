-- 1) USERS
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name   VARCHAR(100) NOT NULL,
    last_name    VARCHAR(100) NOT NULL,
    username     VARCHAR(150) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    is_active    BOOLEAN      NOT NULL
);

-- 2) TRAINING_TYPE
CREATE TABLE IF NOT EXISTS training_type (
    id BIGSERIAL PRIMARY KEY,
    training_type_name VARCHAR(100) NOT NULL UNIQUE
);

-- 3) TRAINEE<-> USER(O2O)
CREATE TABLE IF NOT EXISTS trainee (
    id BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL UNIQUE,
    date_of_birth DATE,
    address       VARCHAR(255),

    CONSTRAINT fk_trainee_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 4) TRAINER<-> USER(O2O)
CREATE TABLE IF NOT EXISTS trainer (
    id BIGSERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL UNIQUE,
    specialization_id BIGINT NOT NULL,

    CONSTRAINT fk_trainer_user
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT fk_trainer_specialization
        FOREIGN KEY (specialization_id) REFERENCES training_type(id)
);

-- 5) TRAINEE <-> TRAINER (M2M)
CREATE TABLE IF NOT EXISTS trainee_trainer (
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,

    CONSTRAINT pk_trainee_trainer PRIMARY KEY (trainee_id, trainer_id),

    CONSTRAINT fk_tt_trainee
        FOREIGN KEY (trainee_id) REFERENCES trainee(id),

    CONSTRAINT fk_tt_trainer
        FOREIGN KEY (trainer_id) REFERENCES trainer(id)
);

-- 6) TRAINING
CREATE TABLE IF NOT EXISTS training (
    id BIGSERIAL PRIMARY KEY,
    trainee_id        BIGINT       NOT NULL,
    trainer_id        BIGINT       NOT NULL,
    training_name     VARCHAR(200) NOT NULL,
    training_type_id  BIGINT       NOT NULL,
    training_date     DATE         NOT NULL,
    training_duration INTEGER      NOT NULL CHECK (training_duration > 0),

    CONSTRAINT fk_training_trainee
        FOREIGN KEY (trainee_id) REFERENCES trainee(id),

    CONSTRAINT fk_training_trainer
        FOREIGN KEY (trainer_id) REFERENCES trainer(id),

    CONSTRAINT fk_training_type
        FOREIGN KEY (training_type_id) REFERENCES training_type(id)
);
