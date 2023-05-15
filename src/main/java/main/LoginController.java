package main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Enums.UserRole;
import util.FileIO;
import util.database.Database;

import java.io.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    public static boolean userIsLoggedIn = false;
    public static UserRole userRole;
    public static boolean isFreshlyRegistered;
    private static String userName;


    @FXML
    private StackPane mainStackPane;
    @FXML
    private ChoiceBox<String> modeSetChoiceBox;
    @FXML
    private VBox loginVBox;
    @FXML
    private TextField userNameTextField;
    @FXML
    private PasswordField userPassTextField;
    @FXML
    private Label alertMessageLabel;
    @FXML
    private VBox registerVBox;
    @FXML
    private TextField regUserNameTextField;
    @FXML
    private PasswordField regUserPasswordField;
    @FXML
    private PasswordField regUserPasswordField1;
    @FXML
    private Label regAlertMessageLabel;
    @FXML
    private BorderPane exitBorderPane;
    @FXML
    private VBox deleteVbox;
    @FXML
    private TextField usernameDelTextField;
    @FXML
    private PasswordField pass1DelPassField;
    @FXML
    private PasswordField pass2DelPassField;
    @FXML
    private Label delMessageAlertLabel;


    public static String getUserName() { return userName; }


    public void initialize() {
        modeSetChoiceBox.getItems().addAll("Login", "Register", "Delete account");
        modeSetChoiceBox.getSelectionModel().selectFirst();
        modeSetChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch(newValue) {
                case "Login" -> loginVBox.toFront();
                case "Register" -> registerVBox.toFront();
                case "Delete account" -> deleteVbox.toFront();
            }
        });
    }

    public void logInOnEnterPress(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) {
            onLoginButtonPressed();
        }
    }

    public void onLoginButtonPressed() {
        if (userNameTextField.getText().equals("") && userPassTextField.getText().equals("")) {
            alertMessageLabel.setText("Please enter the username and the password");
        }
        else if (userNameTextField.getText().equals("")) {
            alertMessageLabel.setText("Please enter the username");
        }
        else if (userPassTextField.getText().equals("")) {
            alertMessageLabel.setText("Please enter the password");
        }
        else {
            try {
                String candUsername = userNameTextField.getText();

                Boolean[] userData = FileIO.getUser(candUsername, hashPassword(userPassTextField.getText()));
                if (userData != null) {
                    userName = candUsername;
                    userRole = userData[0] ? UserRole.ADMIN : UserRole.NON_ADMIN ;
                    isFreshlyRegistered = userData[1];

                    //MainController.replaceRootToGameScreen();
                    //return;
                }

                if (Database.userAccessGranted(candUsername, hashPassword(userPassTextField.getText()))) {
                    userName = candUsername;
                    if (Database.userIsAdmin(userName)) {
                        userRole = UserRole.ADMIN;
                    }
                    else {
                        userRole = UserRole.NON_ADMIN;
                    }
                    isFreshlyRegistered = Database.isUserFreshlyRegistered(null);

                    MainController.replaceRootToGameScreen();
                    return;
                }

                alertMessageLabel.setText("Incorrect username and/or password");
            }
            catch(IOException | SQLException e) {
                logger.error(e.getMessage(), e);
                showErrorAlert(e.getMessage());
            }
        }
    }

    public void onRegisterButtonPressed() {
        mainStackPane.setOnKeyPressed(this::regEscapePress);
        registerVBox.toFront();
        userRegister();
    }

    public void userRegister() {
        if (regUserNameTextField.getText().equals("") && (regUserPasswordField.getText().equals("") ||
                regUserPasswordField1.getText().equals(""))) {
            regAlertMessageLabel.setText("Please enter the username and the password");
        }
        else if (regUserNameTextField.getText().equals("")) {
            regAlertMessageLabel.setText("Please enter the username");
        }
        else if (regUserPasswordField.getText().equals("")) {
            regAlertMessageLabel.setText("Please enter the password");
        }
        else if (regUserPasswordField1.getText().equals("")) {
            regAlertMessageLabel.setText("Please repeat the password");
        }
        else if (!regUserPasswordField.getText().equals(regUserPasswordField1.getText())) {
            regAlertMessageLabel.setText("Two passwords must match");
        }
        else {
            String candUsername = regUserNameTextField.getText();

            try {
                if (FileIO.usernameIsTaken(candUsername)) {
                    regAlertMessageLabel.setText("Username is already taken.\nPlease use another");
                    //return;
                }

                if (Database.usernameIsTaken(candUsername)) {
                    regAlertMessageLabel.setText("Username is already taken.\nPlease use another");
                    return;
                }

                FileIO.registerUser(candUsername, hashPassword(regUserPasswordField.getText()), UserRole.NON_ADMIN);
                Database.registerUser(candUsername, hashPassword(regUserPasswordField.getText()), UserRole.NON_ADMIN);

                regAlertMessageLabel.setText("User successfully registered");
            }
            catch (IOException | SQLException e) {
                logger.error(e.getMessage(), e);
                showErrorAlert(e.getMessage());
            }
        }
    }


    public void onDelButtonPress() {
        if (usernameDelTextField.getText().equals("") && (pass1DelPassField.getText().equals("") ||
                pass2DelPassField.getText().equals(""))) {
            delMessageAlertLabel.setText("Please enter the username and the password");
        }
        else if (usernameDelTextField.getText().equals("")) {
            delMessageAlertLabel.setText("Please enter the username");
        }
        else if (pass1DelPassField.getText().equals("")) {
            delMessageAlertLabel.setText("Please enter the password");
        }
        else if (pass2DelPassField.getText().equals("")) {
            delMessageAlertLabel.setText("Please repeat the password");
        }
        else if (!pass1DelPassField.getText().equals(pass2DelPassField.getText())) {
            delMessageAlertLabel.setText("Two passwords must match");
        }
        else {
            String candUsername = usernameDelTextField.getText();
            try {
                if (FileIO.userAccessGranted(candUsername, hashPassword(pass1DelPassField.getText())) &&
                        pass1DelPassField.getText().equals(pass2DelPassField.getText()) &&
                        isDataChangeAccessGranted()) {
                    FileIO.deleteUser(candUsername);
                    delMessageAlertLabel.setText("User successfully deleted");
                }

                if (Database.userAccessGranted(candUsername, hashPassword(pass1DelPassField.getText())) &&
                        pass1DelPassField.getText().equals(pass2DelPassField.getText()) &&
                        isDataChangeAccessGranted()) {
                    Database.deleteUser(candUsername);
                    delMessageAlertLabel.setText("User successfully deleted");
                }
            }
            catch (IOException | SQLException e) {
                logger.error(e.getMessage(), e);
                showErrorAlert(e.getMessage());
            }
        }
    }

    public void registerOnEnterPress(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER)) userRegister();

    }

    public void escapePress(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ESCAPE)) {
            exitBorderPane.toFront();
        }
    }
    public void regEscapePress(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ESCAPE)) {
            mainStackPane.setOnKeyPressed(this::escapePress);
            loginVBox.toFront();
        }
    }

    public void onExitButtonPressed() {
        mainStackPane.setOnKeyPressed(key -> {});
        exitBorderPane.toFront();
    }

    public static String hashPassword(String pass) {
        StringBuilder tmpStrBlder = new StringBuilder();
        char tmpChar;
        int[] creativityOverload = new int[] {4, 2, 0, 6, 9};
        for(int i = 0, k = 0; i < pass.length(); ++i, k = (k + 1) % creativityOverload.length) {
            tmpChar = (char)((pass.charAt(i) * creativityOverload[k]) % 1112064);
            tmpStrBlder.append(tmpChar);
        }

        return tmpStrBlder.toString();
    }

    public void onYesButtonPressed() {
        System.exit(1);
    }

    public void onNoButtonPressed() { exitBorderPane.toBack(); };

    public boolean isDataChangeAccessGranted() {
        mainStackPane.setDisable(true);
        Alert confirmWind = new Alert(Alert.AlertType.CONFIRMATION);
        confirmWind.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        confirmWind.initOwner(MainApplication.mainStage);
        confirmWind.initStyle(StageStyle.UNDECORATED);
        confirmWind.setGraphic(null);
        confirmWind.setHeaderText("Are you sure You want to change the data?");
        Optional<ButtonType> result = confirmWind.showAndWait();
        if (result.isPresent()) {
            mainStackPane.setDisable(false);
        }
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showErrorAlert(String message) {
        mainStackPane.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        alert.setHeaderText("Failure with the database");
        alert.setContentText(message);
        if (alert.showAndWait().isPresent()) {
            mainStackPane.setDisable(false);
        }
        Platform.runLater(MainController::replaceRootToLogin);
    }

    public static boolean userIsAdmin() {
        return userRole == UserRole.ADMIN;
    }
}
