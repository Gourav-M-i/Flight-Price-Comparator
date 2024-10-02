package com.example.FlightComparator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {

    public static void writeCSV(List<FlightComparison> flightComparisons) {
        for(FlightComparison a:flightComparisons){
            System.out.println("hi"+a.getOperator()+" "+a.getCleartripPrice()+" "+a.getPaytmPrice());
        }
        String csvFile = "FlightComparison.csv";
        try (FileWriter writer = new FileWriter(csvFile)) {

            // Write CSV Header
            writer.append("Flight Operator,Flight Number,Price on Cleartrip,Price on Paytm\n");

            // Write flight comparison data
            for (FlightComparison comparison : flightComparisons) {
                writer.append(comparison.getOperator())
                        .append(",")
                        .append(comparison.getFlightNumber())
                        .append(",")
                        .append(comparison.getCleartripPrice() != null ? comparison.getCleartripPrice() : "No Data Found")
                        .append(",")
                        .append(comparison.getPaytmPrice() != null ? comparison.getPaytmPrice() : "No Data Found")
                        .append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
