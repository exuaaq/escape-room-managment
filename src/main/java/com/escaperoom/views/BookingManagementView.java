package com.escaperoom.views;

import com.escaperoom.dao.BookingDAO;
import com.escaperoom.dao.PlayerDAO;
import com.escaperoom.dao.RoomDAO;
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


public class BookingManagementView {
    
    private Stage primaryStage;
    private User currentUser;
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;
    private PlayerDAO playerDAO;
    private TableView<Booking> bookingTable;
    private ObservableList<Booking> bookingList;
    private ComboBox<String> statusFilter;
    
    public BookingManagementView(Stage primaryStage, User currentUser) {
        this.primaryStage = primaryStage;
        this.currentUser = currentUser;
        this.bookingDAO = new BookingDAO();
        this.roomDAO = new RoomDAO();
        this.playerDAO = new PlayerDAO();
        this.bookingList = FXCollections.observableArrayList();
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
        
        Label titleLabel = new Label("Booking Management");
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
        bookingTable = createBookingTable();
        
        content.getChildren().addAll(filterBar, actionBar, bookingTable);
        
        loadBookings();
        
        return content;
    }
    
    private HBox createFilterBar() {
        HBox filterBar = new HBox(10);
        filterBar.setPadding(new Insets(10));
        filterBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("Status:");
        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "PENDING", "CONFIRMED", "COMPLETED", "CANCELLED");
        statusFilter.setValue("All");
        statusFilter.setOnAction(e -> filterBookings());
        
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearFilter());
        
        filterBar.getChildren().addAll(statusLabel, statusFilter, clearButton);
        
        return filterBar;
    }
    
    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(10));
        actionBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Button addButton = new Button("New Booking");
        addButton.getStyleClass().add("primary-button");
        addButton.setOnAction(e -> showAddDialog());
        
        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> updateStatus(BookingStatus.CONFIRMED));
        
        Button completeButton = new Button("Complete");
        completeButton.setOnAction(e -> updateStatus(BookingStatus.COMPLETED));
        
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("danger-button");
        cancelButton.setOnAction(e -> updateStatus(BookingStatus.CANCELLED));
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> loadBookings());
        
        actionBar.getChildren().addAll(addButton, confirmButton, completeButton, cancelButton, refreshButton);
        
        return actionBar;
    }
    
    private TableView<Booking> createBookingTable() {
        TableView<Booking> table = new TableView<>();
        table.setItems(bookingList);
        
        TableColumn<Booking, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        
        TableColumn<Booking, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getRoom() != null ? data.getValue().getRoom().getName() : "N/A"));
        roomCol.setPrefWidth(180);
        
        TableColumn<Booking, String> dateTimeCol = new TableColumn<>("Scheduled Time");
        dateTimeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getScheduledTime() != null ? 
            data.getValue().getScheduledTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"));
        dateTimeCol.setPrefWidth(150);
        
        TableColumn<Booking, Integer> playersCol = new TableColumn<>("Players");
        playersCol.setCellValueFactory(new PropertyValueFactory<>("numberOfPlayers"));
        playersCol.setPrefWidth(80);
        
        TableColumn<Booking, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
            data.getValue().getStatus().name()));
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(col -> new TableCell<Booking, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "PENDING":
                            setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            break;
                        case "CONFIRMED":
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            break;
                        case "COMPLETED":
                            setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            break;
                        case "CANCELLED":
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });
        
        TableColumn<Booking, Double> priceCol = new TableColumn<>("Price (KM)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        priceCol.setPrefWidth(100);
        
        TableColumn<Booking, String> notesCol = new TableColumn<>("Notes");
        notesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));
        notesCol.setPrefWidth(200);
        
        table.getColumns().addAll(idCol, roomCol, dateTimeCol, playersCol, statusCol, priceCol, notesCol);
        
        return table;
    }
    
    private void loadBookings() {
        List<Booking> bookings = bookingDAO.findAll();
        bookingList.clear();
        bookingList.addAll(bookings);
    }
    
    private void filterBookings() {
        String status = statusFilter.getValue();
        
        if (status.equals("All")) {
            loadBookings();
            return;
        }
        
        List<Booking> bookings = bookingDAO.findByStatus(BookingStatus.valueOf(status));
        bookingList.clear();
        bookingList.addAll(bookings);
    }
    
    private void clearFilter() {
        statusFilter.setValue("All");
        loadBookings();
    }
    
    private void showAddDialog() {
        Dialog<Booking> dialog = new Dialog<>();
        dialog.setTitle("Create New Booking");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        
        GridPane grid = createBookingForm(null);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return extractBookingFromForm(grid);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(booking -> {
            if (booking != null) {
                bookingDAO.save(booking);
                loadBookings();
                AlertUtil.showSuccess("Success", "Booking created successfully!");
            }
        });
    }

    private GridPane createBookingForm(Booking booking) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setPrefWidth(400);

        int row = 0;

        Label roomLabel = new Label("Room:");
        roomLabel.setMinWidth(120);
        grid.add(roomLabel, 0, row);
        ComboBox<Room> roomCombo = new ComboBox<>();
        roomCombo.getItems().addAll(roomDAO.findActive());
        roomCombo.setUserData("room");
        if (booking != null && booking.getRoom() != null) {
            roomCombo.setValue(booking. getRoom());
        }
        grid.add(roomCombo, 1, row++);


        Label dateTimeLabel = new Label("Date/Time:");
        dateTimeLabel.setMinWidth(120);
        grid.add(dateTimeLabel, 0, row);
        TextField dateTimeField = new TextField();
        dateTimeField.setPromptText("DD/MM/YYYY HH: MM");
        dateTimeField. setUserData("scheduledTime");
        if (booking != null && booking.getScheduledTime() != null) {
            dateTimeField.setText(booking.getScheduledTime().format(DateTimeFormatter. ofPattern("dd/MM/yyyy HH:mm")));
        }
        grid.add(dateTimeField, 1, row++);


        Label playersLabel = new Label("Players:");
        playersLabel.setMinWidth(120);
        grid.add(playersLabel, 0, row);
        Spinner<Integer> playersSpinner = new Spinner<>(1, 20, booking != null ? booking.getNumberOfPlayers() : 4);
        playersSpinner.setUserData("numberOfPlayers");
        grid.add(playersSpinner, 1, row++);

        Label priceLabel = new Label("Price (KM):");
        priceLabel.setMinWidth(120);
        grid.add(priceLabel, 0, row);
        TextField priceField = new TextField(booking != null ? String.valueOf(booking.getTotalPrice()) : "50.00");
        priceField.setUserData("totalPrice");
        grid.add(priceField, 1, row++);

        // Notes
        Label notesLabel = new Label("Notes:");
        notesLabel.setMinWidth(120);
        grid.add(notesLabel, 0, row);
        TextArea notesArea = new TextArea(booking != null ? booking.getNotes() : "");
        notesArea.setPrefRowCount(3);
        notesArea.setUserData("notes");
        grid.add(notesArea, 1, row++);

        return grid;
    }
    
    private Booking extractBookingFromForm(GridPane grid) {
        try {
            Booking booking = new Booking();
            booking.setStatus(BookingStatus.PENDING);
            booking.setBookingDate(LocalDateTime.now());
            
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
                        booking.setRoom(room);
                        break;
                    case "scheduledTime":
                        String dateTimeStr = ((TextField) node).getText();
                        try {
                            LocalDateTime scheduledTime = LocalDateTime.parse(dateTimeStr, 
                                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                            booking.setScheduledTime(scheduledTime);
                        } catch (Exception e) {
                            AlertUtil.showError("Validation Error", "Please enter valid date/time (DD/MM/YYYY HH:MM)");
                            return null;
                        }
                        break;
                    case "numberOfPlayers":
                        booking.setNumberOfPlayers(((Spinner<Integer>) node).getValue());
                        break;
                    case "totalPrice":
                        double price = Double.parseDouble(((TextField) node).getText());
                        booking.setTotalPrice(price);
                        break;
                    case "notes":
                        booking.setNotes(((TextArea) node).getText());
                        break;
                }
            }
            
            return booking;
        } catch (Exception e) {
            AlertUtil.showError("Error", "An error occurred: " + e.getMessage());
            return null;
        }
    }
    
    private void updateStatus(BookingStatus newStatus) {
        Booking selectedBooking = bookingTable.getSelectionModel().getSelectedItem();
        
        if (selectedBooking == null) {
            AlertUtil.showWarning("No Selection", "Please select a booking");
            return;
        }
        
        boolean confirmed = AlertUtil.showConfirmation("Confirm", 
            "Change booking status to " + newStatus + "?");
        
        if (confirmed) {
            bookingDAO.updateStatus(selectedBooking.getId(), newStatus);
            loadBookings();
            AlertUtil.showSuccess("Success", "Booking status updated!");
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
