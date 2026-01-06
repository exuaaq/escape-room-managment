# Escape Room Management System

A comprehensive JavaFX-based management system for escape room businesses with full CRUD operations, booking management, player tracking, game session recording, and advanced reporting with PDF export.

## 📋 Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Default Login Credentials](#default-login-credentials)
- [Database Schema](#database-schema)
- [Usage Guide](#usage-guide)
- [Screenshots](#screenshots)
- [API Documentation](#api-documentation)

## ✨ Features

### Core Functionality
- **User Authentication**: Secure login system with BCrypt password hashing
- **Room Management**: Complete CRUD operations for escape rooms
- **Player Management**: Track player statistics, win rates, and performance
- **Booking System**: Schedule and manage reservations with status tracking
- **Game Session Recording**: Log completed games with ratings and reviews
- **Advanced Reporting**: Generate and export reports to PDF

### Technical Features
- **MVC Architecture**: Clean separation of concerns
- **DAO Pattern**: Organized database access layer
- **Input Validation**: Comprehensive validation for all forms
- **Responsive UI**: Modern JavaFX interface with CSS styling
- **PDF Export**: Generate professional reports using Apache PDFBox
- **Real-time Statistics**: Dashboard with live business metrics

### Implemented Requirements
✅ **5 Model Classes**: Room, Player, Booking, GameSession, User  
✅ **2 Interfaces**: Bookable, Exportable  
✅ **MySQL Database**: Complete schema with relationships  
✅ **5 DAO Classes**: UserDAO, RoomDAO, PlayerDAO, BookingDAO, GameSessionDAO  
✅ **7 JavaFX Views**: Login, Dashboard, Room Management, Booking Management, Player Management, Game Sessions, Reports  
✅ **4 Utility Classes**: PasswordHasher, PDFExporter, ValidationUtil, AlertUtil  

## 🛠️ Technologies Used

- **Java**: 11+
- **JavaFX**: 17.0.2 - GUI framework
- **MySQL**: 8.0+ - Database
- **Maven**: Build tool and dependency management
- **Apache PDFBox**: 2.0.29 - PDF generation
- **BCrypt**: 0.4 - Password hashing

## 📁 Project Structure

```
escape-room-management/
├── src/
│   ├── main/
│   │   ├── java/com/escaperoom/
│   │   │   ├── Main.java                    # Application entry point
│   │   │   ├── models/                      # Domain models
│   │   │   │   ├── Room.java
│   │   │   │   ├── Player.java
│   │   │   │   ├── Booking.java
│   │   │   │   ├── GameSession.java
│   │   │   │   ├── User.java
│   │   │   │   ├── BookingStatus.java (enum)
│   │   │   │   └── UserRole.java (enum)
│   │   │   ├── interfaces/                  # Interfaces
│   │   │   │   ├── Bookable.java
│   │   │   │   └── Exportable.java
│   │   │   ├── database/                    # Database connection
│   │   │   │   └── DatabaseConnection.java
│   │   │   ├── dao/                         # Data Access Objects
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── RoomDAO.java
│   │   │   │   ├── PlayerDAO.java
│   │   │   │   ├── BookingDAO.java
│   │   │   │   └── GameSessionDAO.java
│   │   │   ├── views/                       # JavaFX views
│   │   │   │   ├── LoginView.java
│   │   │   │   ├── DashboardView.java
│   │   │   │   ├── RoomManagementView.java
│   │   │   │   ├── BookingManagementView.java
│   │   │   │   ├── PlayerManagementView.java
│   │   │   │   ├── GameSessionView.java
│   │   │   │   └── ReportsView.java
│   │   │   └── utils/                       # Utility classes
│   │   │       ├── PasswordHasher.java
│   │   │       ├── PDFExporter.java
│   │   │       ├── ValidationUtil.java
│   │   │       └── AlertUtil.java
│   │   └── resources/
│   │       ├── style.css                    # CSS styling
│   │       └── database_schema.sql          # Database schema
├── pom.xml                                   # Maven configuration
└── README.md                                 # This file
```

## 📦 Prerequisites

Before running the application, ensure you have:

1. **Java Development Kit (JDK) 11 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK
   - Verify: `java -version`

2. **Maven 3.6+**
   - Download from [Maven](https://maven.apache.org/download.cgi)
   - Verify: `mvn -version`

3. **MySQL Server 8.0+**
   - Download from [MySQL](https://dev.mysql.com/downloads/mysql/)
   - Verify: `mysql --version`

4. **IDE (Optional but recommended)**
   - IntelliJ IDEA, Eclipse, or NetBeans

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/exuaaq/temp03.git
cd temp03
```

### Step 2: Set Up MySQL Database

1. **Start MySQL Server**
   ```bash
   # Windows
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl start mysql
   ```

2. **Create Database and Import Schema**
   ```bash
   mysql -u root -p < src/main/resources/database_schema.sql
   ```
   
   Or manually:
   ```sql
   mysql -u root -p
   CREATE DATABASE escape_room_db;
   USE escape_room_db;
   source /path/to/src/main/resources/database_schema.sql;
   ```

3. **Configure Database Connection**
   
   Edit `src/main/java/com/escaperoom/database/DatabaseConnection.java`:
   ```java
   private static final String URL = "jdbc:mysql://localhost:3306/escape_room_db";
   private static final String USER = "root";
   private static final String PASSWORD = "your_password"; // Change this
   ```

### Step 3: Build the Project

```bash
mvn clean install
```

### Step 4: Run the Application

```bash
mvn javafx:run
```

Or run the Main class from your IDE:
```
com.escaperoom.Main
```

## 🔐 Default Login Credentials

```
Username: admin
Password: admin123
```

**Note**: The default admin user is created automatically when you run the database schema. You can create additional users through the database.

## 🗄️ Database Schema

### Tables

1. **users** - System users (admin/staff)
2. **rooms** - Escape rooms
3. **players** - Registered players
4. **bookings** - Room reservations
5. **booking_players** - Many-to-many relationship between bookings and players
6. **game_sessions** - Completed game records

### Key Relationships
- One Room → Many Bookings
- One Booking → Many Players (many-to-many)
- One Booking → One Game Session
- One Room → Many Game Sessions

## 📖 Usage Guide

### Dashboard
The main dashboard shows:
- Total number of rooms
- Active bookings for today
- Revenue for current month
- Total registered players
- Top 5 players leaderboard
- Quick action buttons

### Room Management
- **Add Room**: Click "➕ Add Room" and fill in the form
- **Edit Room**: Double-click a room or select and click "✏️ Edit Room"
- **Delete Room**: Select a room and click "🗑️ Delete Room"
- **Toggle Active**: Enable/disable rooms without deleting
- **Search/Filter**: Use filters to find specific rooms

### Player Management
- **Add Player**: Click "➕ Add Player"
- **Edit Player**: Double-click or select and edit
- **View Statistics**: See win rates, average times, hints used
- **Search**: Filter by name or email

### Booking Management
- **Create Booking**: Click "➕ New Booking"
  - Select room
  - Choose date/time
  - Set number of players
  - Add notes
- **Update Status**: 
  - Confirm pending bookings
  - Mark as completed
  - Cancel if needed
- **Filter**: View bookings by status

### Game Sessions
- **Record Session**: Click "➕ Add Session"
  - Select room
  - Set start time and duration
  - Mark if completed
  - Add hints used
  - Give rating (1-5)
  - Write review
- **View Details**: Click "👁 View Details" for full session info

### Reports & Analytics
The reports view includes 4 tabs:

1. **Revenue Report**
   - Select date range
   - View total revenue
   - See breakdown by room
   - Export to PDF

2. **Player Statistics**
   - Top 20 players by win rate
   - Games played, won, average time
   - Export leaderboard to PDF

3. **Room Performance**
   - All rooms with ratings
   - Price and active status
   - Export to PDF

4. **Booking Report**
   - Filter by status
   - View all booking details
   - Export filtered results to PDF

### PDF Export
- Click "📄 Export to PDF" button in any report
- Choose save location
- Professional formatted PDF will be generated

## 🖼️ Screenshots

### Login Screen
Modern login interface with gradient background and form validation.

### Dashboard
Comprehensive overview with statistics cards, leaderboard, and quick actions.

### Room Management
Full CRUD interface with search, filters, and inline editing.

### Booking Management
Color-coded status display (Pending=Orange, Confirmed=Green, Completed=Blue, Cancelled=Red).

### Reports
Tabbed interface with multiple report types and PDF export functionality.

## 📚 API Documentation

### Model Classes

#### Room
```java
- id: int
- name: String
- theme: String (Horror, Mystery, Sci-Fi, Adventure)
- difficulty: int (1-5)
- capacity: int
- price: double
- duration: int (minutes)
- description: String
- isActive: boolean
- averageRating: double
```

#### Player
```java
- id: int
- firstName: String
- lastName: String
- email: String
- phone: String
- totalGamesPlayed: int
- gamesWon: int
- gamesLost: int
- averageTime: double
- totalHintsUsed: int
- registrationDate: LocalDateTime
```

#### Booking
```java
- id: int
- room: Room
- players: List<Player>
- bookingDate: LocalDateTime
- scheduledTime: LocalDateTime
- status: BookingStatus
- numberOfPlayers: int
- totalPrice: double
- notes: String
```

#### GameSession
```java
- id: int
- booking: Booking
- room: Room
- players: List<Player>
- startTime: LocalDateTime
- endTime: LocalDateTime
- completed: boolean
- timeSpent: int
- hintsUsed: int
- rating: int (1-5)
- review: String
- revenue: double
```

#### User
```java
- id: int
- username: String
- password: String (hashed)
- role: UserRole (ADMIN, STAFF)
- firstName: String
- lastName: String
- email: String
```

### DAO Methods

All DAO classes implement standard CRUD operations:
- `findById(int id)`
- `findAll()`
- `save(T entity)`
- `update(T entity)`
- `delete(int id)`

Plus specialized methods:
- **RoomDAO**: `findActive()`, `findByTheme(String)`, `updateRating()`
- **PlayerDAO**: `findByEmail()`, `searchByName()`, `getTopPlayers()`
- **BookingDAO**: `findByStatus()`, `findByDateRange()`, `isRoomAvailable()`
- **GameSessionDAO**: `findByRoom()`, `getTotalRevenue()`, `getRevenueByRoom()`

## 🔧 Configuration

### Changing Database Settings
Edit `DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/escape_room_db";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

### Customizing Themes
Edit `src/main/resources/style.css` to customize colors, fonts, and layouts.

### Adding New Rooms
You can add rooms through the GUI or directly in the database:
```sql
INSERT INTO rooms (name, theme, difficulty, capacity, price, duration, description, is_active) 
VALUES ('New Room', 'Horror', 4, 6, 55.00, 60, 'Description here', TRUE);
```

## 🐛 Troubleshooting

### Database Connection Issues
- Ensure MySQL server is running
- Check username/password in DatabaseConnection.java
- Verify database exists: `SHOW DATABASES;`

### JavaFX Module Errors
- Ensure you're using Java 11+
- Run with Maven: `mvn javafx:run`
- Or add VM options: `--module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml`

### Build Errors
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean install -DskipTests
```

## 📄 License

This project is created for educational purposes.

## 👥 Contributors

- System designed and implemented as per academic requirements
- Complete full-stack Java application with JavaFX GUI

## 🙏 Acknowledgments

- JavaFX for the GUI framework
- Apache PDFBox for PDF generation
- MySQL for database management
- BCrypt for secure password hashing

## 📞 Support

For issues or questions:
1. Check the troubleshooting section
2. Review the database schema
3. Ensure all prerequisites are met
4. Verify database connection settings

---

**Project Status**: ✅ Complete - All requirements implemented

**Last Updated**: January 2026