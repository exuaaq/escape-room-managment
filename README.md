# Escape Room Management System

Sistem za upravljanje Escape Room-om razvijen u Java-i sa JavaFX korisničkim interfejsom i MySQL bazom podataka. 

## Sadržaj

- [O Projektu](#o-projektu)
- [Funkcionalnosti](#funkcionalnosti)
- [Tehnologije](#tehnologije)
- [Preduslovi](#preduslovi)
- [Instalacija](#instalacija)
- [Konfiguracija Baze Podataka](#konfiguracija-baze-podataka)
- [Pokretanje Aplikacije](#pokretanje-aplikacije)
- [Struktura Projekta](#struktura-projekta)
- [Upotreba](#upotreba)
- [Sigurnost](#sigurnost)

## O Projektu

Escape Room Management System je desktop aplikacija koja omogućava kompletno upravljanje escape room poslovanjem.  Sistem omogućava praćenje soba, rezervacija, igrača, gaming sesija i finansijskih izvještaja. 

Projekat je razvijen kao timski rad i sadrži sve potrebne komponente: 
- **Modeli objekata**:  User, Room, Player, Booking, GameSession (više od 3)
- **Interfejsi**: DAO (Data Access Object) pattern
- **Baza podataka**: MySQL sa JDBC konekcijom
- **JavaFX GUI**: Login, Dashboard, CRUD forme, tabelarni prikazi
- **CRUD operacije**: Create, Read, Update, Delete nad svim entitetima
- **Izvoz podataka**:  Izvještaji sa mogućnošću eksporta u PDF format

## Funkcionalnosti

### Autentifikacija
- Sigurna autentifikacija sa BCrypt hash-iranjem lozinki
- Upravljanje korisničkim nalozima

### Upravljanje Sobama
- Dodavanje, izmjena i brisanje escape room soba
- Praćenje tema, težine, kapaciteta i cijene
- Aktiviranje/deaktiviranje soba
- Praćenje prosječnih ocjena
- Tabelarni prikaz svih soba

### Upravljanje Rezervacijama (Bookings)
- Kreiranje novih rezervacija
- Filtriranje po statusu (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- Ažuriranje statusa rezervacija
- Povezivanje rezervacija sa sobama i igračima
- Tabelarni prikaz svih rezervacija

### Upravljanje Igračima
- Registracija novih igrača
- Izmjena podataka igrača
- Praćenje statistika igrača: 
  - Ukupan broj odigranih igara
  - Broj pobjeda i poraza
  - Prosječno vrijeme
  - Ukupan broj korištenih savjeta
- Tabelarni prikaz svih igrača

### Gaming Sesije
- Kreiranje sesija sa ili bez rezervacije (walk-in klijenti)
- Praćenje vremena trajanja igre
- Broj korištenih savjeta
- Ocjene i recenzije
- Praćenje prihoda po sesiji
- Tabelarni prikaz svih sesija

### Dashboard
- Pregled statistika: 
  - Ukupan broj soba
  - Rezervacije za danas
  - Mjesečni prihod
  - Ukupan broj igrača
- Top 5 igrača - leaderboard
- Brze akcije za pristup modulima

### Izvještaji i Izvoz
- Izvještaj performansi soba
- Izvještaj rezervacija (sa filterima)
- **Izvoz u PDF format**

## Tehnologije

- **Java 17+**
- **JavaFX 17+** - Korisnički interfejs
- **MySQL 8.0+** - Baza podataka
- **BCrypt** - Hash-iranje lozinki
- **JDBC** - Komunikacija sa bazom podataka
- **DAO Pattern** - Arhitekturni pattern za pristup podacima

## Preduslovi

Prije instalacije, potrebno je imati instalirano:

1. **Java Development Kit (JDK) 17 ili noviji**
   - [Preuzmi JDK](https://www.oracle.com/java/technologies/downloads/)
   
2. **MySQL Server 8.0 ili noviji**
   - [Preuzmi MySQL](https://dev.mysql.com/downloads/mysql/)
   
3. **JavaFX SDK** (ako nije uključen u JDK)
   - [Preuzmi JavaFX](https://openjfx.io/)

## Instalacija

### 1. Kloniraj Repozitorij

```bash
git clone https://github.com/exuaaq/escape-room-managment.git
cd escape-room-managment
```

### 2. Preuzmi Potrebne Biblioteke

Potrebne su sljedeće JAR biblioteke u `lib` folderu:

- `mysql-connector-java-8.0.33.jar` - MySQL JDBC driver
- `bcrypt-0.10.2.jar` - BCrypt biblioteka za hash-iranje
- `javafx-*. jar` - JavaFX biblioteke (ako nisu u JDK-u)

## Konfiguracija Baze Podataka

### 1. Kreiraj Bazu Podataka

Pokreni MySQL i izvrši SQL skriptu:

```bash
mysql -u root -p < database_setup. sql
```

Ili ručno u MySQL klijentu: 

```sql
source /putanja/do/database_setup.sql
```

Ova skripta će:
- Kreirati bazu podataka `escape_room_db`
- Kreirati sve potrebne tabele
- Dodati sample podatke (sobe, igrači, rezervacije, sesije)
- **NAPOMENA**: Nema inicijalnog admin korisnika - potrebno je kreirati ručno

### 2. Kreiraj Prvog Korisnika

Možeš kreirati korisnika na dva načina: 

**Opcija A: Preko MySQL-a**

```sql
USE escape_room_db;

-- Lozinka:  admin123
INSERT INTO users (username, password_hash, role, first_name, last_name, email) 
VALUES ('admin', '$2a$10$xFJkXzR8h7.NvSL1P4f8.eG1YVwJ5l0K8oZ2pQ3mN9xC1wH5yL6Fy', 'ADMIN', 'Admin', 'User', 'admin@escaperoom.com');
```

**Opcija B: Kreiraj vlastiti hash lozinke**

Možeš koristiti online BCrypt generator ili napisati jednostavan Java program. 

### 3. Konfiguriši Database Connection

Uredi fajl `src/main/java/com/escaperoom/database/DatabaseConnection.java`:

```java
private static final String URL = "jdbc: mysql://localhost:3306/escape_room_db";
private static final String USER = "root";  // Tvoj MySQL username
private static final String PASSWORD = ""; // Tvoj MySQL password
```

## Pokretanje Aplikacije
Prije pokretanja dodati bazu podataka
- `mvn clean install`
- `mvn javafx:run`

## Struktura Projekta

```
escape-room-managment/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── escaperoom/
│       │           ├── dao/              # Data Access Objects
│       │           │   ├── BookingDAO. java
│       │           │   ├── GameSessionDAO.java
│       │           │   ├── PlayerDAO.java
│       │           │   ├── RoomDAO.java
│       │           │   └── UserDAO.java
│       │           ├── database/         # Database konfiguracija
│       │           │   └── DatabaseConnection.java
│       │           ├── interfaces/       # Interfejsi
│       │           │   ├── Bookable.java
│       │           │   └── Exportable.java
│       │           ├── models/           # Model klase
│       │           │   ├── Booking.java
│       │           │   ├── BookingStatus.java
│       │           │   ├── GameSession.java
│       │           │   ├── Player.java
│       │           │   ├── Room.java
│       │           │   ├── User.java
│       │           │   └── UserRole.java
│       │           ├── utils/            # Pomoćne klase
│       │           │   ├── AlertUtil.java
│       │           │   └── PasswordHasher.java
│       │           ├── views/            # UI Views
│       │           │   ├── BookingManagementView.java
│       │           │   ├── DashboardView.java
│       │           │   ├── GameSessionView.java
│       │           │   ├── LoginView.java
│       │           │   ├── PlayerManagementView.java
│       │           │   ├── ReportsView.java
│       │           │   ├── RoomManagementView.java
│       │           │   └── UserManagementView.java
│       │           └── Main.java         # Main aplikacija
│       └── resources/
│           ├── database_schema.sql
│           ├── migration_booking_id_nullable.sql
│           └── style.css               # CSS stilovi
├── . gitignore
├── pom.xml                              # Maven konfiguracija
├── PROJECT_SUMMARY.md
└── README.md
```

## Upotreba

### Login

1. Pokreni aplikaciju
2. Uloguj se sa kredencijalima:
   - Username:  `admin`
   - Password: `admin123` (ako si koristio default hash)

### Dashboard

Nakon logina, vidjet ćeš dashboard sa:
- Statistikama sistema
- Top 5 igrača
- Brzim akcijama za pristup modulima

### CRUD Operacije

**Room Management (Upravljanje Sobama)**
- **Create**: Dodaj novu sobu
- **Read**: Pregledaj sve sobe u tabeli
- **Update**: Izmijeni postojeću sobu
- **Delete**: Obriši ili deaktiviraj sobu

**Booking Management (Upravljanje Rezervacijama)**
- **Create**: Kreiraj novu rezervaciju
- **Read**: Pregledaj sve rezervacije u tabeli
- **Update**:  Potvrdi, završi ili otkaži rezervacije
- **Delete**:  Obriši rezervaciju
- **Filter**: Filtriraj po statusu

**Player Management (Upravljanje Igračima)**
- **Create**: Dodaj novog igrača
- **Read**: Pregledaj sve igrače u tabeli
- **Update**: Izmijeni podatke igrača
- **Delete**: Obriši igrača

**Game Sessions (Gaming Sesije)**
- **Create**: Kreiraj novu gaming sesiju
- **Read**:  Pregledaj sve sesije u tabeli
- **Update**: Izmijeni sesiju
- **Filter**: Poveži sa rezervacijom ili kreiraj walk-in sesiju

**User Management (Upravljanje Korisnicima)**
- **Create**: Kreiraj nove korisnike
- **Read**: Pregledaj sve korisnike u tabeli
- **Update**: Izmijeni postojeće korisnike
- **Delete**: Obriši korisnike

### Izvještaji i PDF Izvoz

**Reports (Izvještaji)**
- Generiši izvještaje performansi soba
- Izvještaj rezervacija sa filterima
- **Izvezi izvještaje u PDF format**

## Sigurnost

- **Lozinke**: Sve lozinke su hash-irane pomoću BCrypt algoritma sa saltom
- **SQL Injection**:  Koriste se PreparedStatement-i za sve database upite
- **Validacija**: Svi input podaci se validiraju prije obrade


## Licence

Ovaj projekat je razvijen u obrazovne svrhe. 

