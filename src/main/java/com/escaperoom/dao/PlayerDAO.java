package com.escaperoom.dao;

import com.escaperoom.database.DatabaseConnection;
import com.escaperoom.models.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PlayerDAO {
    

    public Player findById(int id) {
        String sql = "SELECT * FROM players WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPlayerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding player: " + e.getMessage());
        }
        
        return null;
    }
    

    public Player findByEmail(String email) {
        String sql = "SELECT * FROM players WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPlayerFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding player by email: " + e.getMessage());
        }
        
        return null;
    }

    public List<Player> findAll() {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY first_name, last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                players.add(extractPlayerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all players: " + e.getMessage());
        }
        
        return players;
    }

    public List<Player> searchByName(String name) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE CONCAT(first_name, ' ', last_name) LIKE ? ORDER BY first_name, last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                players.add(extractPlayerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching players by name: " + e.getMessage());
        }
        
        return players;
    }

    public List<Player> getTopPlayers(int limit) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM players WHERE total_games_played > 0 ORDER BY (games_won / total_games_played) DESC, games_won DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                players.add(extractPlayerFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting top players: " + e.getMessage());
        }
        
        return players;
    }
    

    public void save(Player player) {
        String sql = "INSERT INTO players (first_name, last_name, email, phone, total_games_played, games_won, games_lost, average_time, total_hints_used) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, player.getFirstName());
            stmt.setString(2, player.getLastName());
            stmt.setString(3, player.getEmail());
            stmt.setString(4, player.getPhone());
            stmt.setInt(5, player.getTotalGamesPlayed());
            stmt.setInt(6, player.getGamesWon());
            stmt.setInt(7, player.getGamesLost());
            stmt.setDouble(8, player.getAverageTime());
            stmt.setInt(9, player.getTotalHintsUsed());
            
            stmt.executeUpdate();
            
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                player.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error saving player: " + e.getMessage());
        }
    }

    public void update(Player player) {
        String sql = "UPDATE players SET first_name = ?, last_name = ?, email = ?, phone = ?, total_games_played = ?, games_won = ?, games_lost = ?, average_time = ?, total_hints_used = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, player.getFirstName());
            stmt.setString(2, player.getLastName());
            stmt.setString(3, player.getEmail());
            stmt.setString(4, player.getPhone());
            stmt.setInt(5, player.getTotalGamesPlayed());
            stmt.setInt(6, player.getGamesWon());
            stmt.setInt(7, player.getGamesLost());
            stmt.setDouble(8, player.getAverageTime());
            stmt.setInt(9, player.getTotalHintsUsed());
            stmt.setInt(10, player.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating player: " + e.getMessage());
        }
    }
    

    public void delete(int id) {
        String sql = "DELETE FROM players WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting player: " + e.getMessage());
        }
    }
    

    public void updateStatistics(Player player) {
        update(player);
    }
    

    private Player extractPlayerFromResultSet(ResultSet rs) throws SQLException {
        Player player = new Player();
        player.setId(rs.getInt("id"));
        player.setFirstName(rs.getString("first_name"));
        player.setLastName(rs.getString("last_name"));
        player.setEmail(rs.getString("email"));
        player.setPhone(rs.getString("phone"));
        player.setTotalGamesPlayed(rs.getInt("total_games_played"));
        player.setGamesWon(rs.getInt("games_won"));
        player.setGamesLost(rs.getInt("games_lost"));
        player.setAverageTime(rs.getDouble("average_time"));
        player.setTotalHintsUsed(rs.getInt("total_hints_used"));
        
        Timestamp timestamp = rs.getTimestamp("registration_date");
        if (timestamp != null) {
            player.setRegistrationDate(timestamp.toLocalDateTime());
        }
        
        return player;
    }
}
