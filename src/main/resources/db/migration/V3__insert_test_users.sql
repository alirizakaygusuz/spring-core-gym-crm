-- Test Trainee
-- Test users password: "password123"
INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('John', 'Doe', 'john.doe', '$2a$10$cOMntoCQAVGQSp5HxxknGeZZ/X6e5ZtGm9MMoetJXo9qGnCVdIoZe', true);

INSERT INTO trainees (user_id, date_of_birth, address)
VALUES ((SELECT id FROM users WHERE username = 'john.doe'), '1990-01-01', '123 Main St');

-- Test Trainer
INSERT INTO users (first_name, last_name, username, password, is_active)
VALUES ('Trainer', 'Jane', 'trainer.jane', '$2a$10$cOMntoCQAVGQSp5HxxknGeZZ/X6e5ZtGm9MMoetJXo9qGnCVdIoZe', true);

INSERT INTO trainers (user_id, specialization_id)
VALUES ((SELECT id FROM users WHERE username = 'trainer.jane'), 1);