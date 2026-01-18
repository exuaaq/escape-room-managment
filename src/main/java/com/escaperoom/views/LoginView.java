package com.escaperoom.views;

import com.escaperoom.dao.UserDAO;
import com.escaperoom.models.User;
import com.escaperoom.models.UserRole;
import com.escaperoom.utils.AlertUtil;
import com.escaperoom.utils.ValidationUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class LoginView {
    
    private Stage primaryStage;
    private UserDAO userDAO;
    
    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.userDAO = new UserDAO();
    }
    
    public Scene createScene() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("login-container");

        Label titleLabel = new Label("Escape Room Management System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.getStyleClass().add("login-title");
        
        Label subtitleLabel = new Label("Please login to continue");
        subtitleLabel.setFont(Font.font("Arial", 14));
        subtitleLabel.getStyleClass().add("login-subtitle");
        

        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        formGrid.getStyleClass().add("login-form");
        
        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);
        
        formGrid.add(usernameLabel, 0, 0);
        formGrid.add(usernameField, 1, 0);
        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);
        

        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(250);
        loginButton.getStyleClass().add("primary-button");
        loginButton.setDefaultButton(true);
        

        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(250);
        registerButton.getStyleClass().add("secondary-button");
        registerButton.setOnAction(e -> showRegistrationDialog());

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            errorLabel.setVisible(false);
            
            if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
                errorLabel.setText("Please enter both username and password");
                errorLabel.setVisible(true);
                return;
            }
            
            User user = userDAO.authenticate(username, password);
            
            if (user != null) {
                showDashboard(user);
            } else {
                errorLabel.setText("Invalid username or password");
                errorLabel.setVisible(true);
                passwordField.clear();
            }
        });
        
        root.getChildren().addAll(titleLabel, subtitleLabel, formGrid, loginButton, registerButton, errorLabel);
        
        Scene scene = new Scene(root, 600, 500);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        
        return scene;
    }
    
    private void showDashboard(User user) {
        DashboardView dashboardView = new DashboardView(primaryStage, user);
        Scene dashboardScene = dashboardView.createScene();
        primaryStage.setScene(dashboardScene);
        //primaryStage.setMaximized(true);
    }
    
    private void showRegistrationDialog() {
        javafx.stage.Stage dialogStage = new javafx.stage.Stage();
        dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        dialogStage.initOwner(primaryStage);
        dialogStage.setTitle("User Registration");
        
        VBox dialogRoot = new VBox(15);
        dialogRoot.setPadding(new Insets(20));
        dialogRoot.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Create New Account");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        

        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);
        
        int row = 0;

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefWidth(250);
        formGrid.add(usernameLabel, 0, row);
        formGrid.add(usernameField, 1, row++);

        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefWidth(250);
        formGrid.add(passwordLabel, 0, row);
        formGrid.add(passwordField, 1, row++);
        

        Label confirmPasswordLabel = new Label("Confirm Password:");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Re-enter password");
        confirmPasswordField.setPrefWidth(250);
        formGrid.add(confirmPasswordLabel, 0, row);
        formGrid.add(confirmPasswordField, 1, row++);
        

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Enter first name");
        firstNameField.setPrefWidth(250);
        formGrid.add(firstNameLabel, 0, row);
        formGrid.add(firstNameField, 1, row++);
        

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Enter last name");
        lastNameField.setPrefWidth(250);
        formGrid.add(lastNameLabel, 0, row);
        formGrid.add(lastNameField, 1, row++);
        

        Label emailLabel = new Label("Email (Optional):");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter email address");
        emailField.setPrefWidth(250);
        formGrid.add(emailLabel, 0, row);
        formGrid.add(emailField, 1, row++);
        

        Label errorLabel = new Label("");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("primary-button");
        registerButton.setPrefWidth(120);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(120);
        cancelButton.setOnAction(e -> dialogStage.close());
        
        buttonBox.getChildren().addAll(registerButton, cancelButton);

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            
            errorLabel.setVisible(false);
            
            // Validation
            if (!ValidationUtil.isNotEmpty(username)) {
                errorLabel.setText("Username is required");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!ValidationUtil.isNotEmpty(password)) {
                errorLabel.setText("Password is required");
                errorLabel.setVisible(true);
                return;
            }
            
            if (password.length() < 6) {
                errorLabel.setText("Password must be at least 6 characters");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                errorLabel.setText("Passwords do not match");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!ValidationUtil.isNotEmpty(firstName)) {
                errorLabel.setText("First name is required");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!ValidationUtil.isNotEmpty(lastName)) {
                errorLabel.setText("Last name is required");
                errorLabel.setVisible(true);
                return;
            }
            
            if (!email.isEmpty() && !ValidationUtil.isValidEmail(email)) {
                errorLabel.setText("Please enter a valid email address");
                errorLabel.setVisible(true);
                return;
            }
            
            // Check if username already exists
            User existingUser = userDAO.findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
            
            if (existingUser != null) {
                errorLabel.setText("Username already exists");
                errorLabel.setVisible(true);
                return;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setEmail(email);
            newUser.setRole(UserRole.STAFF); // Default role for new registrations
            
            try {
                userDAO.save(newUser);
                AlertUtil.showSuccess("Registration Successful", 
                    "Account created successfully! You can now login with your credentials.");
                dialogStage.close();
            } catch (Exception ex) {
                errorLabel.setText("Registration failed: " + ex.getMessage());
                errorLabel.setVisible(true);
            }
        });
        
        dialogRoot.getChildren().addAll(titleLabel, formGrid, errorLabel, buttonBox);
        
        Scene dialogScene = new Scene(dialogRoot, 500, 550);
        dialogScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }
}
