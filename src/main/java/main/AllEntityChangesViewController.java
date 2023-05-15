package main;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileIO;
import util.database.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllEntityChangesViewController {
    Logger logger = LoggerFactory.getLogger(AllEntityChangesViewController.class);

    @FXML
    private BorderPane AllEntityChViewBorderPane;
    @FXML
    private TableView<List<String>> entChangesTableView;
    @FXML
    private TableColumn<List<String>, String> dataTableColumn;
    @FXML
    private TableColumn<List<String>, String> newValueTableColumn;
    @FXML
    private TableColumn<List<String>, String> oldValueTableColumn;
    @FXML
    private TableColumn<List<String>, String> userTableColumn;
    @FXML
    private TableColumn<List<String>, String> timeNDateTableColumn;


    @FXML
    public void initialize() {
        List<List<String>> list = new ArrayList<>();
        try {
            //list = FileIO.loadAllEntChangesFromFile();
            list = Database.loadAllEntityChanges();
        }
        catch (SQLException | IOException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage());
        }

        entChangesTableView.setItems(FXCollections.observableList(list));
        dataTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        oldValueTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(1)));
        newValueTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(2)));
        userTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(3)));
        timeNDateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(4)));
    }


    public void onBackButton() {
        MainController.replaceRootToEntityControl();
    }

    private void showErrorAlert(String message) {
        AllEntityChViewBorderPane.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        alert.setHeaderText("Could not load entity changes from the database");
        alert.setContentText(message);
        if (alert.showAndWait().isPresent()) {
            AllEntityChViewBorderPane.setDisable(false);
        }
    }
}
