package com.escaperoom.views;

import com.escaperoom.dao.*;
import com.escaperoom.models.*;
import com.escaperoom.utils.AlertUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
public class GameSessionView {
    
    private Stage primaryStage;
    private User currentUser;
    private GameSessionDAO gameSessionDAO;
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;
    private TableView<GameSession> sessionTable;
    private ObservableList<GameSession> sessionList;
    
    public GameSessionView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.gameSessionDAO = new GameSessionDAO();
        this.bookingDAO = new BookingDAO();
        this.roomDAO = new RoomDAO();
        this.sessionList = FXCollections.observableArrayList();
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
        
        Label titleLabel = new Label("Game Sessions");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        titleBox.getChildren().addAll(backButton, titleLabel);
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    private VBox createContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        HBox actionBar = createActionBar();
        sessionTable = createSessionTable();
        
        content.getChildren().addAll(actionBar, sessionTable);
        
        loadSessions();
        
        return content;
    }
    
    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(10));
        actionBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button addButton = new Button("Add Session");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddDialog());
        
        Button viewButton = new Button("View Details");
        viewButton.setOnAction(e -> viewDetails());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadSessions());
        
        actionBar.getChildren().addAll(addButton, viewButton, refreshButton);
        
        return actionBar;
    }
    
    private TableView<GameSession> createSessionTable() {
        TableView<GameSession> table = new TableView<>();
        table.setItems(sessionList);
        
        TableColumn<GameSession, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<GameSession, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getRoom() != null ? data.getValue().getRoom().getName() : "N/A"));
        roomCol.setPrefWidth(180);
        
        TableColumn<GameSession, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getStartTime() != null ? 
            data.getValue().getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"));
        dateCol.setPrefWidth(150);
        
        TableColumn<GameSession, Integer> timeCol = new TableColumn<>("Time (min)");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeSpent"));
        timeCol.setPrefWidth(100);
        
        TableColumn<GameSession, Boolean> completedCol = new TableColumn<>("Completed");
        completedCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
        completedCol.setPrefWidth(100);
        completedCol.setCellFactory(col -> new TableCell<GameSession, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "✓ Yes" : "✗ No");
                    setStyle(item ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
                }
            }
        });
        
        TableColumn<GameSession, Integer> hintsCol = new TableColumn<>("Hints");
        hintsCol.setCellValueFactory(new PropertyValueFactory<>("hintsUsed"));
        hintsCol.setPrefWidth(80);
        
        TableColumn<GameSession, Integer> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingCol.setPrefWidth(80);
        
        TableColumn<GameSession, Double> revenueCol = new TableColumn<>("Revenue (KM)");
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        revenueCol.setPrefWidth(120);
        
        table.getColumns().addAll(idCol, roomCol, dateCol, timeCol, completedCol, hintsCol, ratingCol, revenueCol);
        
        return table;
    }
    
    private void loadSessions() {
        List<GameSession> sessions = gameSessionDAO.findAll();
        sessionList.clear();
        sessionList.addAll(sessions);
    }
    
    private void showAddDialog() {
        Dialog<GameSession> dialog = new Dialog<>();
        dialog.setTitle("Add Game Session");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        
        GridPane grid = createSessionForm();
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return extractSessionFromForm(grid);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(session -> {
            if (session != null) {
                gameSessionDAO.save(session);
                loadSessions();
                AlertUtil.showSuccess("Success", "Game session added successfully!");
            }
        });
    }
    
    private GridPane createSessionForm() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setPrefWidth(400);
        
        int row = 0;

        Label roomLabel = new Label("Room:");
        roomLabel.setMinWidth(120);
        grid.add(roomLabel, 0, row);
        ComboBox<Room> roomCombo = new ComboBox<>();
        roomCombo.getItems().addAll(roomDAO.findAll());
        roomCombo.setUserData("room");
        grid.add(roomCombo, 1, row++);

        Label startTimeLabel = new Label("Start Time:");
        startTimeLabel.setMinWidth(120);
        grid.add(startTimeLabel, 0, row);
        TextField startTimeField = new TextField(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        startTimeField.setUserData("startTime");
        grid.add(startTimeField, 1, row++);
        

        Label timeSpentLabel = new Label("Time Spent (min):");
        timeSpentLabel.setMinWidth(120);
        grid.add(timeSpentLabel, 0, row);
        Spinner<Integer> timeSpinner = new Spinner<>(1, 300, 60);
        timeSpinner.setUserData("timeSpent");
        grid.add(timeSpinner, 1, row++);
        

        Label completedLabel = new Label("Completed:");
        completedLabel.setMinWidth(120);
        grid.add(completedLabel, 0, row);
        CheckBox completedCheck = new CheckBox();
        completedCheck.setSelected(true);
        completedCheck.setUserData("completed");
        grid.add(completedCheck, 1, row++);

        Label hintsLabel = new Label("Hints Used:");
        hintsLabel.setMinWidth(120);
        grid.add(hintsLabel, 0, row);
        Spinner<Integer> hintsSpinner = new Spinner<>(0, 50, 2);
        hintsSpinner.setUserData("hintsUsed");
        grid.add(hintsSpinner, 1, row++);
        

        Label ratingLabel = new Label("Rating (1-5):");
        ratingLabel.setMinWidth(120);
        grid.add(ratingLabel, 0, row);
        Spinner<Integer> ratingSpinner = new Spinner<>(1, 5, 4);
        ratingSpinner.setUserData("rating");
        grid.add(ratingSpinner, 1, row++);

        Label revenueLabel = new Label("Revenue (KM):");
        revenueLabel.setMinWidth(120);
        grid.add(revenueLabel, 0, row);
        TextField revenueField = new TextField("50.00");
        revenueField.setUserData("revenue");
        grid.add(revenueField, 1, row++);
        

        Label reviewLabel = new Label("Review:");
        reviewLabel.setMinWidth(120);
        grid.add(reviewLabel, 0, row);
        TextArea reviewArea = new TextArea();
        reviewArea.setPrefRowCount(3);
        reviewArea.setUserData("review");
        grid.add(reviewArea, 1, row++);
        
        return grid;
    }
    
    private GameSession extractSessionFromForm(GridPane grid) {
        try {
            GameSession session = new GameSession();
            
            for (javafx.scene.Node node : grid.getChildren()) {
                Object userData = node.getUserData();
                if (userData == null) continue;
                
                String fieldName = userData.toString();
                
                switch (fieldName) {
                    case "room":
                        Room room = ((ComboBox<Room>) node).getValue();
                        if (room == null) {
                            AlertUtil.showError("Validation Error", "Please select a room");
                            return null;
                        }
                        session.setRoom(room);
                        break;
                    case "startTime":
                        String timeStr = ((TextField) node).getText();
                        LocalDateTime startTime = LocalDateTime.parse(timeStr, 
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        session.setStartTime(startTime);
                        break;
                    case "timeSpent":
                        int timeSpent = ((Spinner<Integer>) node).getValue();
                        session.setTimeSpent(timeSpent);
                        session.setEndTime(session.getStartTime().plusMinutes(timeSpent));
                        break;
                    case "completed":
                        session.setCompleted(((CheckBox) node).isSelected());
                        break;
                    case "hintsUsed":
                        session.setHintsUsed(((Spinner<Integer>) node).getValue());
                        break;
                    case "rating":
                        session.setRating(((Spinner<Integer>) node).getValue());
                        break;
                    case "revenue":
                        double revenue = Double.parseDouble(((TextField) node).getText());
                        session.setRevenue(revenue);
                        break;
                    case "review":
                        session.setReview(((TextArea) node).getText());
                        break;
                }
            }
            
            return session;
        } catch (Exception e) {
            AlertUtil.showError("Error", "An error occurred: " + e.getMessage());
            return null;
        }
    }
    
    private void viewDetails() {
        GameSession selectedSession = sessionTable.getSelectionModel().getSelectedItem();
        
        if (selectedSession == null) {
            AlertUtil.showWarning("No Selection", "Please select a session to view");
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Game Session Details\n\n");
        details.append("Room: ").append(selectedSession.getRoom().getName()).append("\n");
        details.append("Start Time: ").append(selectedSession.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        details.append("Time Spent: ").append(selectedSession.getTimeSpent()).append(" minutes\n");
        details.append("Completed: ").append(selectedSession.isCompleted() ? "Yes" : "No").append("\n");
        details.append("Hints Used: ").append(selectedSession.getHintsUsed()).append("\n");
        details.append("Rating: ").append(selectedSession.getRating()).append("/5\n");
        details.append("Revenue: ").append(selectedSession.getRevenue()).append(" KM\n");
        if (selectedSession.getReview() != null && !selectedSession.getReview().isEmpty()) {
            details.append("\nReview:\n").append(selectedSession.getReview());
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session Details");
        alert.setHeaderText(null);
        alert.setContentText(details.toString());
        alert.showAndWait();
    }
    
    private void backToDashboard() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        DashboardView dashboardView = new DashboardView(primaryStage, currentUser);
        Scene scene = dashboardView.createScene();
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(wasFullScreen);
    }
}
