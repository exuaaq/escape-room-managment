package com.escaperoom;

import com.escaperoom.database.DatabaseConnection;
import com.escaperoom.views.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class EscapeRoomApp extends Application {
    
    @Override
    public void start(Stage primaryStage) {

        if (!DatabaseConnection.testConnection()) {
            System.err.println("WARNING: Could not connect to database!");
            System.err.println("Please ensure MySQL is running and database is created.");
            System.err.println("Run the database_schema.sql file to set up the database.");
        }
        
        primaryStage.setTitle("Escape Room Management System");
        

        LoginView loginView = new LoginView(primaryStage);
        Scene loginScene = loginView.createScene();
        
        primaryStage.setScene(loginScene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
