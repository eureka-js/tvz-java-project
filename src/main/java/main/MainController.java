package main;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Objects;

public class MainController {
    public static final Logger logger = LoggerFactory.getLogger(MainApplication.class);


    public static void setMainPage(Pane root) {
        MainApplication.mainStage
                .setScene(new Scene(root, MainApplication.mainStage.getWidth(), MainApplication.mainStage.getHeight()));
        MainApplication.mainStage.show();
    }

    public static void toStageLogin() {
        try {
            setMainPage(new FXMLLoader(MainApplication.class.getResource("login-view.fxml")).load());
            LoginController.userIsLoggedIn = false;
        }
        catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    public static void toStageGameScreen() {
        try {
            setMainPage(new FXMLLoader(MainApplication.class.getResource("game-view.fxml")).load());
            LoginController.userIsLoggedIn = false;
        }
        catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void toStageEntityControl() {
        try {
            setMainPage(new FXMLLoader(MainApplication.class.getResource("entity_control-view.fxml")).load());
            LoginController.userIsLoggedIn = false;
        }
        catch(IOException e) {
            logger.error(e.getMessage(), e);
        };
    }

    public static void toStageAllEntityChangesView() {
        try {
            setMainPage(new FXMLLoader(MainApplication.class.getResource("all_entity_changes-view.fxml")).load());
            LoginController.userIsLoggedIn = false;
        }
        catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void replaceRoot(String uri) {
        try {
            MainApplication.scene.setRoot(FXMLLoader.load(Objects.requireNonNull(MainController.class.getResource(uri))));
        }
        catch(IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void replaceRootToLogin() {
        replaceRoot("login-view.fxml");
        LoginController.userIsLoggedIn = false;
    }
    public static void replaceRootToGameScreen() {
        replaceRoot("game-view.fxml");
        LoginController.userIsLoggedIn = true;
    }

    public static void replaceRootToEntityControl() {
        replaceRoot("entity_control-view.fxml");
    }

    public static void replaceRootTAllEntityChangesView() {
        replaceRoot("all_entity_changes-view.fxml");
    }
}