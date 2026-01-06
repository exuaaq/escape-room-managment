package com.escaperoom.dao;

import com.escaperoom.database.DatabaseConnection;
import com.escaperoom.models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class RoomDAO {
    

    public Room findById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractRoomFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding room: " + e.getMessage());
        }
        
        return null;
    }
    

    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all rooms: " + e.getMessage());
        }
        
        return rooms;
    }
    

    public List<Room> findActive() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE is_active = TRUE ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding active rooms: " + e.getMessage());
        }
        
        return rooms;
    }
    

    public List<Room> findByTheme(String theme) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE theme = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, theme);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                rooms.add(extractRoomFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding rooms by theme: " + e.getMessage());
        }
        
        return rooms;
    }
    

    public void save(Room room) {
        String sql = "INSERT INTO rooms (name, theme, difficulty, capacity, price, duration, description, is_active, average_rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, room.getName());
            stmt.setString(2, room.getTheme());
            stmt.setInt(3, room.getDifficulty());
            stmt.setInt(4, room.getCapacity());
            stmt.setDouble(5, room.getPrice());
            stmt.setInt(6, room.getDuration());
            stmt.setString(7, room.getDescription());
            stmt.setBoolean(8, room.isActive());
            stmt.setDouble(9, room.getAverageRating());
            
            stmt.executeUpdate();
            
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                room.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error saving room: " + e.getMessage());
        }
    }
    

    public void update(Room room) {
        String sql = "UPDATE rooms SET name = ?, theme = ?, difficulty = ?, capacity = ?, price = ?, duration = ?, description = ?, is_active = ?, average_rating = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, room.getName());
            stmt.setString(2, room.getTheme());
            stmt.setInt(3, room.getDifficulty());
            stmt.setInt(4, room.getCapacity());
            stmt.setDouble(5, room.getPrice());
            stmt.setInt(6, room.getDuration());
            stmt.setString(7, room.getDescription());
            stmt.setBoolean(8, room.isActive());
            stmt.setDouble(9, room.getAverageRating());
            stmt.setInt(10, room.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating room: " + e.getMessage());
        }
    }
    

    public void delete(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting room: " + e.getMessage());
        }
    }
    

    public void updateRating(int roomId, double newRating) {
        String sql = "UPDATE rooms SET average_rating = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, newRating);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating room rating: " + e.getMessage());
        }
    }
    

    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setName(rs.getString("name"));
        room.setTheme(rs.getString("theme"));
        room.setDifficulty(rs.getInt("difficulty"));
        room.setCapacity(rs.getInt("capacity"));
        room.setPrice(rs.getDouble("price"));
        room.setDuration(rs.getInt("duration"));
        room.setDescription(rs.getString("description"));
        room.setActive(rs.getBoolean("is_active"));
        room.setAverageRating(rs.getDouble("average_rating"));
        return room;
    }
}
