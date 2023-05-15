package main;

import exceptions.unchecked.UserDoesntExistException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Enums.Action;
import util.Enums.UserRole;
import util.FileIO;
import util.database.Database;
import util.database.LoadNamesToDatabase;
import util.thread.LastEntityChangeThread;


public class MainApplication extends Application {
    Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static Stage mainStage;
    public static Scene scene;


    @Override
    public void start(Stage stage) throws IOException {
        // This line of code is meant to be executed only once if the database is empty and there are no entity names.
        // If that is the case, uncomment the line below, run and exit the application, and then comment the line again.
        // LoadNamesToDatabase.updateFromFileToDB();

        // This line of code creates an admin
        /* try {
            Database.registerUser("admin", LoginController.hashPassword("admin1"), UserRole.ADMIN);
            FileIO.registerUser("admin", LoginController.hashPassword("admin1"), UserRole.ADMIN);
        }
        catch (SQLException | IOException e) {

        } */

        stage.setOnCloseRequest(event -> {
            if (LoginController.userIsLoggedIn) {
                try {
                    Path path = Paths.get("IO_files/user_entities/" +
                            LoginController.getUserName() + "_entities.dat");
                    FileIO.saveObjectsToFile(path, GameScreenController.entitiesInCellsList,
                            GameScreenController.escapeHole, GameScreenController.depthLevelString.get(),
                            GameScreenController.enemies, GameScreenController.player, GameScreenController.trader,
                            GameScreenController.chest);

                    Database.executeFlushAndSave(Database.player, Database.enemyList, Database.trader, Database.chest,
                            Database.escapeHole, Database.depthString);
                }
                catch (SQLException | IOException | UserDoesntExistException e) {
                    logger.error(e.getMessage(), e);
                    showErrorAlert(e.getMessage(), "Database error");
                }

                if (GameScreenController.gameEngineThread != null) {
                    try {
                        GameScreenController.gameEngineThread.toBeShutDown = true;
                        GameScreenController.executorService.shutdownNow();
                        GameScreenController.executorService.awaitTermination(1, TimeUnit.NANOSECONDS);
                    }
                    catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                        showErrorAlert(e.getMessage(), "GameEngine thread shutdown unsuccessful");
                    }
                }
            }
        });
        mainStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        stage.setTitle("Runevade");
        stage.setScene(scene);
        stage.show();

        Timeline showLastEntityChange =
                new Timeline(new KeyFrame(Duration.seconds(1),
                        event -> Platform.runLater(new LastEntityChangeThread(Action.SHOW))));
        showLastEntityChange.setCycleCount(Timeline.INDEFINITE);
        showLastEntityChange.play();
    }

    public static void main(String[] args)
    {
        launch();
    }

    private void showErrorAlert(final String message, final String headerText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
    }
}