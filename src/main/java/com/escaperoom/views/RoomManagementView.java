package com.escaperoom.views;

import com.escaperoom.dao.RoomDAO;
import com.escaperoom.models.Room;
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


public class RoomManagementView {
    
    private Stage primaryStage;
    private User currentUser;
    private RoomDAO roomDAO;
    private TableView<Room> roomTable;
    private ObservableList<Room> roomList;
    private TextField searchField;
    private ComboBox<String> themeFilter;
    
    public RoomManagementView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.roomDAO = new RoomDAO();
        this.roomList = FXCollections.observableArrayList();
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
        
        Label titleLabel = new Label("Room Management");
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

        roomTable = createRoomTable();
        
        content.getChildren().addAll(filterBar, actionBar, roomTable);
        

        loadRooms();
        
        return content;
    }
    
    private HBox createFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setPadding(new Insets(10));
        filterBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Search:");
        searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterRooms());
        
        Label themeLabel = new Label("Theme:");
        themeFilter = new ComboBox<>();
        themeFilter.getItems().addAll("All", "Horror", "Mystery", "Sci-Fi", "Adventure");
        themeFilter.setValue("All");
        themeFilter.setOnAction(e -> filterRooms());
        
        Button clearButton = new Button("Clear Filters");
        clearButton.setOnAction(e -> clearFilters());
        
        filterBar.getChildren().addAll(searchLabel, searchField, themeLabel, themeFilter, clearButton);
        
        return filterBar;
    }
    
    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(10));
        actionBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button addButton = new Button("Add Room");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddDialog());
        
        Button editButton = new Button("Edit Room");
        editButton.setOnAction(e -> showEditDialog());
        
        Button deleteButton = new Button("Delete Room");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setOnAction(e -> deleteRoom());
        
        Button toggleActiveButton = new Button("Toggle Active");
        toggleActiveButton.setOnAction(e -> toggleActive());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadRooms());
        
        actionBar.getChildren().addAll(addButton, editButton, deleteButton, toggleActiveButton, refreshButton);
        
        return actionBar;
    }
    
    private TableView<Room> createRoomTable() {
        TableView<Room> table = new TableView<>();
        table.setItems(roomList);
        
        TableColumn<Room, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Room, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);
        
        TableColumn<Room, String> themeCol = new TableColumn<>("Theme");
        themeCol.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeCol.setPrefWidth(100);
        
        TableColumn<Room, Integer> difficultyCol = new TableColumn<>("Difficulty");
        difficultyCol.setCellValueFactory(new PropertyValueFactory<>("difficulty"));
        difficultyCol.setPrefWidth(80);
        
        TableColumn<Room, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        capacityCol.setPrefWidth(80);
        
        TableColumn<Room, Double> priceCol = new TableColumn<>("Price (KM)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(100);
        
        TableColumn<Room, Integer> durationCol = new TableColumn<>("Duration (min)");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        durationCol.setPrefWidth(120);
        
        TableColumn<Room, Double> ratingCol = new TableColumn<>("Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("averageRating"));
        ratingCol.setPrefWidth(80);
        
        TableColumn<Room, Boolean> activeCol = new TableColumn<>("Active");
        activeCol.setCellValueFactory(new PropertyValueFactory<>("active"));
        activeCol.setPrefWidth(80);
        activeCol.setCellFactory(col -> new TableCell<Room, Boolean>() {
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
        
        table.getColumns().addAll(idCol, nameCol, themeCol, difficultyCol, capacityCol, 
                                   priceCol, durationCol, ratingCol, activeCol);
        
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                showEditDialog();
            }
        });
        
        return table;
    }
    
    private void loadRooms() {
        List<Room> rooms = roomDAO.findAll();
        roomList.clear();
        roomList.addAll(rooms);
    }
    
    private void filterRooms() {
        String searchText = searchField.getText().toLowerCase();
        String theme = themeFilter.getValue();
        
        List<Room> allRooms = roomDAO.findAll();
        roomList.clear();
        
        for (Room room : allRooms) {
            boolean matchesSearch = searchText.isEmpty() || 
                                   room.getName().toLowerCase().contains(searchText);
            boolean matchesTheme = theme.equals("All") || room.getTheme().equals(theme);
            
            if (matchesSearch && matchesTheme) {
                roomList.add(room);
            }
        }
    }
    
    private void clearFilters() {
        searchField.clear();
        themeFilter.setValue("All");
        loadRooms();
    }
    
    private void showAddDialog() {
        Dialog<Room> dialog = new Dialog<>();
        dialog.setTitle("Add New Room");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        
        GridPane grid = createRoomForm(null);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return extractRoomFromForm(grid, null);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(room -> {
            if (room != null) {
                roomDAO.save(room);
                loadRooms();
                AlertUtil.showSuccess("Success", "Room added successfully!");
            }
        });
    }
    
    private void showEditDialog() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        
        if (selectedRoom == null) {
            AlertUtil.showWarning("No Selection", "Please select a room to edit");
            return;
        }
        
        Dialog<Room> dialog = new Dialog<>();
        dialog.setTitle("Edit Room");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        
        GridPane grid = createRoomForm(selectedRoom);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return extractRoomFromForm(grid, selectedRoom);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(room -> {
            if (room != null) {
                roomDAO.update(room);
                loadRooms();
                AlertUtil.showSuccess("Success", "Room updated successfully!");
            }
        });
    }
    
    private GridPane createRoomForm(Room room) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        int row = 0;
        
        grid.add(new Label("Name:"), 0, row);
        TextField nameField = new TextField(room != null ? room.getName() : "");
        nameField.setUserData("name");
        grid.add(nameField, 1, row++);
        
        grid.add(new Label("Theme:"), 0, row);
        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("Horror", "Mystery", "Sci-Fi", "Adventure");
        themeCombo.setValue(room != null ? room.getTheme() : "Horror");
        themeCombo.setUserData("theme");
        grid.add(themeCombo, 1, row++);
        
        grid.add(new Label("Difficulty (1-5):"), 0, row);
        Spinner<Integer> difficultySpinner = new Spinner<>(1, 5, room != null ? room.getDifficulty() : 3);
        difficultySpinner.setUserData("difficulty");
        grid.add(difficultySpinner, 1, row++);
        
        grid.add(new Label("Capacity:"), 0, row);
        Spinner<Integer> capacitySpinner = new Spinner<>(1, 20, room != null ? room.getCapacity() : 6);
        capacitySpinner.setUserData("capacity");
        grid.add(capacitySpinner, 1, row++);
        

        grid.add(new Label("Price (KM):"), 0, row);
        TextField priceField = new TextField(room != null ? String.valueOf(room.getPrice()) : "50.00");
        priceField.setUserData("price");
        grid.add(priceField, 1, row++);
        

        grid.add(new Label("Duration (min):"), 0, row);
        Spinner<Integer> durationSpinner = new Spinner<>(30, 180, room != null ? room.getDuration() : 60, 15);
        durationSpinner.setUserData("duration");
        grid.add(durationSpinner, 1, row++);
        

        grid.add(new Label("Description:"), 0, row);
        TextArea descArea = new TextArea(room != null ? room.getDescription() : "");
        descArea.setPrefRowCount(3);
        descArea.setUserData("description");
        grid.add(descArea, 1, row++);
        

        grid.add(new Label("Active:"), 0, row);
        CheckBox activeCheck = new CheckBox();
        activeCheck.setSelected(room == null || room.isActive());
        activeCheck.setUserData("active");
        grid.add(activeCheck, 1, row++);
        
        return grid;
    }
    
    private Room extractRoomFromForm(GridPane grid, Room existingRoom) {
        try {
            Room room = existingRoom != null ? existingRoom : new Room();
            
            for (javafx.scene.Node node : grid.getChildren()) {
                Object userData = node.getUserData();
                if (userData == null) continue;
                
                String fieldName = userData.toString();
                
                switch (fieldName) {
                    case "name":
                        String name = ((TextField) node).getText();
                        if (!ValidationUtil.isNotEmpty(name)) {
                            AlertUtil.showError("Validation Error", "Name is required");
                            return null;
                        }
                        room.setName(name);
                        break;
                    case "theme":
                        room.setTheme(((ComboBox<String>) node).getValue());
                        break;
                    case "difficulty":
                        room.setDifficulty(((Spinner<Integer>) node).getValue());
                        break;
                    case "capacity":
                        room.setCapacity(((Spinner<Integer>) node).getValue());
                        break;
                    case "price":
                        double price = Double.parseDouble(((TextField) node).getText());
                        if (!ValidationUtil.isPositive(price)) {
                            AlertUtil.showError("Validation Error", "Price must be positive");
                            return null;
                        }
                        room.setPrice(price);
                        break;
                    case "duration":
                        room.setDuration(((Spinner<Integer>) node).getValue());
                        break;
                    case "description":
                        room.setDescription(((TextArea) node).getText());
                        break;
                    case "active":
                        room.setActive(((CheckBox) node).isSelected());
                        break;
                }
            }
            
            return room;
        } catch (NumberFormatException e) {
            AlertUtil.showError("Validation Error", "Please enter valid numbers");
            return null;
        }
    }
    
    private void deleteRoom() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        
        if (selectedRoom == null) {
            AlertUtil.showWarning("No Selection", "Please select a room to delete");
            return;
        }
        
        boolean confirmed = AlertUtil.showConfirmation("Confirm Delete", 
            "Are you sure you want to delete room: " + selectedRoom.getName() + "?");
        
        if (confirmed) {
            roomDAO.delete(selectedRoom.getId());
            loadRooms();
            AlertUtil.showSuccess("Success", "Room deleted successfully!");
        }
    }
    
    private void toggleActive() {
        Room selectedRoom = roomTable.getSelectionModel().getSelectedItem();
        
        if (selectedRoom == null) {
            AlertUtil.showWarning("No Selection", "Please select a room");
            return;
        }
        
        selectedRoom.setActive(!selectedRoom.isActive());
        roomDAO.update(selectedRoom);
        loadRooms();
        
        String status = selectedRoom.isActive() ? "activated" : "deactivated";
        AlertUtil.showSuccess("Success", "Room " + status + " successfully!");
    }
    
    private void backToDashboard() {
        boolean wasFullScreen = primaryStage.isFullScreen();
        DashboardView dashboardView = new DashboardView(primaryStage, currentUser);
        Scene scene = dashboardView.createScene();
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(wasFullScreen);
    }
}
