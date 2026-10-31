-- =====================================================================
-- LearnHub - MySQL schema (reference / manual setup)
--
-- You do NOT need to run this file: Spring Boot (Hibernate ddl-auto=update)
-- creates these tables automatically. It is provided for documentation,
-- the ER diagram in your report, or for creating the database by hand.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS learnhub_db;
USE learnhub_db;

-- User profiles (students and instructors)
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,          -- BCrypt hash
    role        VARCHAR(20)  NOT NULL,          -- STUDENT | INSTRUCTOR
    created_at  DATETIME(6)  NOT NULL
);

-- Course data
CREATE TABLE IF NOT EXISTS courses (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(150)  NOT NULL,
    description    VARCHAR(2000) NOT NULL,
    category       VARCHAR(60)   NOT NULL,
    course_level   VARCHAR(20)   NOT NULL,
    duration_hours INT           NOT NULL,
    instructor_id  BIGINT        NOT NULL,
    created_at     DATETIME(6)   NOT NULL,
    CONSTRAINT fk_course_instructor FOREIGN KEY (instructor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS lessons (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id        BIGINT        NOT NULL,
    title            VARCHAR(150)  NOT NULL,
    content          VARCHAR(5000) NOT NULL,
    order_index      INT           NOT NULL,
    duration_minutes INT           NOT NULL,
    CONSTRAINT fk_lesson_course FOREIGN KEY (course_id) REFERENCES courses (id)
);

-- Interactive quizzes
CREATE TABLE IF NOT EXISTS quizzes (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id  BIGINT       NOT NULL,
    title      VARCHAR(150) NOT NULL,
    CONSTRAINT fk_quiz_course FOREIGN KEY (course_id) REFERENCES courses (id)
);

CREATE TABLE IF NOT EXISTS questions (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id        BIGINT       NOT NULL,
    question_text  VARCHAR(500) NOT NULL,
    option_a       VARCHAR(255) NOT NULL,
    option_b       VARCHAR(255) NOT NULL,
    option_c       VARCHAR(255),
    option_d       VARCHAR(255),
    correct_option INT          NOT NULL,       -- index 0..3
    CONSTRAINT fk_question_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id)
);

-- Course enrollment management
CREATE TABLE IF NOT EXISTS enrollments (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT      NOT NULL,
    course_id    BIGINT      NOT NULL,
    enrolled_at  DATETIME(6) NOT NULL,
    completed    BIT         NOT NULL,
    CONSTRAINT uk_enrollment UNIQUE (user_id, course_id),
    CONSTRAINT fk_enrollment_user   FOREIGN KEY (user_id)   REFERENCES users (id),
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES courses (id)
);

-- Learning progress
CREATE TABLE IF NOT EXISTS lesson_progress (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    enrollment_id  BIGINT      NOT NULL,
    lesson_id      BIGINT      NOT NULL,
    completed_at   DATETIME(6) NOT NULL,
    CONSTRAINT uk_progress UNIQUE (enrollment_id, lesson_id),
    CONSTRAINT fk_progress_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments (id),
    CONSTRAINT fk_progress_lesson     FOREIGN KEY (lesson_id)     REFERENCES lessons (id)
);

CREATE TABLE IF NOT EXISTS quiz_attempts (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT      NOT NULL,
    quiz_id          BIGINT      NOT NULL,
    score            INT         NOT NULL,
    total_questions  INT         NOT NULL,
    attempted_at     DATETIME(6) NOT NULL,
    CONSTRAINT fk_attempt_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_attempt_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes (id)
);

-- Useful queries for the report / viva
-- Progress of every student in every course:
-- SELECT u.full_name, c.title,
--        COUNT(lp.id) AS lessons_done,
--        (SELECT COUNT(*) FROM lessons l WHERE l.course_id = c.id) AS total_lessons
-- FROM enrollments e
-- JOIN users u   ON u.id = e.user_id
-- JOIN courses c ON c.id = e.course_id
-- LEFT JOIN lesson_progress lp ON lp.enrollment_id = e.id
-- GROUP BY u.full_name, c.id, c.title;
