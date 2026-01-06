package com.escaperoom.views;

import com.escaperoom.dao.*;
import com.escaperoom.models.Player;
import com.escaperoom.models.Room;
import com.escaperoom.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;


public class DashboardView {
    
    private Stage primaryStage;
    private User currentUser;
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;
    private PlayerDAO playerDAO;
    private GameSessionDAO gameSessionDAO;
    
    public DashboardView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.roomDAO = new RoomDAO();
        this.bookingDAO = new BookingDAO();
        this.playerDAO = new PlayerDAO();
        this.gameSessionDAO = new GameSessionDAO();
    }
    
    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("dashboard-root");

        root.setTop(createTopNavigation());

        root.setCenter(createMainContent());
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        
        return scene;
    }
    
    private HBox createTopNavigation() {
        HBox topNav = new HBox();
        topNav.setPadding(new Insets(10, 20, 10, 20));
        topNav.setAlignment(Pos.CENTER_RIGHT);
        topNav.getStyleClass().add("top-navigation");
        
        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout());
        
        topNav.getChildren().add(logoutButton);
        
        return topNav;
    }
    
    private VBox createMainContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        

        Label welcomeLabel = new Label("Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        welcomeLabel.getStyleClass().add("welcome-label");

        HBox statsBox = createStatisticsCards();

        VBox leaderboardBox = createLeaderboard();
        HBox quickActionsBox = createQuickActions();
        
        content.getChildren().addAll(welcomeLabel, statsBox, leaderboardBox, quickActionsBox);
        
        return content;
    }
    
    private HBox createStatisticsCards() {
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER);
        

        int totalRooms = roomDAO.findAll().size();
        VBox roomCard = createStatCard("Total Rooms", String.valueOf(totalRooms), "🏠");

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        int todayBookings = bookingDAO.findByDateRange(startOfDay, endOfDay).size();
        VBox bookingCard = createStatCard("Bookings Today", String.valueOf(todayBookings), "📅");
        

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime now = LocalDateTime.now();
        double monthRevenue = gameSessionDAO.getTotalRevenue(startOfMonth, now);
        VBox revenueCard = createStatCard("Revenue This Month", String.format("%.2f KM", monthRevenue), "💰");

        int totalPlayers = playerDAO.findAll().size();
        VBox playerCard = createStatCard("Total Players", String.valueOf(totalPlayers), "👥");
        
        statsBox.getChildren().addAll(roomCard, bookingCard, revenueCard, playerCard);
        
        return statsBox;
    }
    
    private VBox createStatCard(String title, String value, String emoji) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(250);
        card.setPrefHeight(120);
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setFont(Font.font(36));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        titleLabel.getStyleClass().add("stat-title");
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        valueLabel.getStyleClass().add("stat-value");
        
        card.getChildren().addAll(emojiLabel, titleLabel, valueLabel);
        
        return card;
    }
    
    private VBox createLeaderboard() {
        VBox leaderboardBox = new VBox(10);
        leaderboardBox.setPadding(new Insets(20));
        leaderboardBox.getStyleClass().add("leaderboard-box");
        
        Label titleLabel = new Label("Top 5 Players - Leaderboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        TableView<Player> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<Player, String> rankCol = new TableColumn<>("#");
        rankCol.setCellFactory(col -> new TableCell<Player, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        rankCol.setPrefWidth(50);
        
        TableColumn<Player, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        nameCol.setPrefWidth(200);
        
        TableColumn<Player, String> gamesCol = new TableColumn<>("Games");
        gamesCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getTotalGamesPlayed())));
        gamesCol.setPrefWidth(80);
        
        TableColumn<Player, String> winRateCol = new TableColumn<>("Win Rate");
        winRateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("%.1f%%", data.getValue().getWinRate())));
        winRateCol.setPrefWidth(100);
        
        TableColumn<Player, String> avgTimeCol = new TableColumn<>("Avg Time");
        avgTimeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("%.1f min", data.getValue().getAverageTime())));
        avgTimeCol.setPrefWidth(100);
        
        table.getColumns().addAll(rankCol, nameCol, gamesCol, winRateCol, avgTimeCol);
        
        List<Player> topPlayers = playerDAO.getTopPlayers(5);
        table.getItems().addAll(topPlayers);
        
        leaderboardBox.getChildren().addAll(titleLabel, table);
        
        return leaderboardBox;
    }
    
    private HBox createQuickActions() {
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button roomsButton = createActionButton("Manage Rooms");
        roomsButton.setOnAction(e -> showRoomManagement());
        
        Button bookingsButton = createActionButton("Manage Bookings");
        bookingsButton.setOnAction(e -> showBookingManagement());
        
        Button playersButton = createActionButton("Manage Players");
        playersButton.setOnAction(e -> showPlayerManagement());
        
        Button sessionsButton = createActionButton("Game Sessions");
        sessionsButton.setOnAction(e -> showGameSessionView());
        
        Button reportsButton = createActionButton("View Reports");
        reportsButton.setOnAction(e -> showReports());
        
        actionsBox.getChildren().addAll(roomsButton, bookingsButton, playersButton, sessionsButton, reportsButton);
        
        return actionsBox;
    }
    
    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.getStyleClass().add("action-button");
        return button;
    }
    
    private void refreshDashboard() {
        Scene scene = createScene();
        primaryStage.setScene(scene);
    }
    
    private void logout() {
        LoginView loginView = new LoginView(primaryStage);
        Scene loginScene = loginView.createScene();
        primaryStage.setScene(loginScene);
        primaryStage.setMaximized(false);
        primaryStage.setWidth(600);
        primaryStage.setHeight(500);
        primaryStage.centerOnScreen();
    }
    
    private void showRoomManagement() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        RoomManagementView roomView = new RoomManagementView(primaryStage, currentUser);
        Scene scene = roomView.createScene();
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(wasFullScreen);
    }
    
    private void showBookingManagement() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        BookingManagementView bookingView = new BookingManagementView(primaryStage, currentUser);
        Scene scene = bookingView.createScene();
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(wasFullScreen);
    }
    
    private void showPlayerManagement() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        PlayerManagementView playerView = new PlayerManagementView(primaryStage, currentUser);
        Scene scene = playerView.createScene();
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(wasFullScreen);
    }
    
    private void showGameSessionView() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        GameSessionView sessionView = new GameSessionView(primaryStage, currentUser);
        Scene scene = sessionView.createScene();
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(wasFullScreen);
    }
    
    private void showReports() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        ReportsView reportsView = new ReportsView(primaryStage, currentUser);
        Scene scene = reportsView.createScene();
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(wasFullScreen);
    }
}
