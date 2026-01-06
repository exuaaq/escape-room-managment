-- Create database
CREATE DATABASE IF NOT EXISTS escape_room_db;
USE escape_room_db;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STAFF') NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    theme VARCHAR(50),
    difficulty INT CHECK (difficulty BETWEEN 1 AND 5),
    capacity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    duration INT NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Players table
CREATE TABLE IF NOT EXISTS players (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    total_games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    games_lost INT DEFAULT 0,
    average_time DECIMAL(10,2) DEFAULT 0.00,
    total_hints_used INT DEFAULT 0,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings table
CREATE TABLE IF NOT EXISTS bookings (
    id INT PRIMARY KEY AUTO_INCREMENT,
    room_id INT NOT NULL,
    scheduled_time DATETIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    number_of_players INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    notes TEXT,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- Booking_Players (many-to-many)
CREATE TABLE IF NOT EXISTS booking_players (
    booking_id INT,
    player_id INT,
    PRIMARY KEY (booking_id, player_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

-- Game Sessions table (booking_id is nullable for walk-in clients)
CREATE TABLE IF NOT EXISTS game_sessions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NULL,
    room_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME,
    completed BOOLEAN DEFAULT FALSE,
    time_spent INT,
    hints_used INT DEFAULT 0,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    review TEXT,
    revenue DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE
);

-- Insert sample rooms
INSERT INTO rooms (name, theme, difficulty, capacity, price, duration, description, is_active, average_rating) VALUES
('The Haunted Manor', 'Horror', 5, 6, 50.00, 60, 'Escape from a haunted mansion before midnight strikes', TRUE, 4.5),
('Murder Mystery', 'Mystery', 3, 4, 40.00, 60, 'Solve the murder case in the detective office', TRUE, 4.2),
('Space Station Escape', 'Sci-Fi', 4, 8, 60.00, 75, 'Repair the station and escape before oxygen runs out', TRUE, 4.8),
('Pirate Treasure Hunt', 'Adventure', 2, 5, 35.00, 45, 'Find the buried treasure on the pirate ship', TRUE, 4.0),
('Zombie Apocalypse', 'Horror', 5, 6, 55.00, 60, 'Survive the zombie outbreak and find the cure', TRUE, 4.7),
('Ancient Egyptian Tomb', 'Mystery', 4, 4, 45.00, 60, 'Uncover the secrets of the pharaoh tomb', TRUE, 4.3),
('Time Machine', 'Sci-Fi', 3, 6, 50.00, 60, 'Fix the time machine and return to present', TRUE, 4.1),
('Medieval Castle', 'Adventure', 3, 7, 45.00, 60, 'Escape from the dungeon and find the hidden exit', TRUE, 3.9)
ON DUPLICATE KEY UPDATE name=name;

-- Insert sample players
INSERT INTO players (first_name, last_name, email, phone, total_games_played, games_won, games_lost, average_time, total_hints_used) VALUES
('John', 'Doe', 'john.doe@email. com', '+38761123456', 15, 10, 5, 45.5, 12),
('Jane', 'Smith', 'jane.smith@email.com', '+38762234567', 20, 18, 2, 42.3, 8),
('Michael', 'Johnson', 'michael.j@email.com', '+38763345678', 8, 5, 3, 50.2, 15),
('Emily', 'Davis', 'emily.davis@email.com', '+38764456789', 12, 9, 3, 48.0, 10),
('Robert', 'Wilson', 'robert. w@email.com', '+38765567890', 25, 20, 5, 40.5, 7),
('Sarah', 'Brown', 'sarah.brown@email.com', '+38766678901', 18, 14, 4, 44.8, 9),
('David', 'Martinez', 'david.m@email.com', '+38767789012', 10, 6, 4, 52.1, 18),
('Lisa', 'Anderson', 'lisa.anderson@email.com', '+38768890123', 22, 19, 3, 41.2, 6),
('James', 'Taylor', 'james.taylor@email. com', '+38769901234', 14, 10, 4, 46.5, 11),
('Emma', 'Thomas', 'emma.thomas@email.com', '+38760012345', 16, 12, 4, 47.3, 13),
('Daniel', 'Moore', 'daniel.moore@email.com', '+38761111111', 9, 5, 4, 51.0, 16),
('Olivia', 'Jackson', 'olivia.jackson@email.com', '+38762222222', 11, 8, 3, 49.0, 12),
('William', 'White', 'william.white@email.com', '+38763333333', 7, 4, 3, 53.5, 20),
('Sophia', 'Harris', 'sophia.harris@email.com', '+38764444444', 19, 15, 4, 43.2, 8),
('Alexander', 'Martin', 'alex.martin@email.com', '+38765555555', 13, 9, 4, 48.8, 14)
ON DUPLICATE KEY UPDATE email=email;

-- Insert sample bookings
INSERT INTO bookings (room_id, scheduled_time, status, number_of_players, total_price, notes) VALUES
(1, '2026-01-10 18:00:00', 'CONFIRMED', 4, 50.00, 'Birthday party group'),
(2, '2026-01-10 19:30:00', 'CONFIRMED', 3, 40.00, NULL),
(3, '2026-01-11 15:00:00', 'PENDING', 6, 60.00, 'Corporate team building'),
(4, '2026-01-11 17:00:00', 'CONFIRMED', 5, 35.00, 'Family group'),
(5, '2026-01-12 20:00:00', 'PENDING', 4, 55.00, NULL),
(1, '2026-01-05 18:00:00', 'COMPLETED', 5, 50.00, NULL),
(2, '2026-01-06 19:00:00', 'COMPLETED', 4, 40.00, NULL),
(3, '2026-01-07 16:00:00', 'COMPLETED', 7, 60.00, NULL),
(6, '2026-01-08 18:30:00', 'COMPLETED', 4, 45.00, NULL),
(7, '2026-01-09 17:00:00', 'COMPLETED', 5, 50.00, NULL),
(1, '2025-12-28 18:00:00', 'CANCELLED', 4, 50.00, 'Customer requested cancellation'),
(5, '2025-12-29 20:00:00', 'CANCELLED', 6, 55.00, 'No show')
ON DUPLICATE KEY UPDATE id=id;

-- Insert booking_players relationships
INSERT INTO booking_players (booking_id, player_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(2, 5), (2, 6), (2, 7),
(3, 8), (3, 9), (3, 10), (3, 11), (3, 12), (3, 13),
(4, 14), (4, 15), (4, 1), (4, 2), (4, 3),
(5, 4), (5, 5), (5, 6), (5, 7),
(6, 1), (6, 2), (6, 3), (6, 4), (6, 5),
(7, 6), (7, 7), (7, 8), (7, 9),
(8, 10), (8, 11), (8, 12), (8, 13), (8, 14), (8, 15), (8, 1),
(9, 2), (9, 3), (9, 4), (9, 5),
(10, 6), (10, 7), (10, 8), (10, 9), (10, 10)
ON DUPLICATE KEY UPDATE booking_id=booking_id;

-- Insert sample game sessions
INSERT INTO game_sessions (booking_id, room_id, start_time, end_time, completed, time_spent, hints_used, rating, review, revenue) VALUES
(6, 1, '2026-01-05 18:00:00', '2026-01-05 18:52:00', TRUE, 52, 2, 5, 'Amazing experience! Very scary and immersive.', 50.00),
(7, 2, '2026-01-06 19:00:00', '2026-01-06 19:58:00', TRUE, 58, 1, 4, 'Great mystery, challenging puzzles.', 40.00),
(8, 3, '2026-01-07 16:00:00', '2026-01-07 17:15:00', TRUE, 75, 3, 5, 'Best escape room ever! Loved the sci-fi theme.', 60.00),
(9, 6, '2026-01-08 18:30:00', '2026-01-08 19:28:00', TRUE, 58, 2, 4, 'Very interesting Egyptian theme.', 45.00),
(10, 7, '2026-01-09 17:00:00', '2026-01-09 17:55:00', TRUE, 55, 1, 4, 'Time machine concept was cool.', 50.00)
ON DUPLICATE KEY UPDATE id=id;