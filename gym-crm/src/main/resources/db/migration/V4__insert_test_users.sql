-- Test Trainee
-- Test users password: "password123"
INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('John', 'Doe', 'john.doe', '$2a$10$cOMntoCQAVGQSp5HxxknGeZZ/X6e5ZtGm9MMoetJXo9qGnCVdIoZe', true);

INSERT INTO trainees (user_id, date_of_birth, address)
VALUES ((SELECT id FROM users WHERE username = 'john.doe'), '1990-01-01', '123 Main St');

-- Assign TRAINEE role to john.doe
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'john.doe' AND r.name = 'TRAINEE';


-- Test Trainer
INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('Trainer', 'Jane', 'trainer.jane', '$2a$10$cOMntoCQAVGQSp5HxxknGeZZ/X6e5ZtGm9MMoetJXo9qGnCVdIoZe', true);

INSERT INTO trainers (user_id, specialization_id)
VALUES ((SELECT id FROM users WHERE username = 'trainer.jane'), 1);

-- Assign TRAINER role to trainer.jane
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'trainer.jane' AND r.name = 'TRAINER';