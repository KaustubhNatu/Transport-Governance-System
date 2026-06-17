ALTER TABLE student_transport
    ADD CONSTRAINT fk_st_student
        FOREIGN KEY (student_id)
            REFERENCES students(id);