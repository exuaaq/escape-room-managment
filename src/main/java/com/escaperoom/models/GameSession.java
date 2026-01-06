package com.escaperoom.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class GameSession {
    private int id;
    private Booking booking;
    private Room room;
    private List<Player> players;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean completed;
    private int timeSpent;
    private int hintsUsed;
    private int rating;
    private String review;
    private double revenue;
    

    public GameSession() {
        this.players = new ArrayList<>();
    }
    
    public GameSession(int id, Booking booking, Room room, List<Player> players,
                       LocalDateTime startTime, LocalDateTime endTime, boolean completed,
                       int timeSpent, int hintsUsed, int rating, String review, double revenue) {
        this.id = id;
        this.booking = booking;
        this.room = room;
        this.players = players != null ? players : new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
        this.completed = completed;
        this.timeSpent = timeSpent;
        this.hintsUsed = hintsUsed;
        this.rating = rating;
        this.review = review;
        this.revenue = revenue;
    }
    

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    public Room getRoom() {
        return room;
    }
    
    public void setRoom(Room room) {
        this.room = room;
    }
    
    public List<Player> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public int getTimeSpent() {
        return timeSpent;
    }
    
    public void setTimeSpent(int timeSpent) {
        this.timeSpent = timeSpent;
    }
    
    public int getHintsUsed() {
        return hintsUsed;
    }
    
    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getReview() {
        return review;
    }
    
    public void setReview(String review) {
        this.review = review;
    }
    
    public double getRevenue() {
        return revenue;
    }
    
    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }
    
    @Override
    public String toString() {
        return "Session #" + id + " - " + (room != null ? room.getName() : "N/A");
    }
}
