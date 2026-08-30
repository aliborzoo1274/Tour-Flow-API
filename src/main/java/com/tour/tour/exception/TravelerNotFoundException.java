package com.tour.tour.exception;

public class TravelerNotFoundException extends RuntimeException {

    public TravelerNotFoundException(Long id) {
        super("Traveler with id " + id + " was not found");
    }
}
