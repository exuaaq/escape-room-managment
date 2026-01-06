package com.escaperoom.models;

import java.time.LocalDateTime;


public class Player {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private int totalGamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private double averageTime;
    private int totalHintsUsed;
    private LocalDateTime registrationDate;
    

    public Player() {}
    
    public Player(int id, String firstName, String lastName, String email, String phone,
                  int totalGamesPlayed, int gamesWon, int gamesLost, double averageTime,
                  int totalHintsUsed, LocalDateTime registrationDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.totalGamesPlayed = totalGamesPlayed;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.averageTime = averageTime;
        this.totalHintsUsed = totalHintsUsed;
        this.registrationDate = registrationDate;
    }
    
    // racunanje winrate
    public double getWinRate() {
        if (totalGamesPlayed == 0) return 0.0;
        return (double) gamesWon / totalGamesPlayed * 100;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }
    
    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }
    
    public int getGamesWon() {
        return gamesWon;
    }
    
    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }
    
    public int getGamesLost() {
        return gamesLost;
    }
    
    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }
    
    public double getAverageTime() {
        return averageTime;
    }
    
    public void setAverageTime(double averageTime) {
        this.averageTime = averageTime;
    }
    
    public int getTotalHintsUsed() {
        return totalHintsUsed;
    }
    
    public void setTotalHintsUsed(int totalHintsUsed) {
        this.totalHintsUsed = totalHintsUsed;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    @Override
    public String toString() {
        return getFullName() + " (" + email + ")";
    }
}
