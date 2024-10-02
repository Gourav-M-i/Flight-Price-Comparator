package com.example.FlightComparator;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FlightComparator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Take input for travel date from the user
        System.out.println("Enter Travel Date (YYYY/MM/DD): ");
        String travelDate = scanner.nextLine();

        // Fixed route
        String fromCity = "BANGALORE";
        String toCity = "DELHI";

        // Start scraping process
        scrapeFlightData(travelDate, fromCity, toCity);
    }

    public static void scrapeFlightData(String travelDate, String fromCity, String toCity) {
        // Setup Chrome WebDriver
        WebDriverManager.chromedriver().setup();
//        WebDriverManager.chromedriver().clearDriverCache().setup();
        WebDriver driver = new ChromeDriver();


        // Scrape data from Cleartrip
        List<FlightDetails> cleartripFlights = scrapeCleartrip(driver, travelDate, fromCity, toCity);
//
//        for(FlightDetails f:cleartripFlights){
//            System.out.println(f.getFlightNumber()+f.getOperator()+f.getPrice());
//        }

        // Scrape data from Paytm
        List<FlightDetails> paytmFlights = scrapePaytm(driver, travelDate, fromCity, toCity);

        // Close the driver
        driver.quit();

        // Compare and generate CSV
        compareAndGenerateCSV(cleartripFlights, paytmFlights);
    }

    private static List<FlightDetails> scrapeCleartrip(WebDriver driver, String travelDate, String fromCity, String toCity) {
        List<FlightDetails> flights = new ArrayList<>();

        // Navigate to Cleartrip flight search page
        driver.get("https://www.cleartrip.com/flights");
//        System.out.println("hi");

        // Input the 'From' city
        WebElement fromCityInput = driver.findElement(By.xpath("//input[@placeholder='Where from?']"));
        fromCityInput.clear();
        fromCityInput.sendKeys(fromCity);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            Thread.sleep(3000);  // 3000 milliseconds = 3 seconds
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }

        try {
            // Wait until the specific element is visible (adjust the locator as needed)
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div/div[2]/div[1]/div[1]/div/div[1]/div[2]/div/div[2]/div/div[1]/div[2]/ul[1]")));
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Input the 'To' city
        WebElement toCityInput = driver.findElement(By.xpath("//input[@placeholder='Where to?']"));
        toCityInput.clear();
        toCityInput.sendKeys(toCity);

        try {
            Thread.sleep(3000);  // 3000 milliseconds = 3 seconds
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }

        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div/div/div[2]/div[1]/div[1]/div/div[1]/div[2]/div/div[2]/div/div[3]/div[2]/ul")));
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
        }


        WebElement dateInput = driver.findElement(By.xpath("/html/body/div[1]/div/main/div/div[1]/div[2]/div[1]/div[1]/div/div[1]/div[2]/div/div[4]/div/div/div/div[1]/div[2]"));
        dateInput.click();
        LocalDate date = LocalDate.parse(travelDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // Define the output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEE MMM d yyyy", Locale.ENGLISH);

        String formattedDate = date.format(outputFormatter);



        WebElement dateDiv = driver.findElement(By.xpath("//div[@aria-label='" + formattedDate + "']"));
        dateDiv.click();



        // Click on the Search button (Assuming a 'Search Flights' button is available)
        WebElement searchButton = driver.findElement(By.xpath("/html/body/div[1]/div/main/div/div/div[2]/div[1]/div[1]/div/div[1]/div[2]/div/div[7]/button"));
        searchButton.click();



        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div[1]/div/main/div/div/div[2]/div[2]/div[8]/div[2]/div[1]/div[1]/div/div[2]")));

        // Now find all elements containing flight blocks
        List<WebElement> flightBlocks = driver.findElements(By.cssSelector("div[data-testid='airlineBlock']"));

        // Iterate through each flight block and extract details
        for (WebElement flightBlock : flightBlocks) {
            try {
                // Extract flight name
                WebElement flightNameElement = flightBlock.findElement(By.xpath(".//p[@class='fw-500 fs-2 c-neutral-900']"));
                String flightName = flightNameElement.getText();

                // Extract flight number
                WebElement flightNumberElement = flightBlock.findElement(By.xpath(".//p[contains(@class, 'fs-1 c-neutral-400')]"));
                String flightNumber = flightNumberElement.getText();

                // Extract flight price
                WebElement flightPriceElement = flightBlock.findElement(By.xpath(".//p[@class='m-0 fs-5 fw-700 c-neutral-900 ta-right false']"));
                String flightPrice = flightPriceElement.getText();

                FlightDetails flightDetails=new FlightDetails(flightName,flightNumber,flightPrice);
                flights.add(flightDetails);
                // Print extracted data
//                System.out.println("Flight Name: " + flightName);
//                System.out.println("Flight Number: " + flightNumber);
//                System.out.println("Flight Price: " + flightPrice);
//                System.out.println("-----------------------------");
            } catch (Exception e) {
                System.out.println("Error extracting flight details: " + e.getMessage());
            }
        }

        // Return the list of flights
        return flights;
    }

    private static List<FlightDetails> scrapePaytm(WebDriver driver, String travelDate, String fromCity, String toCity) {
        List<FlightDetails> flights = new ArrayList<>();

        // Navigate to Paytm Flights website
        driver.get("https://tickets.paytm.com/flights");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement fromCityB = driver.findElement(By.id("srcCode"));
        fromCityB.click();
        try {
            // Wait until the specific element is visible (adjust the locator as needed)
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("text-box")));
            element.sendKeys("Bangalore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(3000);  // 3000 milliseconds = 3 seconds
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }



        try {
            // Wait until the specific element is visible (adjust the locator as needed)
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[1]/main/section/div[1]/div/div/div[1]/div[1]/div[1]/div/div/div[2]/div[2]/div/div[2]/div/div[1]")));
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Input the 'To' city
        WebElement toCityB = driver.findElement(By.id("destCode"));
        toCityB.click();
        try {
            // Wait until the specific element is visible (adjust the locator as needed)
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("text-box")));
            element.sendKeys("Delhi");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(3000);  // 3000 milliseconds = 3 seconds
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted");
        }

        

        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/div/div/div[1]/main/section/div[1]/div/div/div[1]/div[1]/div[2]/div/div/div[2]/div[2]/div/div[2]/div/div[1]")));
            element.click();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Input the travel date
