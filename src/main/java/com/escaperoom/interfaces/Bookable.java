package com.escaperoom.interfaces;

import java.time.LocalDateTime;


public interface Bookable {

    boolean isAvailable(LocalDateTime dateTime);
    

    double calculatePrice(int numberOfPeople);

    int getCapacity();

    String getName();
}
