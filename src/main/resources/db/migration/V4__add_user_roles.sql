ALTER TABLE users
    ADD COLUMN user_role VARCHAR(32) NOT NULL DEFAULT 'USER';

UPDATE users
SET user_role = 'ADMIN'
WHERE user_email = 'john@example.com';
