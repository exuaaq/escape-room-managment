# Escape Room Management System

An Escape Room Management System developed in Java featuring a JavaFX user interface and a MySQL database.

## Contributors

- Eldar Alić
- Eniz Dajić
- Feđa Čoloman

## Table of Contents

- [About the Project](#about-the-project)
- [Features](#features)
- [Technologies](#technologies)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Configuration](#database-configuration)
- [Project Structure](#project-structure)
- [Usage](#usage)
- [Security](#security)
- [License](#license)

---

## About the Project

The Escape Room Management System is a desktop application designed to streamline the complete management of an escape room business. The system enables seamless tracking of rooms, bookings, players, game sessions, and financial reporting.

The project includes:

- **Object Models**: User, Room, Player, Booking, and GameSession.
- **DAO Pattern**: Separation of data access and business logic.
- **Database Integration**: MySQL with JDBC.
- **JavaFX GUI**: Login screen, dashboard, management forms, and reporting views.
- **CRUD Operations**: Full Create, Read, Update, and Delete functionality.
- **PDF Export**: Generate reports directly in PDF format.

---

## Features

### Authentication

- Secure login using BCrypt password hashing.
- User role management and authorization.

### Room Management

- Add, edit, and delete escape rooms.
- Manage themes, difficulty levels, player capacities, and pricing.
- Activate or deactivate rooms.
- Track ratings and reviews.

### Booking Management

- Create and manage bookings.
- Filter bookings by status:
  - PENDING
  - CONFIRMED
  - COMPLETED
  - CANCELLED
- Update booking lifecycle status.
- Associate bookings with rooms and players.

### Player Management

- Register and update player profiles.
- Track:
  - Total games played
  - Wins and losses
  - Average completion time
  - Total hints used

### Game Sessions

- Start sessions from bookings or walk-in customers.
- Track active game duration.
- Record hints used, ratings, and reviews.
- Calculate session revenue.

### Dashboard

- Total active rooms
- Today's bookings
- Monthly revenue
- Total registered players
- Top 5 players leaderboard
- Quick navigation shortcuts

### Reports & Exporting

- Room performance reports
- Booking summaries
- PDF export support

---

## Technologies

- Java 17+
- JavaFX 17+
- MySQL 8.0+
- JDBC
- BCrypt
- DAO Pattern

---

## Prerequisites

Before running the application, install:

1. **Java Development Kit (JDK) 17+**
   - https://www.oracle.com/java/technologies/downloads/

2. **MySQL Server 8.0+**
   - https://dev.mysql.com/downloads/mysql/

3. **JavaFX SDK** (if required)
   - https://openjfx.io/

---

## Installation

### Clone the Repository

```bash
git clone https://github.com/exuaaq/escape-room-managment.git
cd escape-room-managment
```

### Add Required Dependencies

Place the following libraries in the project's `lib/` directory (if not managed through Maven):

- `mysql-connector-java-8.0.33.jar`
- `bcrypt-0.10.2.jar`
- JavaFX runtime libraries

---

## Database Configuration

### 1. Import the Database Schema

Run the schema file:

```bash
mysql -u root -p < src/main/resources/database_schema.sql
```

Or execute inside a MySQL client:

```sql
SOURCE /absolute/path/to/src/main/resources/database_schema.sql;
```

This will:

- Create the `escape_room_db` database
- Create all required tables
- Insert sample data

---

### 2. Create an Admin User

```sql
USE escape_room_db;

INSERT INTO users (
    username,
    password_hash,
    role,
    first_name,
    last_name,
    email
)
VALUES (
    'admin',
    '$2a$10$xFJkXzR8h7.NvSL1P4f8.eG1YVwJ5l0K8oZ2pQ3mN9xC1wH5yL6Fy',
    'ADMIN',
    'Admin',
    'User',
    'admin@escaperoom.com'
);
```

**Default Credentials**

| Username | Password |
|-----------|-----------|
| admin | admin123 |

---

### 3. Configure Database Connection

Open:

```text
src/main/java/com/escaperoom/database/DatabaseConnection.java
```

Update:

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/escape_room_db";

private static final String USER = "root";
private static final String PASSWORD = "password";
```

---

## Running the Application

After configuring the database:

1. Import the database schema.
2. Configure database credentials.
3. Run:

```text
Main.java
```

---

## Project Structure

```text
escape-room-managment/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── escaperoom/
│       │           ├── dao/
│       │           ├── database/
│       │           ├── interfaces/
│       │           ├── models/
│       │           ├── utils/
│       │           ├── views/
│       │           └── Main.java
│       └── resources/
│           ├── database_schema.sql
│           ├── migration_booking_id_nullable.sql
│           └── style.css
├── .gitignore
├── pom.xml
├── PROJECT_SUMMARY.md
└── README.md
```

---

## Usage

### Login

Launch the application and log in using:

```text
Username: admin
Password: admin123
```

### Dashboard

After login, the dashboard displays:

- System statistics
- Revenue overview
- Top players
- Quick navigation options

### Available Modules

#### Room Management

- Create rooms
- Edit room information
- Manage pricing and difficulty
- Activate/deactivate rooms

#### Booking Management

- Create bookings
- Update booking status
- Filter booking records

#### Player Management

- Manage player profiles
- View statistics and history

#### Game Sessions

- Start and monitor sessions
- Record reviews and ratings
- Track revenue

#### User Management

- Manage system operators
- Assign roles and permissions

#### Reports

- Generate operational reports
- Export reports to PDF

---

## Security

### Password Protection

Passwords are stored using BCrypt hashing.

### SQL Injection Prevention

All database operations use prepared statements.

### Input Validation

User input is validated before processing.

---

## License

This project was developed as part of an academic team project and is intended for educational purposes only.
