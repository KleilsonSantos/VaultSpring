-- Align Flyway dev seed passwords with documented credential (secret123).
-- Previous hash had no documented plaintext and failed login in Swagger/curl.

UPDATE users
SET user_password = '$2a$10$eUNieSfBKisXr3zK2aaNYe6YgY3DOn0eWuP23yv5xu2ld4XRToQZW',
    user_updated_at = NOW()
WHERE user_password = '$2a$10$ZSEWzyqZhRjxU6eRyTMTcOMCU8nnp7b4HR3V3qbtEdh6Trzj9K6du';
