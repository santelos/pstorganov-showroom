ALTER TABLE users
ADD roles text ARRAY    NOT NULL    DEFAULT '{}';
