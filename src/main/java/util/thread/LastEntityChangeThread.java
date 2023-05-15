package util.thread;

import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import main.MainApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileIO;
import util.database.Database;
import util.Enums.Action;

import java.io.EOFException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class LastEntityChangeThread implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(LastEntityChangeThread.class);

    private final Action action;
    private StringBuilder entityChangeStr;
    private static boolean isInFailState;


    public LastEntityChangeThread(Action action) { this.action = action; }
    public LastEntityChangeThread(Action action, StringBuilder stringBuilder) {
        this.action = action;
        this.entityChangeStr = stringBuilder;
    }

    @Override
    public synchronized void run(){
        if (!isInFailState) {
            doAction();
        }
    }

    private synchronized void doAction() {
        try {
            if (action == Action.SHOW) {
                MainApplication.mainStage
                        .setTitle("Runevade\tLast entity change ( " + FileIO.loadLastEntityChange() + " )");

                MainApplication.mainStage
                        .setTitle("Runevade\tLast entity change ( " + Database.loadLastEntityChange() + " )");
            }
            else {
                Database.insertEntityChange(entityChangeStr);
            }
        }
        catch (SQLException | ParseException | IOException e) {
            isInFailState = true;

            logger.error(e.getMessage(), e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initStyle(StageStyle.UNDECORATED);
            alert.setGraphic(null);
            alert.setHeaderText("Error while loading last entity change");
            alert.setContentText(e.getMessage());
            if (alert.showAndWait().isPresent()) {
                System.exit(1);
            }
        }
    }
}
