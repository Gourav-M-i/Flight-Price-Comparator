package com.example.FlightComparator;

public class FlightComparison {
    private String operator;
    private String flightNumber;
    private String cleartripPrice;
    private String paytmPrice;

    // Constructors, Getters, Setters

    public FlightComparison(String operator, String flightNumber, String cleartripPrice, String paytmPrice) {
        this.operator = operator;
        this.flightNumber = flightNumber;
        this.cleartripPrice = cleartripPrice;
        this.paytmPrice = paytmPrice;
    }

    public String getOperator() {
        return operator;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getCleartripPrice() {
        return cleartripPrice;
    }

    public String getPaytmPrice() {
        return paytmPrice;
    }
}