//        WebElement dateInput = driver.findElement(By.id("departureDate"));
//        dateInput.click();
        LocalDate date = LocalDate.parse(travelDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        // Define the output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("EEE MMM d yyyy", Locale.ENGLISH);

        // Format the date into the desired format
        String formattedDate = date.format(outputFormatter);

        // Print the formatted date
//        System.out.println(formattedDate);

//        WebElement dateDiv = driver.findElement(By.xpath("//div[@aria-label='" + formattedDate + "']"));
//        System.out.println("kss"+dateDiv.getAttribute("aria-label"));
//        dateDiv.click();



        // Click on the Search button (Assuming a 'Search Flights' button is available)
        WebElement searchButton = driver.findElement(By.id("flightSearch"));
        searchButton.click();

        // Wait for the results page to load (you may need to implement WebDriverWait to wait for elements to appear)
//        try {
//            Thread.sleep(5000);  // Simple sleep, replace with WebDriverWait for better handling
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        try {
            wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("/html/body/div/div/div[1]/div/div[2]/div/div/div[2]/div[4]/section/div[1]")));

            WebElement flightsList = driver.findElement(By.id("flightsList"));

            // Find all direct div children of the flightsList element
            List<WebElement> divChildren = flightsList.findElements(By.xpath("./div"));

            // Iterate through each div and print the content or any specific data
            int maxDivsToShow = Math.min(divChildren.size(), 11);

// Iterate through each div and print the content or any specific data
            for (int i = 0; i < maxDivsToShow; i++) {
                WebElement div = divChildren.get(i);
                String divText = div.getText();

                // Split the text by newlines to create an array of lines
                String[] lines = divText.split("\\r?\\n");

                // Ensure there are enough lines to avoid IndexOutOfBoundsException
                if (lines.length >= 13) {
                    // Extract the 3rd line (flight name)
                    String flightName = lines[2];  // Index 2 is the 3rd line (0-based index)

                    // Extract the 13th line (price)
                    String price = lines[12];  // Index 12 is the 13th line

                    FlightDetails flightDetails=new FlightDetails(flightName,"123",price);

                    flights.add(flightDetails);

                    // Print the extracted flight name and price
//                    System.out.println("Flight Name: " + flightName);
//                    System.out.println("Price: " + price);
//                    System.out.println("----------------------");
                } else {
                    System.out.println("Insufficient data in div");
                }
            }
        } catch (Exception e) {
                System.out.println("Error extracting flight details: " + e.getMessage());
            }


        // Get flights and return the list
        return flights;
    }

    private static void compareAndGenerateCSV(List<FlightDetails> cleartripFlights, List<FlightDetails> paytmFlights) {
        List<FlightComparison> comparisons = new ArrayList<>();


        for (FlightDetails cleartripFlight : cleartripFlights) {
            for (FlightDetails paytmFlight : paytmFlights) {
                    comparisons.add(new FlightComparison(cleartripFlight.getOperator(), cleartripFlight.getFlightNumber(), cleartripFlight.getPrice(), paytmFlight.getPrice()));
            }
        }

        System.out.println("----------------------------------");
        System.out.println("Flight name\tFlight Number\tCleartrip\tPaytm");

        for (FlightComparison f:comparisons){
            System.out.println(f.getOperator()+"\t"+f.getFlightNumber()+"\t"+f.getCleartripPrice()+"\t"+f.getPaytmPrice());
        }

        // Generate the CSV file
        CSVWriter.writeCSV(comparisons);
    }
}
