package com.example.FlightComparator;

public class FlightDetails {
    private String operator;
    private String flightNumber;
    private String price;

    // Constructors, Getters, Setters, and toString methods

    public FlightDetails(String operator, String flightNumber, String price) {
        this.operator = operator;
        this.flightNumber = flightNumber;
        this.price = price;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
