package com.escaperoom.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Booking {
    private int id;
    private Room room;
    private List<Player> players;
    private LocalDateTime bookingDate;
    private LocalDateTime scheduledTime;
    private BookingStatus status;
    private int numberOfPlayers;
    private double totalPrice;
    private String notes;
    
    // konmstruktori
    public Booking() {
        this.players = new ArrayList<>();
    }
    
    public Booking(int id, Room room, List<Player> players, LocalDateTime bookingDate,
                   LocalDateTime scheduledTime, BookingStatus status, int numberOfPlayers,
                   double totalPrice, String notes) {
        this.id = id;
        this.room = room;
        this.players = players != null ? players : new ArrayList<>();
        this.bookingDate = bookingDate;
        this.scheduledTime = scheduledTime;
        this.status = status;
        this.numberOfPlayers = numberOfPlayers;
        this.totalPrice = totalPrice;
        this.notes = notes;
    }
    

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
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
    
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
    
    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }
    
    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
    
    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public String toString() {
        return "Booking #" + id + " - " + (room != null ? room.getName() : "N/A");
    }
}
