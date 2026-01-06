package com.escaperoom.dao;

import com.escaperoom.database.DatabaseConnection;
import com.escaperoom.models.Booking;
import com.escaperoom.models.GameSession;
import com.escaperoom.models.Player;
import com.escaperoom.models.Room;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameSessionDAO {
    
    private final RoomDAO roomDAO = new RoomDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    

    public GameSession findById(int id) {
        String sql = "SELECT * FROM game_sessions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractGameSessionFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding game session: " + e.getMessage());
        }
        
        return null;
    }
    

    public List<GameSession> findAll() {
        List<GameSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM game_sessions ORDER BY start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sessions.add(extractGameSessionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all game sessions: " + e.getMessage());
        }
        
        return sessions;
    }
    

    public List<GameSession> findByRoom(int roomId) {
        List<GameSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM game_sessions WHERE room_id = ? ORDER BY start_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sessions.add(extractGameSessionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding game sessions by room: " + e.getMessage());
        }
        return sessions;
    }
    

    public List<GameSession> findByDateRange(LocalDateTime start, LocalDateTime end) {
        List<GameSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM game_sessions WHERE start_time BETWEEN ? AND ? ORDER BY start_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                sessions.add(extractGameSessionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding game sessions by date range: " + e.getMessage());
        }
        
        return sessions;
    }
    

    public double getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT SUM(revenue) as total FROM game_sessions WHERE start_time BETWEEN ? AND ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }
        
        return 0.0;
    }
    

    public Map<Room, Double> getRevenueByRoom(LocalDateTime start, LocalDateTime end) {
        Map<Room, Double> revenueMap = new HashMap<>();
        String sql = "SELECT room_id, SUM(revenue) as total FROM game_sessions WHERE start_time BETWEEN ? AND ? GROUP BY room_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int roomId = rs.getInt("room_id");
                double revenue = rs.getDouble("total");
                Room room = roomDAO.findById(roomId);
                if (room != null) {
                    revenueMap.put(room, revenue);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by room: " + e.getMessage());
        }
        
        return revenueMap;
    }
    

    public void save(GameSession session) {
        String sql = "INSERT INTO game_sessions (booking_id, room_id, start_time, end_time, completed, time_spent, hints_used, rating, review, revenue) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (session.getBooking() != null) {
                stmt.setInt(1, session.getBooking().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, session.getRoom().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));
            
            if (session.getEndTime() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(session.getEndTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            stmt.setBoolean(5, session.isCompleted());
            stmt.setInt(6, session.getTimeSpent());
            stmt.setInt(7, session.getHintsUsed());
            
            if (session.getRating() > 0) {
                stmt.setInt(8, session.getRating());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            stmt.setString(9, session.getReview());
            stmt.setDouble(10, session.getRevenue());
            
            stmt.executeUpdate();
            
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                session.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error saving game session: " + e.getMessage());
        }
    }

    public void update(GameSession session) {
        String sql = "UPDATE game_sessions SET booking_id = ?, room_id = ?, start_time = ?, end_time = ?, completed = ?, time_spent = ?, hints_used = ?, rating = ?, review = ?, revenue = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (session.getBooking() != null) {
                stmt.setInt(1, session.getBooking().getId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, session.getRoom().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));
            
            if (session.getEndTime() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(session.getEndTime()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            stmt.setBoolean(5, session.isCompleted());
            stmt.setInt(6, session.getTimeSpent());
            stmt.setInt(7, session.getHintsUsed());
            
            if (session.getRating() > 0) {
                stmt.setInt(8, session.getRating());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }
            
            stmt.setString(9, session.getReview());
            stmt.setDouble(10, session.getRevenue());
            stmt.setInt(11, session.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating game session: " + e.getMessage());
        }
    }
    

    public void delete(int id) {
        String sql = "DELETE FROM game_sessions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting game session: " + e.getMessage());
        }
    }

    private GameSession extractGameSessionFromResultSet(ResultSet rs) throws SQLException {
        GameSession session = new GameSession();
        session.setId(rs.getInt("id"));
        
        int bookingId = rs.getInt("booking_id");
        if (bookingId > 0) {
            Booking booking = bookingDAO.findById(bookingId);
            session.setBooking(booking);
            if (booking != null) {
                session.setPlayers(booking.getPlayers());
            }
        }
        
        int roomId = rs.getInt("room_id");
        Room room = roomDAO.findById(roomId);
        session.setRoom(room);
        
        Timestamp startTime = rs.getTimestamp("start_time");
        if (startTime != null) {
            session.setStartTime(startTime.toLocalDateTime());
        }
        
        Timestamp endTime = rs.getTimestamp("end_time");
        if (endTime != null) {
            session.setEndTime(endTime.toLocalDateTime());
        }
        
        session.setCompleted(rs.getBoolean("completed"));
        session.setTimeSpent(rs.getInt("time_spent"));
        session.setHintsUsed(rs.getInt("hints_used"));
        
        int rating = rs.getInt("rating");
        if (!rs.wasNull()) {
            session.setRating(rating);
        }
        
        session.setReview(rs.getString("review"));
        session.setRevenue(rs.getDouble("revenue"));
        
        return session;
    }
}
