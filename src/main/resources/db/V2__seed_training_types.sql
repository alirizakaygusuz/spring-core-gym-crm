INSERT INTO training_type (training_type_name) VALUES
  ('CARDIO'),
  ('STRENGTH'),
  ('FLEXIBILITY'),
  ('BALANCE'),
  ('YOGA'),
  ('PILATES'),
  ('CROSSFIT'),
  ('FUNCTIONAL_TRAINING'),
  ('HIIT'),
  ('MOBILITY'),
  ('ENDURANCE')
ON CONFLICT (training_type_name) DO NOTHING;