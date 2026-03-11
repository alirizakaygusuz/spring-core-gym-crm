insert into roles (name, description) values
    ('TRAINER', 'Trainer user who can manage trainings and access only their own trainer-related resources.'),
    ('TRAINEE', 'Trainee user who can access and manage only their own trainee-related resources.')
ON CONFLICT (name) DO NOTHING;