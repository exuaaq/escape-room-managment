package com.escaperoom.views;

import com.escaperoom.dao.*;
import com.escaperoom.models.*;
import com.escaperoom.utils.AlertUtil;
import com.escaperoom.utils.PDFExporter;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ReportsView {
    
    private Stage primaryStage;
    private User currentUser;
    private GameSessionDAO gameSessionDAO;
    private RoomDAO roomDAO;
    private PlayerDAO playerDAO;
    private BookingDAO bookingDAO;
    
    public ReportsView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.gameSessionDAO = new GameSessionDAO();
        this.roomDAO = new RoomDAO();
        this.playerDAO = new PlayerDAO();
        this.bookingDAO = new BookingDAO();
    }
    
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("management-root");
        
        root.setTop(createHeader());
        root.setCenter(createContent());
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        
        return scene;
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.setPadding(new Insets(20));
        header.getStyleClass().add("header-box");
        
        HBox titleBox = new HBox(20);
        titleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button backButton = new Button("← Back to Dashboard");
        backButton.setOnAction(e -> backToDashboard());
        backButton.getStyleClass().add("back-button");
        
        Label titleLabel = new Label("Reports & Analytics");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        titleBox.getChildren().addAll(backButton, titleLabel);
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    private TabPane createContent() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        Tab revenueTab = new Tab("Revenue Report", createRevenueReport());
        Tab playerTab = new Tab("Player Statistics", createPlayerStatistics());
        Tab roomTab = new Tab("Room Performance", createRoomPerformance());
        Tab bookingTab = new Tab("Booking Report", createBookingReport());
        
        tabPane.getTabs().addAll(revenueTab, playerTab, roomTab, bookingTab);
        
        return tabPane;
    }
    
    private VBox createRevenueReport() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Revenue Report");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        

        HBox dateBox = new HBox(10);
        dateBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label fromLabel = new Label("From:");
        DatePicker fromDate = new DatePicker(LocalDateTime.now().minusMonths(1).toLocalDate());
        
        Label toLabel = new Label("To:");
        DatePicker toDate = new DatePicker(LocalDateTime.now().toLocalDate());
        
        Button generateButton = new Button("Generate Report");
        generateButton.getStyleClass().add("primary-button");
        
        Button exportButton = new Button("Export to PDF");
        exportButton.setOnAction(e -> exportRevenueToPDF(fromDate, toDate));
        
        dateBox.getChildren().addAll(fromLabel, fromDate, toLabel, toDate, generateButton, exportButton);

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(20);
        
        generateButton.setOnAction(e -> {
            LocalDateTime start = fromDate.getValue().atStartOfDay();
            LocalDateTime end = toDate.getValue().atTime(23, 59, 59);
            
            double totalRevenue = gameSessionDAO.getTotalRevenue(start, end);
            Map<Room, Double> revenueByRoom = gameSessionDAO.getRevenueByRoom(start, end);
            
            StringBuilder report = new StringBuilder();
            report.append("REVENUE REPORT\n");
            report.append("=" .repeat(50)).append("\n\n");
            report.append("Period: ").append(start.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            report.append(" to ").append(end.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");
            report.append("Total Revenue: ").append(String.format("%.2f KM", totalRevenue)).append("\n\n");
            report.append("Revenue by Room:\n");
            report.append("-".repeat(50)).append("\n");
            
            for (Map.Entry<Room, Double> entry : revenueByRoom.entrySet()) {
                report.append(String.format("%-30s: %10.2f KM\n", entry.getKey().getName(), entry.getValue()));
            }
            
            reportArea.setText(report.toString());
        });
        
        box.getChildren().addAll(titleLabel, dateBox, reportArea);
        
        return box;
    }
    
    private VBox createPlayerStatistics() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Player Statistics - Top Players");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Button exportButton = new Button("Export to PDF");
        exportButton.setOnAction(e -> exportPlayersToPDF());
        
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(20);

        List<Player> topPlayers = playerDAO.getTopPlayers(20);
        StringBuilder report = new StringBuilder();
        report.append("TOP PLAYERS LEADERBOARD\n");
        report.append("=".repeat(80)).append("\n\n");
        report.append(String.format("%-5s %-25s %-10s %-10s %-12s %-12s\n", 
            "Rank", "Name", "Games", "Won", "Win Rate", "Avg Time"));
        report.append("-".repeat(80)).append("\n");
        
        int rank = 1;
        for (Player player : topPlayers) {
            report.append(String.format("%-5d %-25s %-10d %-10d %-12.1f%% %-12.1f\n",
                rank++,
                player.getFullName(),
                player.getTotalGamesPlayed(),
                player.getGamesWon(),
                player.getWinRate(),
                player.getAverageTime()));
        }
        
        reportArea.setText(report.toString());
        
        box.getChildren().addAll(titleLabel, exportButton, reportArea);
        
        return box;
    }
    
    private VBox createRoomPerformance() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Room Performance Report");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        Button exportButton = new Button("Export to PDF");
        exportButton.setOnAction(e -> exportRoomPerformanceToPDF());
        
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(20);

        List<Room> rooms = roomDAO.findAll();
        StringBuilder report = new StringBuilder();
        report.append("ROOM PERFORMANCE REPORT\n");
        report.append("=".repeat(80)).append("\n\n");
        report.append(String.format("%-30s %-10s %-15s %-12s\n", 
            "Room Name", "Rating", "Price (KM)", "Active"));
        report.append("-".repeat(80)).append("\n");
        
        for (Room room : rooms) {
            report.append(String.format("%-30s %-10.2f %-15.2f %-12s\n",
                room.getName(),
                room.getAverageRating(),
                room.getPrice(),
                room.isActive() ? "Yes" : "No"));
        }
        
        reportArea.setText(report.toString());
        
        box.getChildren().addAll(titleLabel, exportButton, reportArea);
        
        return box;
    }
    
    private VBox createBookingReport() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Booking Report");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("Status:");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("All", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED");
        statusCombo.setValue("All");
        
        Button generateButton = new Button("Generate Report");
        generateButton.getStyleClass().add("primary-button");
        
        Button exportButton = new Button("Export to PDF");
        exportButton.setOnAction(e -> exportBookingsToPDF(statusCombo.getValue()));
        
        filterBox.getChildren().addAll(statusLabel, statusCombo, generateButton, exportButton);
        
        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(20);
        
        generateButton.setOnAction(e -> {
            List<Booking> bookings;
            String status = statusCombo.getValue();
            
            if (status.equals("All")) {
                bookings = bookingDAO.findAll();
            } else {
                bookings = bookingDAO.findByStatus(BookingStatus.valueOf(status));
            }
            
            StringBuilder report = new StringBuilder();
            report.append("BOOKING REPORT\n");
            report.append("=".repeat(80)).append("\n\n");
            report.append("Filter: Status = ").append(status).append("\n");
            report.append("Total Bookings: ").append(bookings.size()).append("\n\n");
            report.append(String.format("%-5s %-25s %-20s %-12s %-10s\n", 
                "ID", "Room", "Scheduled Time", "Status", "Price"));
            report.append("-".repeat(80)).append("\n");
            
            for (Booking booking : bookings) {
                report.append(String.format("%-5d %-25s %-20s %-12s %-10.2f\n",
                    booking.getId(),
                    booking.getRoom() != null ? booking.getRoom().getName() : "N/A",
                    booking.getScheduledTime() != null ? 
                        booking.getScheduledTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                    booking.getStatus(),
                    booking.getTotalPrice()));
            }
            
            reportArea.setText(report.toString());
        });
        
        box.getChildren().addAll(titleLabel, filterBox, reportArea);
        
        return box;
    }
    
    private void exportRevenueToPDF(DatePicker fromDate, DatePicker toDate) {
        try {
            LocalDateTime start = fromDate.getValue().atStartOfDay();
            LocalDateTime end = toDate.getValue().atTime(23, 59, 59);
            
            double totalRevenue = gameSessionDAO.getTotalRevenue(start, end);
            Map<Room, Double> revenueByRoom = gameSessionDAO.getRevenueByRoom(start, end);

            List<String[]> tableData = new ArrayList<>();
            for (Map.Entry<Room, Double> entry : revenueByRoom.entrySet()) {
                tableData.add(new String[]{
                    entry.getKey().getName(),
                    String.format("%.2f KM", entry.getValue())
                });
            }
            
            String[] headers = {"Room", "Revenue"};
            PDFExporter exporter = new PDFExporter("Revenue Report", headers, tableData);
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Revenue Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("revenue_report.pdf");
            
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                exporter.exportToPDF(file.getAbsolutePath());
                AlertUtil.showSuccess("Success", "Report exported to PDF successfully!");
            }
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Failed to export report: " + e.getMessage());
        }
    }
    
    private void exportPlayersToPDF() {
        try {
            List<Player> topPlayers = playerDAO.getTopPlayers(20);
            
            List<String[]> tableData = new ArrayList<>();
            int rank = 1;
            for (Player player : topPlayers) {
                tableData.add(new String[]{
                    String.valueOf(rank++),
                    player.getFullName(),
                    String.valueOf(player.getTotalGamesPlayed()),
                    String.format("%.1f%%", player.getWinRate()),
                    String.format("%.1f min", player.getAverageTime())
                });
            }
            
            String[] headers = {"Rank", "Name", "Games", "Win Rate", "Avg Time"};
            PDFExporter exporter = new PDFExporter("Top Players Report", headers, tableData);
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Player Statistics Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("players_report.pdf");
            
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                exporter.exportToPDF(file.getAbsolutePath());
                AlertUtil.showSuccess("Success", "Report exported to PDF successfully!");
            }
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Failed to export report: " + e.getMessage());
        }
    }
    
    private void exportRoomPerformanceToPDF() {
        try {
            List<Room> rooms = roomDAO.findAll();
            
            List<String[]> tableData = new ArrayList<>();
            for (Room room : rooms) {
                tableData.add(new String[]{
                    room.getName(),
                    room.getTheme(),
                    String.format("%.2f", room.getAverageRating()),
                    String.format("%.2f KM", room.getPrice()),
                    room.isActive() ? "Yes" : "No"
                });
            }
            
            String[] headers = {"Room", "Theme", "Rating", "Price", "Active"};
            PDFExporter exporter = new PDFExporter("Room Performance Report", headers, tableData);
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Room Performance Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("rooms_report.pdf");
            
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                exporter.exportToPDF(file.getAbsolutePath());
                AlertUtil.showSuccess("Success", "Report exported to PDF successfully!");
            }
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Failed to export report: " + e.getMessage());
        }
    }
    
    private void exportBookingsToPDF(String status) {
        try {
            List<Booking> bookings;
            
            if (status.equals("All")) {
                bookings = bookingDAO.findAll();
            } else {
                bookings = bookingDAO.findByStatus(BookingStatus.valueOf(status));
            }
            
            List<String[]> tableData = new ArrayList<>();
            for (Booking booking : bookings) {
                tableData.add(new String[]{
                    String.valueOf(booking.getId()),
                    booking.getRoom() != null ? booking.getRoom().getName() : "N/A",
                    booking.getScheduledTime() != null ? 
                        booking.getScheduledTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                    booking.getStatus().toString(),
                    String.format("%.2f KM", booking.getTotalPrice())
                });
            }
            
            String[] headers = {"ID", "Room", "Scheduled Time", "Status", "Price"};
            PDFExporter exporter = new PDFExporter("Booking Report (Status: " + status + ")", headers, tableData);
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Booking Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("bookings_report.pdf");
            
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                exporter.exportToPDF(file.getAbsolutePath());
                AlertUtil.showSuccess("Success", "Report exported to PDF successfully!");
            }
        } catch (Exception e) {
            AlertUtil.showError("Export Error", "Failed to export report: " + e.getMessage());
        }
    }
    
    private void backToDashboard() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        DashboardView dashboardView = new DashboardView(primaryStage, currentUser);
        Scene scene = dashboardView.createScene();
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(wasFullScreen);
    }
}
