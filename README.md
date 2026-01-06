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

Escape Room Management System je desktop aplikacija koja omogućava kompletno upravljanje escape room poslovanjem. Sistem omogućava praćenje soba, rezervacija, igrača, gaming sesija i finansijskih izvještaja.

## Funkcionalnosti

### Upravljanje Korisnicima
- Sigurna autentifikacija sa BCrypt hash-iranjem lozinki
- Dvije uloge: **ADMIN** i **STAFF**
- Kreiranje i upravljanje korisničkim nalozima

### Upravljanje Sobama
- Dodavanje, izmjena i brisanje escape room soba
- Praćenje tema, težine, kapaciteta i cijene
- Aktiviranje/deaktiviranje soba
- Praćenje prosječnih ocjena

### Upravljanje Rezervacijama (Bookings)
- Kreiranje novih rezervacija
- Filtriranje po statusu (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- Ažuriranje statusa rezervacija
- Povezivanje rezervacija sa sobama i igračima

### Upravljanje Igračima
- Registracija novih igrača
- Praćenje statistika igrača: 
  - Ukupan broj odigranih igara
  - Broj pobjeda i poraza
  - Prosječno vrijeme
  - Ukupan broj korištenih savjeta

### Gaming Sesije
- Kreiranje sesija sa ili bez rezervacije (walk-in klijenti)
- Praćenje vremena trajanja igre
- Broj korištenih savjeta
- Ocjene i recenzije
- Praćenje prihoda po sesiji

### Dashboard
- Pregled statistika: 
  - Ukupan broj soba
  - Rezervacije za danas
  - Mjesečni prihod
  - Ukupan broj igrača
- Top 5 igrača - leaderboard
- Brze akcije za pristup modulima

### Izvještaji
- Izvještaj performansi soba
- Izvještaj rezervacija (sa filterima)
- Izvoz u PDF format

## Tehnologije

- **Java 17+**
- **JavaFX 17+** - Korisnički interfejs
- **MySQL 8.0+** - Baza podataka
- **BCrypt** - Hash-iranje lozinki
- **JDBC** - Komunikacija sa bazom podataka
- **Maven** - Build tool (opciono)

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
git clone https://github.com/exuaaq/temp03.git
cd temp03
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
mysql -u root -p < database_setup.sql
```

Ili ručno u MySQL klijentu:

```sql
source /putanja/do/database_setup.sql
```

Ova skripta će: 
- Kreirati bazu podataka `escape_room_db`
- Kreirati sve potrebne tabele
- Dodati sample podatke (sobe, igrači, rezervacije, sesije)
- **NAPOMENA**:  Nema inicijalnog admin korisnika - potrebno je kreirati ručno

### 2. Kreiraj Prvog Admin Korisnika

Možeš kreirati admin korisnika na dva načina:

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

### Preko Komandne Linije

```bash
# Kompajliranje
javac -d bin -cp "lib/*" src/main/java/com/escaperoom/**/*. java

# Pokretanje
java -cp "bin: lib/*" --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml com. escaperoom.EscapeRoomApp
```

### Preko IDE (IntelliJ IDEA / Eclipse)

1. Otvori projekat u IDE-u
2. Dodaj JAR biblioteke iz `lib` foldera u projekat
3. Konfiguriši JavaFX (ako je potrebno)
4. Pokreni `EscapeRoomApp. java` kao main klasu

## Struktura Projekta

```
temp03/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── escaperoom/
│       │           ├── dao/              # Data Access Objects
│       │           │   ├── BookingDAO.java
│       │           │   ├── GameSessionDAO.java
│       │           │   ├── PlayerDAO.java
│       │           │   ├── RoomDAO.java
│       │           │   └── UserDAO. java
│       │           ├── database/         # Database konfiguracija
│       │           │   └── DatabaseConnection.java
│       │           ├── models/           # Model klase
│       │           │   ├── Booking.java
│       │           │   ├── BookingStatus.java
│       │           │   ├── GameSession. java
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
│       │           │   ├── LoginView. java
│       │           │   ├── PlayerManagementView.java
│       │           │   ├── ReportsView. java
│       │           │   ├── RoomManagementView.java
│       │           │   └── UserManagementView.java
│       │           └── EscapeRoomApp.java # Main aplikacija
│       └── resources/
│           └── style.css               # CSS stilovi
├── lib/                                # Eksterne biblioteke
├── database_setup.sql                  # SQL skripta za setup
└── README. md
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

### Moduli

**Room Management**
- Pregledaj sve sobe
- Dodaj novu sobu
- Izmijeni ili deaktiviraj postojeću sobu

**Booking Management**
- Pregledaj sve rezervacije
- Kreiraj novu rezervaciju
- Potvrdi, završi ili otkaži rezervacije
- Filtriraj po statusu

**Player Management**
- Pregledaj sve igrače
- Dodaj novog igrača
- Izmijeni podatke igrača
- Vidi statistike igrača

**Game Sessions**
- Kreiraj novu gaming sesiju
- Poveži sa rezervacijom ili kreiraj walk-in sesiju
- Prati vrijeme, savjete, ocjene

**Reports**
- Generiši izvještaje performansi soba
- Izvještaj rezervacija sa filterima
- Izvezi u PDF

**User Management** (samo za ADMIN)
- Kreiraj nove korisnike (ADMIN ili STAFF)
- Izmijeni postojeće korisnike
- Obriši korisnike

## Sigurnost

- **Lozinke**: Sve lozinke su hash-irane pomoću BCrypt algoritma sa saltom
- **SQL Injection**:  Koriste se PreparedStatement-i za sve database upite
- **Autorizacija**: Samo ADMIN korisnici mogu pristupiti upravljanju korisnicima
- **Validacija**:  Svi input podaci se validiraju prije obrade

## Važne Napomene

1. **Database Credentials**: Nikada ne komituj database lozinke u repozitorij
2. **Default Admin**: Promijeni default admin lozinku nakon prvog logina
3. **Backup**: Redovno pravi backup baze podataka
4. **Production**: Za produkciju, konfiguriši dodatne sigurnosne mjere

## Doprinos

Za sve prijedloge, bugove ili nove funkcionalnosti:

1. Fork repozitorij
2. Kreiraj feature branch (`git checkout -b feature/NovaFunkcionalnost`)
3. Commit izmjene (`git commit -m 'Dodao novu funkcionalnost'`)
4. Push na branch (`git push origin feature/NovaFunkcionalnost`)
5. Otvori Pull Request

## Licence

Ovaj projekat je razvijen u obrazovne svrhe. 

## Autor

**exuaaq**
- GitHub: [@exuaaq](https://github.com/exuaaq)

## Podrška

Za pitanja ili probleme, otvori issue na GitHub repozitoriju.

---

**Uživaj u korištenju Escape Room Management System-a!**
