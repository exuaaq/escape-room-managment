package com.escaperoom.models;

import com.escaperoom.interfaces.Bookable;
import java.time.LocalDateTime;


public class Room implements Bookable {
    private int id;
    private String name;
    private String theme;
    private int difficulty;
    private int capacity;
    private double price;
    private int duration;
    private String description;
    private boolean isActive;
    private double averageRating;
    

    public Room() {}
    
    public Room(int id, String name, String theme, int difficulty, int capacity, 
                double price, int duration, String description, boolean isActive, double averageRating) {
        this.id = id;
        this.name = name;
        this.theme = theme;
        this.difficulty = difficulty;
        this.capacity = capacity;
        this.price = price;
        this.duration = duration;
        this.description = description;
        this.isActive = isActive;
        this.averageRating = averageRating;
    }

    @Override
    public boolean isAvailable(LocalDateTime dateTime) {
        // provjerava se u dao
        return isActive;
    }
    
    @Override
    public double calculatePrice(int numberOfPeople) {
        // po roomu ne po osobi
        return price;
    }
    
    @Override
    public int getCapacity() {
        return capacity;
    }
    
    @Override
    public String getName() {
        return name;
    }
    

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public int getDifficulty() {
        return difficulty;
    }
    
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
    
    @Override
    public String toString() {
        return name + " (" + theme + ")";
    }
}
