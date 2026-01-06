package com.escaperoom.dao;

import com.escaperoom.database.DatabaseConnection;
import com.escaperoom.models.Booking;
import com.escaperoom.models.BookingStatus;
import com.escaperoom.models.Player;
import com.escaperoom.models.Room;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    
    private final RoomDAO roomDAO = new RoomDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();
    

    public Booking findById(int id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractBookingFromResultSet(rs, conn);
            }
        } catch (SQLException e) {
            System.err.println("Error finding booking: " + e.getMessage());
        }
        
        return null;
    }
    


    public List<Booking> findAll() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings ORDER BY scheduled_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs, conn));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all bookings: " + e.getMessage());
        }
        
        return bookings;
    }
    

    public List<Booking> findByRoom(int roomId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE room_id = ? ORDER BY scheduled_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs, conn));
            }
        } catch (SQLException e) {
            System.err.println("Error finding bookings by room: " + e.getMessage());
        }
        
        return bookings;
    }
    

    public List<Booking> findByStatus(BookingStatus status) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE status = ? ORDER BY scheduled_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs, conn));
            }
        } catch (SQLException e) {
            System.err.println("Error finding bookings by status: " + e.getMessage());
        }
        
        return bookings;
    }

    public List<Booking> findByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE scheduled_time BETWEEN ? AND ? ORDER BY scheduled_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                bookings.add(extractBookingFromResultSet(rs, conn));
            }
        } catch (SQLException e) {
            System.err.println("Error finding bookings by date range: " + e.getMessage());
        }
        
        return bookings;
    }
    

    public boolean isRoomAvailable(int roomId, LocalDateTime dateTime) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE room_id = ? AND scheduled_time = ? AND status != 'CANCELLED'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            stmt.setTimestamp(2, Timestamp.valueOf(dateTime));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking room availability: " + e.getMessage());
        }
        
        return false;
    }
    

    public void save(Booking booking) {
        String sql = "INSERT INTO bookings (room_id, scheduled_time, status, number_of_players, total_price, notes) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, booking.getRoom().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(booking.getScheduledTime()));
            stmt.setString(3, booking.getStatus().name());
            stmt.setInt(4, booking.getNumberOfPlayers());
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setString(6, booking.getNotes());
            
            stmt.executeUpdate();
            
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int bookingId = keys.getInt(1);
                booking.setId(bookingId);
                
                // Save booking_players relationships
                saveBookingPlayers(conn, bookingId, booking.getPlayers());
            }
        } catch (SQLException e) {
            System.err.println("Error saving booking: " + e.getMessage());
        }
    }
    

    public void update(Booking booking) {
        String sql = "UPDATE bookings SET room_id = ?, scheduled_time = ?, status = ?, number_of_players = ?, total_price = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, booking.getRoom().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(booking.getScheduledTime()));
            stmt.setString(3, booking.getStatus().name());
            stmt.setInt(4, booking.getNumberOfPlayers());
            stmt.setDouble(5, booking.getTotalPrice());
            stmt.setString(6, booking.getNotes());
            stmt.setInt(7, booking.getId());
            
            stmt.executeUpdate();
            
            // Update booking_players relationships
            deleteBookingPlayers(conn, booking.getId());
            saveBookingPlayers(conn, booking.getId(), booking.getPlayers());
            
        } catch (SQLException e) {
            System.err.println("Error updating booking: " + e.getMessage());
        }
    }
    

    public void delete(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting booking: " + e.getMessage());
        }
    }
    

    public void updateStatus(int bookingId, BookingStatus status) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating booking status: " + e.getMessage());
        }
    }
    

    private void saveBookingPlayers(Connection conn, int bookingId, List<Player> players) throws SQLException {
        String sql = "INSERT INTO booking_players (booking_id, player_id) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Player player : players) {
                stmt.setInt(1, bookingId);
                stmt.setInt(2, player.getId());
                stmt.executeUpdate();
            }
        }
    }
    

    private void deleteBookingPlayers(Connection conn, int bookingId) throws SQLException {
        String sql = "DELETE FROM booking_players WHERE booking_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        }
    }
    

    private List<Player> loadBookingPlayers(Connection conn, int bookingId) throws SQLException {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT player_id FROM booking_players WHERE booking_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int playerId = rs.getInt("player_id");
                Player player = playerDAO.findById(playerId);
                if (player != null) {
                    players.add(player);
                }
            }
        }
        
        return players;
    }
    

    private Booking extractBookingFromResultSet(ResultSet rs, Connection conn) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        
        int roomId = rs.getInt("room_id");
        Room room = roomDAO.findById(roomId);
        booking.setRoom(room);
        
        Timestamp scheduledTime = rs.getTimestamp("scheduled_time");
        if (scheduledTime != null) {
            booking.setScheduledTime(scheduledTime.toLocalDateTime());
        }
        
        Timestamp bookingDate = rs.getTimestamp("booking_date");
        if (bookingDate != null) {
            booking.setBookingDate(bookingDate.toLocalDateTime());
        }
        
        booking.setStatus(BookingStatus.valueOf(rs.getString("status")));
        booking.setNumberOfPlayers(rs.getInt("number_of_players"));
        booking.setTotalPrice(rs.getDouble("total_price"));
        booking.setNotes(rs.getString("notes"));

        List<Player> players = loadBookingPlayers(conn, booking.getId());
        booking.setPlayers(players);
        
        return booking;
    }
}
