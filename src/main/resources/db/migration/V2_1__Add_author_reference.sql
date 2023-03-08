ALTER TABLE budget
    ADD COLUMN author_id INT;
ALTER TABLE budget
    ADD FOREIGN KEY (author_id) REFERENCES author (id);