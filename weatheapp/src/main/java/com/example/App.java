package com.example;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class App extends Application {

    private TextField cityInput;
    private Label weatherDisplay;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Quick Weather App");

        // --- 1. UI Components ---
        Label titleLabel = new Label("Enter City Name:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        cityInput = new TextField();
        cityInput.setPromptText("e.g., London, Tokyo, New York");
        cityInput.setMaxWidth(250);

        Button fetchButton = new Button("Get Weather");
        
        weatherDisplay = new Label("Weather data will appear here.");
        weatherDisplay.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-text-alignment: center;");
        weatherDisplay.setWrapText(true);

        // --- 2. Event Handling ---
        fetchButton.setOnAction(e -> handleFetchWeather());
        cityInput.setOnAction(e -> handleFetchWeather());

        // --- 3. Layout Setup ---
        VBox root = new VBox(15); 
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(titleLabel, cityInput, fetchButton, weatherDisplay);

        // --- 4. Scene & Stage ---
        Scene scene = new Scene(root, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleFetchWeather() {
        String city = cityInput.getText().trim();
        if (city.isEmpty()) {
            weatherDisplay.setText("Please enter a city name first!");
            return;
        }

        weatherDisplay.setText("Fetching data...");
        
        new Thread(() -> {
            String weatherData = fetchWeatherData(city);
            javafx.application.Platform.runLater(() -> weatherDisplay.setText(weatherData));
        }).start();
    }

    // --- 5. API Fetching Logic ---
    private String fetchWeatherData(String city) {
        try {
            // Clean up city string space formats and tell wttr.in to return format option 3 (clean plain text summary)
            String cleanCity = city.trim().replace(" ", "+");
            String urlString = "https://wttr.in/" + cleanCity + "?format=3";
            
            java.net.URL url = new java.net.URL(urlString);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getInputStream(), "UTF-8")
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return "Error: Could not find city or server error (Code: " + responseCode + ")";
            }
        } catch (Exception e) {
            // This displays the precise reason your connection fell through right on the app interface
            return "Connection Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}