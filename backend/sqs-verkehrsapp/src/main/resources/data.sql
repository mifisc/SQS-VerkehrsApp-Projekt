-- Testuser
INSERT INTO app_users (id, username, password_hash)
VALUES (
           '11111111-1111-1111-1111-111111111111',
           'testuser',
           '$2a$10$7QJq98oXkF6vA9Y9XJv3EuW0yVQJx0X7M7s7wT9x0H5uM2r8bN5rK'
       );

-- Saved Roads
INSERT INTO saved_roads (id, user_id, road_id)
VALUES (
           '22222222-2222-2222-2222-222222222222',
           '11111111-1111-1111-1111-111111111111',
           'A3'
       );

INSERT INTO saved_roads (id, user_id, road_id)
VALUES (
           '33333333-3333-3333-3333-333333333333',
           '11111111-1111-1111-1111-111111111111',
           'A92'
       );