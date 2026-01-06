package com.escaperoom.views;

import com.escaperoom.dao.PlayerDAO;
import com.escaperoom.models.Player;
import com.escaperoom.models.User;
import com.escaperoom.utils.AlertUtil;
import com.escaperoom.utils.ValidationUtil;
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

import java.util.List;


public class PlayerManagementView {
    
    private Stage primaryStage;
    private User currentUser;
    private PlayerDAO playerDAO;
    private TableView<Player> playerTable;
    private ObservableList<Player> playerList;
    private TextField searchField;
    
    public PlayerManagementView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.playerDAO = new PlayerDAO();
        this.playerList = FXCollections.observableArrayList();
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
        
        Label titleLabel = new Label("Player Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        titleBox.getChildren().addAll(backButton, titleLabel);
        header.getChildren().add(titleBox);
        
        return header;
    }
    
    private VBox createContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        HBox filterBar = createFilterBar();
        HBox actionBar = createActionBar();
        playerTable = createPlayerTable();
        
        content.getChildren().addAll(filterBar, actionBar, playerTable);
        
        loadPlayers();
        
        return content;
    }
    
    private HBox createFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setPadding(new Insets(10));
        filterBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Search:");
        searchField = new TextField();
        searchField.setPromptText("Search by name or email...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterPlayers());
        
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearFilter());
        
        filterBar.getChildren().addAll(searchLabel, searchField, clearButton);
        
        return filterBar;
    }
    
    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(10));
        actionBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button addButton = new Button("Add Player");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddDialog());
        
        Button editButton = new Button("Edit Player");
        editButton.setOnAction(e -> showEditDialog());
        
        Button deleteButton = new Button("Delete Player");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deletePlayer());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadPlayers());
        
        actionBar.getChildren().addAll(addButton, editButton, deleteButton, refreshButton);
        
        return actionBar;
    }
    
    private TableView<Player> createPlayerTable() {
        TableView<Player> table = new TableView<>();
        table.setItems(playerList);
        
        TableColumn<Player, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Player, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFullName()));
        nameCol.setPrefWidth(180);
        
        TableColumn<Player, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<Player, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);
        
        TableColumn<Player, Integer> gamesCol = new TableColumn<>("Games");
        gamesCol.setCellValueFactory(new PropertyValueFactory<>("totalGamesPlayed"));
        gamesCol.setPrefWidth(80);
        
        TableColumn<Player, String> winRateCol = new TableColumn<>("Win Rate");
        winRateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("%.1f%%", data.getValue().getWinRate())));
        winRateCol.setPrefWidth(100);
        
        TableColumn<Player, Double> avgTimeCol = new TableColumn<>("Avg Time");
        avgTimeCol.setCellValueFactory(new PropertyValueFactory<>("averageTime"));
        avgTimeCol.setPrefWidth(100);
        
        TableColumn<Player, Integer> hintsCol = new TableColumn<>("Hints Used");
        hintsCol.setCellValueFactory(new PropertyValueFactory<>("totalHintsUsed"));
        hintsCol.setPrefWidth(100);
        
        table.getColumns().addAll(idCol, nameCol, emailCol, phoneCol, gamesCol, winRateCol, avgTimeCol, hintsCol);
        
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                showEditDialog();
            }
        });
        
        return table;
    }
    
    private void loadPlayers() {
        List<Player> players = playerDAO.findAll();
        playerList.clear();
        playerList.addAll(players);
    }
    
    private void filterPlayers() {
        String searchText = searchField.getText().toLowerCase();
        
        if (searchText.isEmpty()) {
            loadPlayers();
            return;
        }
        
        List<Player> allPlayers = playerDAO.findAll();
        playerList.clear();
        
        for (Player player : allPlayers) {
            if (player.getFullName().toLowerCase().contains(searchText) ||
                player.getEmail().toLowerCase().contains(searchText)) {
                playerList.add(player);
            }
        }
    }
    
    private void clearFilter() {
        searchField.clear();
        loadPlayers();
    }
    
    private void showAddDialog() {
        Dialog<Player> dialog = new Dialog<>();
        dialog.setTitle("Add New Player");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        
        GridPane grid = createPlayerForm(null);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return extractPlayerFromForm(grid, null);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(player -> {
            if (player != null) {
                playerDAO.save(player);
                loadPlayers();
                AlertUtil.showSuccess("Success", "Player added successfully!");
            }
        });
    }
    
    private void showEditDialog() {
        Player selectedPlayer = playerTable.getSelectionModel().getSelectedItem();
        
        if (selectedPlayer == null) {
            AlertUtil.showWarning("No Selection", "Please select a player to edit");
            return;
        }
        
        Dialog<Player> dialog = new Dialog<>();
        dialog.setTitle("Edit Player");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        
        GridPane grid = createPlayerForm(selectedPlayer);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return extractPlayerFromForm(grid, selectedPlayer);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(player -> {
            if (player != null) {
                playerDAO.update(player);
                loadPlayers();
                AlertUtil.showSuccess("Success", "Player updated successfully!");
            }
        });
    }
    
    private GridPane createPlayerForm(Player player) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        int row = 0;
        
        grid.add(new Label("First Name:"), 0, row);
        TextField firstNameField = new TextField(player != null ? player.getFirstName() : "");
        firstNameField.setUserData("firstName");
        grid.add(firstNameField, 1, row++);
        
        grid.add(new Label("Last Name:"), 0, row);
        TextField lastNameField = new TextField(player != null ? player.getLastName() : "");
        lastNameField.setUserData("lastName");
        grid.add(lastNameField, 1, row++);
        
        grid.add(new Label("Email:"), 0, row);
        TextField emailField = new TextField(player != null ? player.getEmail() : "");
        emailField.setUserData("email");
        grid.add(emailField, 1, row++);
        
        grid.add(new Label("Phone:"), 0, row);
        TextField phoneField = new TextField(player != null ? player.getPhone() : "");
        phoneField.setUserData("phone");
        grid.add(phoneField, 1, row++);
        
        return grid;
    }
    
    private Player extractPlayerFromForm(GridPane grid, Player existingPlayer) {
        try {
            Player player = existingPlayer != null ? existingPlayer : new Player();
            
            for (javafx.scene.Node node : grid.getChildren()) {
                Object userData = node.getUserData();
                if (userData == null || !(node instanceof TextField)) continue;
                
                String fieldName = userData.toString();
                String value = ((TextField) node).getText();
                
                switch (fieldName) {
                    case "firstName":
                        if (!ValidationUtil.isNotEmpty(value)) {
                            AlertUtil.showError("Validation Error", "First name is required");
                            return null;
                        }
                        player.setFirstName(value);
                        break;
                    case "lastName":
                        if (!ValidationUtil.isNotEmpty(value)) {
                            AlertUtil.showError("Validation Error", "Last name is required");
                            return null;
                        }
                        player.setLastName(value);
                        break;
                    case "email":
                        if (!ValidationUtil.isValidEmail(value)) {
                            AlertUtil.showError("Validation Error", "Please enter a valid email");
                            return null;
                        }
                        player.setEmail(value);
                        break;
                    case "phone":
                        if (!ValidationUtil.isValidPhone(value)) {
                            AlertUtil.showError("Validation Error", "Please enter a valid phone number");
                            return null;
                        }
                        player.setPhone(value);
                        break;
                }
            }
            
            return player;
        } catch (Exception e) {
            AlertUtil.showError("Error", "An error occurred: " + e.getMessage());
            return null;
        }
    }
    
    private void deletePlayer() {
        Player selectedPlayer = playerTable.getSelectionModel().getSelectedItem();
        
        if (selectedPlayer == null) {
            AlertUtil.showWarning("No Selection", "Please select a player to delete");
            return;
        }
        
        boolean confirmed = AlertUtil.showConfirmation("Confirm Delete", 
            "Are you sure you want to delete player: " + selectedPlayer.getFullName() + "?");
        
        if (confirmed) {
            playerDAO.delete(selectedPlayer.getId());
            loadPlayers();
            AlertUtil.showSuccess("Success", "Player deleted successfully!");
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
