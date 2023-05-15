package main;

import entities.beings.*;
import entities.items.*;
import entities.other.Chest;
import entities.other.Names;
import exceptions.checked.OutOfGameGridException;
import exceptions.unchecked.NegativeStatInputException;
import exceptions.unchecked.UserDoesntExistException;
import exceptions.unchecked.WrongCoordInputFormatException;
import exceptions.unchecked.ZeroValueInputException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CustomSimpleStringProperty;
import util.FileIO;
import util.database.Database;
import util.thread.LastEntityChangeThread;
import util.Enums.LABEL_UPDATE_MODE;
import util.Enums.SEX;
import util.Enums.MatrixPos;
import util.Enums.UserRole;
import util.Enums.Action;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static main.GameScreenController.chest;

public class EntityControlController {
    Logger logger = LoggerFactory.getLogger(EntityControlController.class);
    Random randNum = new Random();
    

    private Player player = GameScreenController.player;
    private List<Enemy> enemyList = GameScreenController.enemies;
    private List<Item> itemList;
    private List<List<List<Object>>> lclEntitiesInCellsList = GameScreenController.entitiesInCellsList;


    @FXML
    private StackPane baseStackPane;
    @FXML
    private ToggleButton playerToggleButton;
    @FXML
    private ToggleButton enemiesToggleButton;
    @FXML
    private ToggleButton itemsToggleButton;
    @FXML
    private GridPane playerControlGridPane;
    @FXML
    private TextField playerNameTextField;
    @FXML
    private TextField playerLevelTextField;
    @FXML
    private TextField playerBaseHealthTextField;
    @FXML
    private TextField playerHealthTextField;
    @FXML
    private TextField playerStaminaTextField;
    @FXML
    private TextField playerWeightTextField;
    @FXML
    private TextField playerArmourTextField;
    @FXML
    private TextField playerAttSpeedTextField;
    @FXML
    private TextField playerBaseAttDmgTextField;
    @FXML
    private TextField playerMedAttDmgTextField;
    @FXML
    private TextField playerAccuracyTextField;
    @FXML
    private TextField playerInitiativeTextField;
    @FXML
    private TextField playerSexTextField;
    @FXML
    private TextField playerTypeTextField;
    @FXML
    private TextField playerSymoblTextField;
    @FXML
    private TextField playerCoordTextField;
    @FXML
    private TextField playerKillCountTextField;
    @FXML
    private TableView<Item> playerInvItemsTableView;
    @FXML
    private TableColumn<Item, String> playerInvItemNameTableColumn;
    @FXML
    private TableColumn<Item, String> playerInvItemTypeTableColumn;
    @FXML
    private TableColumn<Item, String> playerInvItemDefAttTableColumn;
    @FXML
    private TableColumn<Item, String> playerInvItemWeightTableColumn;
    @FXML
    private TableColumn<Item, String> playerInvItemLevelTableColumn;
    @FXML
    private TableColumn<Item, Item> playerInvEquipButtonTableColumn;
    @FXML
    private GridPane enemiesControlGridPane;
    @FXML
    private HBox enemyLabelHBox;
    @FXML
    private TextField enemyNameTextField;
    @FXML
    private TextField enemyLevelTextField;
    @FXML
    private TextField enemyBaseHealthTextField;
    @FXML
    private TextField enemyHealthTextField;
    @FXML
    private TextField enemyStaminaTextField;
    @FXML
    private TextField enemyWeightTextField;
    @FXML
    private TextField enemyArmourTextField;
    @FXML
    private TextField enemyAttSpeedTextField;
    @FXML
    private TextField enemyBaseAttDmgTextField;
    @FXML
    private TextField enemyMedAttDmgTextField;
    @FXML
    private TextField enemyAccuracyTextField;
    @FXML
    private TextField enemyInitiativeTextField;
    @FXML
    private TextField enemySexTextField;
    @FXML
    private TextField enemyTypeTextField;
    @FXML
    private TextField enemySymoblTextField;
    @FXML
    private TextField enemyCoordTextField;
    @FXML
    private TextField enemyFilterByAllTextField;
    @FXML
    private TextField enemyFilterByNameTextField;
    @FXML
    private TextField enemyFilterByTypeTextField;
    @FXML
    private TextField enemyFilterBySexTextField;
    @FXML
    private TextField enemyFilterByLevelTextField;
    @FXML
    private TextField enemySearchNameTextField;
    @FXML
    private TextField enemySearchTypeTextField;
    @FXML
    private TextField enemySearchSexTextField;
    @FXML
    private TextField enemySearchLevelTextField;
    @FXML
    private Button enemyListRefreshButton;
    @FXML
    private TableView<Enemy> enemyListTableView;
    @FXML
    private TableColumn<Enemy, String> enemyNameTableColumn;
    @FXML
    private TableColumn<Enemy, String> enemyTypeTableColumn;
    @FXML
    private TableColumn<Enemy, String> enemySexTableColumn;
    @FXML
    private TableColumn<Enemy, String> enemyLevelTableColumn;
    @FXML
    private TableColumn<Enemy, Enemy> enemyDeleteTableColumn;
    @FXML
    private TableView<Item> enemyInvTableView;
    @FXML
    private TableColumn<Item, String> enemyInvItemNameTableColumn;
    @FXML
    private TableColumn<Item, String> enemyInvItemDefAttTableColumn;
    @FXML
    private TableColumn<Item, String> enemyInvItemWeightTableColumn;
    @FXML
    private TableColumn<Item, String> enemyInvItemLevelTableColumn;
    @FXML
    private TableColumn<Item, Item> enemyInvEquipButtonTableColumn;
    @FXML
    private TableColumn<Item, String> enemyInvItemTypeTableColumn;
    @FXML
    private TableColumn<Item, String> enemyInvItemSymbolTableColumn;
    @FXML
    private TableColumn<Item, String> enemyInvCoordTableColumn;
    @FXML
    private Button createHumanButton;
    @FXML
    private Button createHoundButton;
    @FXML
    private Button createRatButton;
    @FXML
    private BorderPane itemsControlBorderPane;
    @FXML
    private Button itemListRefreshButton;
    @FXML
    private TableView<Item> itemListTableView;
    @FXML
    private TableColumn<Item, Item> itemListItemNameTableColumn;
    @FXML
    private TableColumn<Item, Item>itemListItemDefAttTableColumn;
    @FXML
    private TableColumn<Item, Item> itemListItemWeightTableColumn;
    @FXML
    private TableColumn<Item, Item> itemListItemLevelTableColumn;
    @FXML
    private TableColumn<Item, String> itemListItemTypeTableColumn;
    @FXML
    private TableColumn<Item, Item> itemListItemSymbolTableColumn;
    @FXML
    private TableColumn<Item, Item> itemListCoordTableColumn;
    @FXML
    private TableColumn<Item, Item> itemListOwnerNameTableColumn;
    @FXML
    private TableColumn<Item, Item> itemListDeleteButtonTableColumn;
    @FXML
    private TextField itemFilterByAllTextField;
    @FXML
    private TextField itemFilterByOwnerTextField;
    @FXML
    private TextField itemFilterByNameTextField;
    @FXML
    private TextField itemFilterByTypeTextField;
    @FXML
    private TextField itemFilterByDefDmgTextField;
    @FXML
    private TextField itemFilterByWeightTextField;
    @FXML
    private TextField itemFilterBySymbolTextField;
    @FXML
    private TextField itemFilterByCoordTextField;
    @FXML
    private TextField itemFilterByLevelTextField;
    @FXML
    private TextField itemSearchOwnerTextField;
    @FXML
    private TextField itemSearchNameTextField;
    @FXML
    private TextField itemSearchTypeTextField;
    @FXML
    private TextField itemSearchDefDmgTextField;
    @FXML
    private TextField itemSearchWeightTextField;
    @FXML
    private TextField itemSearchSymbolTextField;
    @FXML
    private TextField itemSearchCoordTextField;
    @FXML
    private TextField itemSearchLevelTextField;
    @FXML
    private Button createHeadwareButton;
    @FXML
    private Button createTrunkwareButton;
    @FXML
    private Button createArmwareButton;
    @FXML
    private Button createLegwareButton;
    @FXML
    private Button createShieldButton;
    @FXML
    private Button createWeaponButton;
    @FXML
    private BorderPane usersControlBorderPane;
    @FXML
    private ToggleButton usersToggleButton;
    @FXML
    private Button EntChangesViewButton;
    @FXML
    private TableView<List<String>> userListTableView;
    @FXML
    private TableColumn<List<String>, String> usernameTableColumn;
    @FXML
    private TableColumn<List<String>, List<String>> userRoleTableColumn;
    @FXML
    private TableColumn<List<String>, List<String>> userDeleteTableColumn;


    @FXML
    public void initialize() {
        if (!LoginController.userIsAdmin()) {
            usersToggleButton.setDisable(true);
            EntChangesViewButton.setDisable(true);
        }

        itemList = new ArrayList<>();

        onPlayerButtonPress();
        updateItemList();
    }

    public void updateEnemyTableView() {
        enemyListTableView.setItems(FXCollections.observableList(enemyList));
        enemyNameTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        enemyTypeTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        enemySexTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSex().toString()));
        enemyLevelTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getLevel())));
        enemyDeleteTableColumn
                .setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        enemyDeleteTableColumn.setCellFactory(cellData -> new TableCell<>() {
            private final Button deleteButton = new Button();

            @Override
            public void updateItem(Enemy enemy, boolean empty) {
                super.updateItem(enemy, empty);
                if (enemy == null) {
                    setGraphic(null);
                    return;
                }

                deleteButton.setMinWidth(80);
                deleteButton.setText("Delete");
                setGraphic(deleteButton);

                deleteButton.setOnAction(event -> {
                    if (!dataChangeAccessIsGranted()) {
                        return;
                    }

                    StringBuilder entChangeStrForFile = new StringBuilder("Deleted: enemy -> "
                            + enemy.getName() + " ! " + " " + " ! " + " " + " ! " + LoginController.getUserName() + " ! "
                            + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    
                    enemy.getInventoryList().forEach(item -> {
                        itemList.remove(item);
                    });
                    enemyList.remove(enemy);
                    lclEntitiesInCellsList.get(enemy.getX()).get(enemy.getY()).remove(enemy);
                    Database.bufferDelEnemyId(enemy);
                    enemyListTableView.refresh();
                    enemyInvTableView.getItems().clear();
                    updateEnemyLabels(enemy, LABEL_UPDATE_MODE.EMPTY);
                    
                    loadEnemyChangeToFile(entChangeStrForFile);
                    loadEnemyChangeToDB(entChangeStrForFile);
                });
            }
        });
    }

    public void updateEnemyLabels(Enemy enemy, LABEL_UPDATE_MODE doPopulate) {
        // I do apologize for this method with 16 ternary operators. It can have one if statement, but the code would
        // be double in size in rows for something that gets executed relatively rarely on user's input
        // I would, personally, prefer a one if statement over 16 ternary operators

        enemyNameTextField.setText(doPopulate.state ? enemy.getName() : "");
        enemyLevelTextField.setText(doPopulate.state ? Integer.toString(enemy.getLevel()) : "");
        enemyBaseHealthTextField.setText(doPopulate.state ? Integer.toString(enemy.getBaseHealth()) : "");
        enemyHealthTextField.setText(doPopulate.state ? Integer.toString(enemy.getHealth()) : "");
        enemyStaminaTextField.setText(doPopulate.state ? Integer.toString(enemy.getStamina()) : "");
        enemyWeightTextField.setText(doPopulate.state ? Integer.toString(enemy.getWeight()) : "");
        enemyArmourTextField.setText(doPopulate.state ?  Integer.toString(enemy.getArmour()) : "");
        enemyAttSpeedTextField.setText(doPopulate.state ? Integer.toString(enemy.getAttackSpeed()) : "");
        enemyBaseAttDmgTextField.setText(doPopulate.state ? Integer.toString(enemy.getBaseAttDmg()) : "");
        enemyMedAttDmgTextField.setText(doPopulate.state ? Integer.toString(enemy.getMedianAttDmg()) : "");
        enemyAccuracyTextField.setText(doPopulate.state ? String.format(Locale.ENGLISH, "%.4f" , enemy.getAccuracy()) : "");
        //enemyAccuracyTextField.setText(doPopulate.state ? new DecimalFormat("0.0000").format(enemy.getAccuracy()) : "");
        enemyInitiativeTextField.setText(doPopulate.state ? Integer.toString(enemy.getInitiative()) : "");
        enemySexTextField.setText(doPopulate.state ? enemy.getSex().toString() : "");
        enemyTypeTextField.setText(doPopulate.state ? enemy.getType() : "");
        enemySymoblTextField.setText(doPopulate.state ? enemy.getSymbol() : "");
        enemyCoordTextField.setText(doPopulate.state ? (enemy.getX() + ", " + enemy.getY()) : "");
    }

    public void updateEnemyInvItems(Enemy enemy) {
        enemyInvTableView.setItems(FXCollections.observableList(enemy.getInventoryList()));
        enemyInvItemNameTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        enemyInvItemDefAttTableColumn
                .setCellValueFactory(cellData -> CustomSimpleStringProperty.simpleStrPropertyDefAtt(cellData.getValue()));
        enemyInvItemWeightTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getWeight())));
        enemyInvItemLevelTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getWeight())));
        enemyInvItemTypeTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        enemyInvItemSymbolTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSymbol()));
        enemyInvCoordTableColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty("(" + cellData.getValue().getX() + ", " + cellData.getValue().getY() + ")"));

        enemyInvEquipButtonTableColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        enemyInvEquipButtonTableColumn.setCellFactory(cellData -> new TableCell<>() {
            private final Button equipUnequipButton = new Button();

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                if(item.isEquipped()) {
                    equipUnequipButton.setText("Unequip");
                }
                else {
                    equipUnequipButton.setText("Equip");
                }

                equipUnequipButton.setMinWidth(80);
                setGraphic(equipUnequipButton);
                equipUnequipButton.setOnAction(event -> {
                    if (!dataChangeAccessIsGranted()) {
                        return;
                    }

                    StringBuilder entChangeStrForFile = new StringBuilder();

                    if(item.isEquipped()) {
                        entChangeStrForFile.append("Unequip: enemy -> " + enemy.getName() + " -> " + item.getName() + " ! ");

                        enemy.unEquipItem(item);
                        equipUnequipButton.setText("Equip");
                    }
                    else {
                        entChangeStrForFile.append("Equip: enemy -> " + enemy.getName() + " -> " + item.getName() + " ! ");

                        enemy.getEquippedItemsList().stream()
                                .filter(param -> param.getType().equals(item.getType()))
                                .findFirst().ifPresent(enemy::unEquipItem);
                        enemy.equipItem(item);
                        equipUnequipButton.setText("Unequip");
                    }

                    enemyInvTableView.getColumns().get(enemyInvTableView.getColumns().size() - 1).setVisible(false);
                    enemyInvTableView.getColumns().get(enemyInvTableView.getColumns().size() - 1).setVisible(true);
                    updateEnemyLabels(enemy, LABEL_UPDATE_MODE.POPULATE);

                    entChangeStrForFile.append(" " + " ! " + " " + " ! " + LoginController.getUserName()
                            + " ! " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                    loadEnemyChangeToFile(entChangeStrForFile);
                    loadEnemyChangeToDB(entChangeStrForFile);
                });
            }
        });
    }

    public void onEnemyListTableViewMouseClick(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (enemyLabelHBox.isDisabled()) {
                enemyLabelHBox.setDisable(false);
            }
            updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(), LABEL_UPDATE_MODE.POPULATE);
            updateEnemyInvItems(enemyListTableView.getSelectionModel().getSelectedItem());
        }
    }

    public void onPlayerButtonPress() {
        playerToggleButton.setStyle("-fx-background-color: #29202e");
        enemiesToggleButton.setStyle("-fx-background-color: #363f4d");
        itemsToggleButton.setStyle("-fx-background-color: #363f4d");
        usersToggleButton.setStyle("-fx-background-color: #363f4d");
        updatePlayerStatView();
        updatePlayerItemList();
        playerControlGridPane.toFront();
    }

    public void onEnemiesButtonPress() {
        enemiesToggleButton.setStyle("-fx-background-color: #29202e");
        playerToggleButton.setStyle("-fx-background-color: #363f4d");
        itemsToggleButton.setStyle("-fx-background-color: #363f4d");
        usersToggleButton.setStyle("-fx-background-color: #363f4d");
        updateEnemyTableView();
        if (enemyListTableView.getSelectionModel().getSelectedItem() != null) {
            updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(), LABEL_UPDATE_MODE.POPULATE);
            updateEnemyInvItems(enemyListTableView.getSelectionModel().getSelectedItem());
        }
        else {
            enemyLabelHBox.setDisable(true);
        }
        enemiesControlGridPane.toFront();
    }

    public void onItemsButtonPress() {
        itemsToggleButton.setStyle("-fx-background-color: #29202e");
        playerToggleButton.setStyle("-fx-background-color: #363f4d");
        enemiesToggleButton.setStyle("-fx-background-color: #363f4d");
        usersToggleButton.setStyle("-fx-background-color: #363f4d");
        updateItemTable();
        itemsControlBorderPane.toFront();
    }

    public void onUsersButtonPress() {
        usersToggleButton.setStyle("-fx-background-color: #29202e");
        itemsToggleButton.setStyle("-fx-background-color: #363f4d");
        playerToggleButton.setStyle("-fx-background-color: #363f4d");
        enemiesToggleButton.setStyle("-fx-background-color: #363f4d");
        updateUsersTable();
        usersControlBorderPane.toFront();
    }

    public void onBackButtonPress() {
        try {
            Path path = Paths.get("IO_files/user_entities/" + LoginController.getUserName() + "_entities.dat");
            FileIO.saveObjectsToFile(path, lclEntitiesInCellsList,  GameScreenController.escapeHole,
                    GameScreenController.depthLevelString.get(), enemyList, player,
                    GameScreenController.trader,  chest);

            Database.executeFlushAndSave(player, enemyList, GameScreenController.trader, chest,
                    GameScreenController.escapeHole, GameScreenController.depthLevelString.get());
        }
        catch (SQLException | IOException | UserDoesntExistException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Database error");
        }

        MainController.replaceRootToGameScreen();
    }
    public void onEnemyNameTextField() {
        if (dataChangeAccessIsGranted()) {
            StringBuilder entChangeStrForFile = new StringBuilder("Name: enemy -> "
                    + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                    + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                    + enemyNameTextField.getText() + " ! " + LoginController.getUserName() + " ! "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            enemyListTableView.getSelectionModel().getSelectedItem().setName(enemyNameTextField.getText());

            enemyListTableView.refresh();

            loadEnemyChangeToFile(entChangeStrForFile);
            loadEnemyChangeToDB(entChangeStrForFile);
        }
        updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(), LABEL_UPDATE_MODE.POPULATE);
    }
    public void onEnemyLevelTextField() {
        try {
            String tmpStr = enemyLevelTextField.getText();
            int level = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (level < 0) {
                throw new NegativeStatInputException("Inputted level '" + level + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Level: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getLevel() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setLevel(level);
                enemyListTableView.refresh();
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyLevelTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getLevel()));
    }
    public void onEnemyBaseHealthTextField() {
        try {
            String tmpStr = enemyBaseHealthTextField.getText();
            int baseHealth = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (baseHealth < 0) {
                throw new NegativeStatInputException("Inputted base health '" + baseHealth + "' is less than 0");
            }
            if (baseHealth == 0) {
                throw new NegativeStatInputException("Inputted base health '" + baseHealth + "' equals to 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Base health: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getBaseHealth() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setBaseHealth(baseHealth);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyBaseHealthTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getBaseHealth()));
    }
    public void onEnemyHealthTextField() {
        try {
            String tmpStr = enemyHealthTextField.getText();
            int health = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (health < 0) {
                throw new NegativeStatInputException("Inputted health '" + health + "' is less than 0");
            }
            else if (health == 0) {
                throw new ZeroValueInputException("Inputted health '" + health + "' equals to 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Health: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getHealth() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setHealth(health);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);
                
                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | ZeroValueInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyHealthTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getHealth()));
    }
    public void onEnemyStaminaTextField() {
        try {
            String tmpStr = enemyStaminaTextField.getText();
            int stamina = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (stamina < 0) {
                throw new NegativeStatInputException("Inputted stamina '" + stamina + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Stamina: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getStamina() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setStamina(stamina);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyStaminaTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getStamina()));
    }

    public void onEnemyWeightTextField() {
        try {
            String tmpStr = enemyWeightTextField.getText();
            int weight = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (weight < 0) {
                throw new NegativeStatInputException("Inputted weight '" + weight + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Weight: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getWeight() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setWeight(weight);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyWeightTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getWeight()));
    }
    public void onEnemyArmourTextField() {
        try {
            String tmpStr = enemyArmourTextField.getText();
            int armour = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (armour < 0) {
                throw new NegativeStatInputException("Inputted armour '" + armour + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Armour: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getArmour() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setArmour(armour);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyArmourTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getArmour()));
    }
    public void onEnemyAttSpeedTextField() {
        try {
            String tmpStr = enemyAttSpeedTextField.getText();
            int attSpeed = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (attSpeed < 0) {
                throw new NegativeStatInputException("Inputted attack speed '" + attSpeed + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Attack speed: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getAttackSpeed() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setAttackSpeed(attSpeed);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyAttSpeedTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getAttackSpeed()));
    }
    public void onEnemyBaseAttDmgTextField() {
        try {
            String tmpStr = enemyBaseAttDmgTextField.getText();
            int baseAtt = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (baseAtt < 0) {
                throw new NegativeStatInputException("Inputted base attack '" + baseAtt + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Base attack damage: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getBaseAttDmg() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setBaseAttDmg(baseAtt);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(), 
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyBaseAttDmgTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getBaseAttDmg()));
    }
    public void onEnemyMedAttDmgTextField() {
        try {
            String tmpStr = enemyMedAttDmgTextField.getText();
            int medAtt = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (medAtt < 0) {
                throw new NegativeStatInputException("Inputted median attack '" + medAtt + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Median attack damage: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getMedianAttDmg() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setMedianAttDmg(medAtt);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyMedAttDmgTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getMedianAttDmg()));
    }
    public void onEnemyAccuracyTextField() {
        try {
            String tmpStr = enemyAccuracyTextField.getText();
            if (!tmpStr.matches("[0-9.]+")) {
                throw new NumberFormatException("Only decimal numbers allowed");
            }

            double accuracy = Double.parseDouble(tmpStr.substring(0, Math.min(tmpStr.length(), 8)));

            if (accuracy < 0) {
                throw new NegativeStatInputException("Inputted accuracy '" + accuracy + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Accuracy: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getAccuracy() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setAccuracy(accuracy);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyAccuracyTextField.setText(String.format(Locale.ENGLISH, "%.4f"
                , enemyListTableView.getSelectionModel().getSelectedItem().getAccuracy()));
    }

    public void onEnemyInitiativeTextField() {
        try {
            String tmpStr = enemyInitiativeTextField.getText();
            int initiative = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (initiative < 0) {
                throw new NegativeStatInputException("Inputted initiative '" + initiative + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Initiative: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getInitiative() + " ! "
                        + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setInitiative(initiative);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyInitiativeTextField
                .setText(Integer.toString(enemyListTableView.getSelectionModel().getSelectedItem().getInitiative()));
    }
    public void onEnemySexTextField() {
        String tmpStr = enemySexTextField.getText();

        if (!SEX.isValidSex(tmpStr)) {
            enemySexTextField.setText(enemyListTableView.getSelectionModel().getSelectedItem().getSex().toString());
            showErrorAlert("Inputted sex is not valid (Political answer:" +
                    " not working with the game logic/code due to time constraints)", "Input error");

            return;
        }

        if (dataChangeAccessIsGranted()) {
            StringBuilder entChangeStrForFile = new StringBuilder("Sex: enemy -> "
                    + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                    + enemyListTableView.getSelectionModel().getSelectedItem().getSex() + " ! " + tmpStr
                    + " ! " + LoginController.getUserName() + " ! "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            enemyListTableView.getSelectionModel().getSelectedItem().setSex(switch(tmpStr.toLowerCase()) {
                case "male" -> SEX.MALE;
                case "female" -> SEX.FEMALE;
                default -> SEX.UNKNOWN;
            });
            updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(), LABEL_UPDATE_MODE.POPULATE);
            enemyListTableView.refresh();

            loadEnemyChangeToFile(entChangeStrForFile);
            loadEnemyChangeToDB(entChangeStrForFile);
        }
    }
    public void onEnemySymbolTextField() {
        String tmpStr = enemySymoblTextField.getText();

        if (tmpStr.length() != 1) {
            enemySymoblTextField.setText(enemyListTableView.getSelectionModel().getSelectedItem().getSymbol());
            showErrorAlert("Inputted symbol needs to be of length 1", "Input error");

            return;
        }

        if (dataChangeAccessIsGranted()) {
            StringBuilder entChangeStrForFile = new StringBuilder("Symbol: enemy -> "
                    + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                    + enemyListTableView.getSelectionModel().getSelectedItem().getSymbol() + " ! " + tmpStr
                    + " ! " + LoginController.getUserName() + " ! "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            enemyListTableView.getSelectionModel().getSelectedItem().setSymbol(tmpStr);
            updateEnemyTableView();
            updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                    LABEL_UPDATE_MODE.POPULATE);

            loadEnemyChangeToFile(entChangeStrForFile);
            loadEnemyChangeToDB(entChangeStrForFile);
        }
    }

    public void onEnemyCoordTextField() {
        try {
            String tmpStr = enemyCoordTextField.getText();

            String[] tmpStrArray = tmpStr.split(", ");
            if (tmpStrArray.length != 2) {
                throw new WrongCoordInputFormatException("Wrong inputted format\nCorrect: x, y");
            }

            int coordX = Integer.parseInt(tmpStrArray[0].substring(0, Math.min(tmpStrArray[0].length(), 5)));
            int coordY = Integer.parseInt(tmpStrArray[1].substring(0, Math.min(tmpStrArray[1].length(), 5)));

            if (coordX < 0) {
                throw new NegativeStatInputException("Inputted coordinate x '" + coordX + "' is less than 0");
            }
            else if (coordY < 0) {
                throw new NegativeStatInputException("Inputted coordinate y '" + coordY + "' is less than 0");
            }
            else if (coordX >= GameScreenController.gameBoardRowCount) {
                throw new NegativeStatInputException("Inputted coordinate x '" + coordX + "' is greater than max column count");
            }
            else if (coordY >= GameScreenController.gameBoardColumnCount) {
                throw new NegativeStatInputException("Inputted coordinate y '" + coordY + "' is greater than max row count");
            }

            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Coordinates: enemy -> "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getName() + " ! "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getX() + ", "
                        + enemyListTableView.getSelectionModel().getSelectedItem().getY() + " ! "
                        + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                enemyListTableView.getSelectionModel().getSelectedItem().setX(coordX);
                enemyListTableView.getSelectionModel().getSelectedItem().setY(coordY);
                updateEnemyLabels(enemyListTableView.getSelectionModel().getSelectedItem(),
                        LABEL_UPDATE_MODE.POPULATE);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException | WrongCoordInputFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        enemyCoordTextField.setText(enemyListTableView.getSelectionModel().getSelectedItem().getX() + ", "
                + enemyListTableView.getSelectionModel().getSelectedItem().getY());
    }

    public boolean dataChangeAccessIsGranted() {
        baseStackPane.setDisable(true);
        Alert confirmWind = new Alert(Alert.AlertType.CONFIRMATION);
        confirmWind.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        confirmWind.initOwner(MainApplication.mainStage);
        confirmWind.initStyle(StageStyle.UNDECORATED);
        confirmWind.setGraphic(null);
        confirmWind.setHeaderText("Are you sure You want to change the data?");
        Optional<ButtonType> result = confirmWind.showAndWait();
        if (result.isPresent()) {
            baseStackPane.setDisable(false);
        }
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void updateItemList() {
        enemyList.forEach(enemy -> itemList.addAll(enemy.getInventoryList()));
        itemList.addAll(player.getInventoryList());
        itemList.addAll(chest.getInventory());
    }

    public void updateItemTable() {
        itemListTableView.setItems(FXCollections.observableList(itemList));

        itemListItemNameTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        itemListItemNameTableColumn.setCellFactory(cell -> new TableCell<>() {
            private final TextField textField = new TextField();

            @Override
            public void updateItem(Item item, boolean empty) {
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                textField.setText(item.getName());
                textField.setMinWidth(50);
                textField.setStyle("-fx-background-color: transparent;");
                setGraphic(textField);

                textField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER) && dataChangeAccessIsGranted()) {
                        StringBuilder entChangeStrForFile = new StringBuilder("Name: item -> " + item.getName() + " ! "
                                + item.getName() + " ! " + textField + " ! "
                                + LoginController.getUserName() + " ! "
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                        item.setName(textField.getText());

                        loadEnemyChangeToFile(entChangeStrForFile);
                        loadEnemyChangeToDB(entChangeStrForFile);
                    }
                });
            }
        });

        itemListItemDefAttTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        itemListItemDefAttTableColumn.setCellFactory(cell -> new TableCell<>() {
            private final TextField textField = new TextField();

            @Override
            public void updateItem(Item item, boolean empty) {
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                if (item instanceof Armour) {
                    textField.setText(Integer.toString(((Armour)item).getArmour()));
                }
                else {
                    textField.setText(Integer.toString(((Weapon)item).getDamage()));
                }
                textField.setMinWidth(50);
                textField.setStyle("-fx-background-color: transparent;");
                setGraphic(textField);

                textField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        try {
                            String tmpStr = textField.getText();
                            int defAtt = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

                            if (defAtt < 0) {
                                throw new NegativeStatInputException("Inputted defence/attack '" + defAtt +
                                        "' is less than 0");
                            }
                            if (dataChangeAccessIsGranted()) {
                                StringBuilder entChangeStrForFile = new StringBuilder(
                                        (item instanceof Armour ? "Defence" : "Attack") + ": item -> "
                                        + item.getName() + " ! "
                                        + (item instanceof Armour ? ((Armour)item).getArmour() : ((Weapon) item).getDamage())
                                        + " ! " + tmpStr + " ! "
                                        + LoginController.getUserName() + " ! "
                                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                                if (item.isEquipped()) {
                                    ((Being)item.getOwner()).unEquipItem(item);
                                    if (item instanceof Armour) {
                                        ((Armour) item).setArmour(defAtt);
                                    }
                                    else {
                                        ((Weapon) item).setDamage(defAtt);
                                    }
                                    ((Being)item.getOwner()).equipItem(item);
                                }
                                else {
                                    if (item instanceof Armour) {
                                        ((Armour) item).setArmour(defAtt);
                                    }
                                    else {
                                        ((Weapon) item).setDamage(defAtt);
                                    }
                                }

                                loadEnemyChangeToFile(entChangeStrForFile);
                                loadEnemyChangeToDB(entChangeStrForFile);

                                return;
                            }
                        }
                        catch (NegativeStatInputException | NumberFormatException e) {
                            logger.error(e.getMessage(), e);
                            showErrorAlert(e.getMessage(), "Input error");
                        }

                        if (item instanceof Armour) {
                            textField.setText(Integer.toString(((Armour)item).getArmour()));
                        }
                        else {
                            textField.setText(Integer.toString(((Weapon)item).getDamage()));
                        }
                    }
                });
            }
        });

        itemListItemWeightTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        itemListItemWeightTableColumn.setCellFactory(cell -> new TableCell<>() {
            private final TextField textField = new TextField();

            @Override
            public void updateItem(Item item, boolean empty) {
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                textField.setText(Integer.toString(item.getWeight()));
                textField.setMinWidth(50);
                textField.setStyle("-fx-background-color: transparent;");
                setGraphic(textField);

                textField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        try {
                            String tmpStr = textField.getText();
                            int weight = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

                            if (weight < 0) {
                                throw new NegativeStatInputException("Inputted weight '" + weight + "' is less than 0");
                            }
                            if (dataChangeAccessIsGranted()) {
                                StringBuilder entChangeStrForFile = new StringBuilder("Weight: item -> "
                                        + item.getName() + " ! "
                                        + item.getWeight() + " ! " + tmpStr + " ! "
                                        + LoginController.getUserName() + " ! "
                                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                                if (item.isEquipped()) {
                                    ((Being)item.getOwner()).unEquipItem(item);
                                    item.setWeight(weight);
                                    ((Being)item.getOwner()).equipItem(item);
                                }
                                else {
                                    item.setWeight(weight);
                                }

                                loadEnemyChangeToFile(entChangeStrForFile);
                                loadEnemyChangeToDB(entChangeStrForFile);

                                return;
                            }
                        }
                        catch (NegativeStatInputException | NumberFormatException e) {
                            logger.error(e.getMessage(), e);
                            showErrorAlert(e.getMessage(), "Input error");
                        }

                        textField.setText(Integer.toString(item.getWeight()));
                    }
                });
            }
        });

        itemListItemLevelTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        itemListItemLevelTableColumn.setCellFactory(cell -> new TableCell<>() {
            private final TextField textField = new TextField();

            @Override
            public void updateItem(Item item, boolean empty) {
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                textField.setText(Integer.toString(item.getLevel()));
                textField.setMinWidth(50);
                textField.setStyle("-fx-background-color: transparent;");
                setGraphic(textField);

                textField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        try {
                            String tmpStr = textField.getText();
                            int level = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

                            if (level < 0) {
                                throw new NegativeStatInputException("Inputted level '" + level + "' is less than 0");
                            }
                            if (dataChangeAccessIsGranted()) {
                                StringBuilder entChangeStrForFile = new StringBuilder("Level: item -> "
                                        + item.getName() + " ! "
                                        + item.getLevel() + " ! " + tmpStr + " ! "
                                        + LoginController.getUserName() + " ! "
                                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                                if (item.isEquipped()) {
                                    ((Being)item.getOwner()).unEquipItem(item);
                                    item.setLevel(level);
                                    ((Being)item.getOwner()).equipItem(item);
                                }
                                else {
                                    item.setLevel(level);
                                }

                                loadEnemyChangeToFile(entChangeStrForFile);
                                loadEnemyChangeToDB(entChangeStrForFile);

                                return;
                            }
                        }
                        catch (NegativeStatInputException | NumberFormatException e) {
                            logger.error(e.getMessage(), e);
                            showErrorAlert(e.getMessage(), "Input error");
                        }

                        textField.setText(Integer.toString(item.getLevel()));
                    }
                });
            }
        });

        itemListItemTypeTableColumn.
                setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        itemListItemSymbolTableColumn.
                setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        itemListItemSymbolTableColumn.setCellFactory(cell -> new TableCell<>() {
            private final TextField textField = new TextField();

            @Override
            public void updateItem(Item item, boolean empty) {
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                textField.setText(item.getSymbol());
                textField.setMinWidth(50);
                textField.setStyle("-fx-background-color: transparent;");
                setGraphic(textField);

                textField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        String tmpStr = textField.getText();

                        if (tmpStr.length() != 1) {
                            textField.setText(item.getSymbol());
                            showErrorAlert("Inputted symbol needs to be of length 1", "Input error");

                            return;
                        }

                        if (dataChangeAccessIsGranted()) {
                            StringBuilder entChangeStrForFile = new StringBuilder("Symbol: item -> "
                                    + item.getName() + " ! "
                                    + item.getSymbol() + " ! " + tmpStr + " ! "
                                    + LoginController.getUserName() + " ! "
                                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                            item.setSymbol(tmpStr);

                            loadEnemyChangeToFile(entChangeStrForFile);
                            loadEnemyChangeToDB(entChangeStrForFile);
                        }
                        else {
                            textField.setText(item.getSymbol());
                        }
                    }
                });
            }
        });

        itemListCoordTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        itemListCoordTableColumn.setCellFactory(cell -> new TableCell<>() {
            private final TextField textField = new TextField();

            @Override
            public void updateItem(Item item, boolean empty) {
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                textField.setText(item.getX() + ", " + item.getY());
                textField.setMinWidth(50);
                textField.setStyle("-fx-background-color: transparent;");
                if (item.isPickedUp()) {
                    textField.setEditable(false);
                }
                else {
                    textField.setEditable(true);
                }
                setGraphic(textField);

                textField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        try {
                            String tmpStr = textField.getText();

                            String[] tmpStrArray = tmpStr.split(", ");
                            if (tmpStrArray.length != 2) {
                                throw new WrongCoordInputFormatException("Wrong inputted format\nCorrect: x, y");
                            }

                            int coordX = Integer.parseInt(tmpStrArray[0].substring(0,
                                    Math.min(tmpStrArray[0].length(), 5)));
                            int coordY = Integer.parseInt(tmpStrArray[1].substring(0,
                                    Math.min(tmpStrArray[1].length(), 5)));

                            if (coordX < 0) {
                                throw new NegativeStatInputException("Inputted coordinate x '" + coordX
                                        + "' is less than 0");
                            }
                            else if (coordY < 0) {
                                throw new NegativeStatInputException("Inputted coordinate y '" + coordY
                                        + "' is less than 0");
                            }
                            else if (coordX >= GameScreenController.gameBoardRowCount) {
                                throw new NegativeStatInputException("Inputted coordinate x '" + coordX
                                        + "' is greater than max column count");
                            }
                            else if (coordY >= GameScreenController.gameBoardColumnCount) {
                                throw new NegativeStatInputException("Inputted coordinate y '" + coordY
                                        + "' is greater than max row count");
                            }

                            if (dataChangeAccessIsGranted()) {
                                StringBuilder entChangeStrForFile = new StringBuilder("Coordinates: item -> "
                                        + item.getName() + " ! "
                                        + item.getX() + ", " + item.getY() + " ! " + tmpStr
                                        + " ! " + LoginController.getUserName() + " ! "
                                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                                item.setX(coordX);
                                item.setY(coordY);

                                loadEnemyChangeToFile(entChangeStrForFile);
                                loadEnemyChangeToDB(entChangeStrForFile);

                                return;
                            }
                        }
                        catch (NegativeStatInputException | NumberFormatException | WrongCoordInputFormatException e) {
                            logger.error(e.getMessage(), e);
                            showErrorAlert(e.getMessage(), "Input error");
                        }

                        textField.setText(item.getX() + ", " + item.getY());
                    }
                });
            }
        });

        itemListOwnerNameTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        itemListOwnerNameTableColumn.setCellFactory(cell -> new TableCell<>() {
            private final TextField textField = new TextField();

            @Override
            public void updateItem(Item item, boolean empty) {
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                textField.minWidth(130);
                textField.setStyle("-fx-background-color: transparent;");
                setGraphic(textField);
                textField.setText((item.getOwner() == null ? "" : item.getOwner().getName()));

                textField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        if (player.getName().equals(textField.getText()) && dataChangeAccessIsGranted()) {
                            StringBuilder entChangeStrForFile = new StringBuilder("Owner: item -> "
                                    + item.getName() + " ! "
                                    + (item.getOwner() == null ? "" : item.getOwner().getName()) +
                                    " ! " + textField.getText() + " ! " + LoginController.getUserName() + " ! "
                                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                            if (item.isPickedUp()) {
                                if (item.isEquipped()) {
                                    ((Being)item.getOwner()).unEquipItem(item);
                                }
                                ((Being)item.getOwner()).getInventoryList().remove(item);
                            }
                            else if (item.getOwner() instanceof Chest){
                                ((Chest<?>)item.getOwner()).getInventory().remove(item);
                            }
                            else {
                                item.setPickedUp(true);
                            }
                            item.setOwner(player);
                            player.getInventoryList().add(item);

                            loadEnemyChangeToFile(entChangeStrForFile);
                            loadEnemyChangeToDB(entChangeStrForFile);
                            return;
                        }
                        else if (textField.getText().equalsIgnoreCase("chest") && dataChangeAccessIsGranted()) {
                            if (chest != null && dataChangeAccessIsGranted()) {
                                StringBuilder entChangeStrForFile = new StringBuilder("Owner: item -> "
                                        + item.getName() + " ! "
                                        + (item.getOwner() == null ? "" : item.getOwner().getName()) +
                                        " ! " + textField.getText() + " ! " + LoginController.getUserName() + " ! "
                                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                                if (item.isPickedUp()) {
                                    if (item.isEquipped()) {
                                        ((Being)item.getOwner()).unEquipItem(item);
                                    }
                                    ((Being)item.getOwner()).getInventoryList().remove(item);
                                }
                                else {
                                    item.setPickedUp(true);
                                }
                                item.setOwner(chest);
                                chest.getInventory().add(item);

                                loadEnemyChangeToFile(entChangeStrForFile);
                                loadEnemyChangeToDB(entChangeStrForFile);
                                return;
                            }
                        }
                        else {
                            Enemy enemy = enemyList.stream()
                                    .filter(en -> en.getName().equalsIgnoreCase(textField.getText()))
                                    .findFirst().orElse(null);

                            if (enemy != null && dataChangeAccessIsGranted()) {
                                StringBuilder entChangeStrForFile = new StringBuilder("Owner: item -> "
                                        + item.getName() + " ! "
                                        + (item.getOwner() == null ? "" : item.getOwner().getName()) +
                                        " ! " + textField.getText() + " ! " + LoginController.getUserName() + " ! "
                                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                                if (item.isPickedUp()) {
                                    if (item.isEquipped()) {
                                        ((Being)item.getOwner()).unEquipItem(item);
                                    }
                                    ((Being)item.getOwner()).getInventoryList().remove(item);
                                }
                                else {
                                    item.setPickedUp(true);
                                }
                                item.setOwner(enemy);
                                enemy.getInventoryList().add(item);

                                loadEnemyChangeToFile(entChangeStrForFile);
                                loadEnemyChangeToDB(entChangeStrForFile);
                                return;
                            }
                        }

                        showErrorAlert("Entity named \"" + textField.getText() +
                                "\" not found", "Input error");
                        textField.setText(item.getOwner() == null ? "" : item.getOwner().getName());
                    }
                });

            }
        });

        itemListDeleteButtonTableColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        itemListDeleteButtonTableColumn.setCellFactory(cellData -> new TableCell<>() {
            private final Button deleteButton = new Button();

            @Override
            public void updateItem(Item item, boolean empty) {
                if (item == null) {
                    setGraphic(null);
                    return;
                }

                deleteButton.setText("Delete");
                deleteButton.setMinWidth(80);
                setGraphic(deleteButton);
                deleteButton.setOnAction(event -> {
                    if (dataChangeAccessIsGranted()) {
                        StringBuilder entChangeStrForFile = new StringBuilder("Delete: item -> "
                                + item.getName() + " ! " + " " + " ! "
                                + " " + " ! " + LoginController.getUserName() + " ! "
                                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                        if (item.isPickedUp()) {
                            if (item.isEquipped()) {
                                ((Being)item.getOwner()).unEquipItem(item);
                            }
                            ((Being)item.getOwner()).getInventoryList().remove(item);
                        }
                        itemList.remove(item);
                        Database.bufferDelItemId(item);
                        itemListTableView.getSelectionModel().clearSelection();
                        chest.getInventory().remove(item);
                        itemListTableView.refresh();

                        loadEnemyChangeToFile(entChangeStrForFile);
                        loadEnemyChangeToDB(entChangeStrForFile);
                    }
                });
            }
        });
    }

    public void updatePlayerStatView() {
        playerNameTextField.setText(player.getName());
        playerLevelTextField.setText(Integer.toString(player.getLevel()));
        playerBaseHealthTextField.setText(Integer.toString(player.getBaseHealth()));
        playerHealthTextField.setText(Integer.toString(player.getHealth()));
        playerStaminaTextField.setText(Integer.toString(player.getStamina()));
        playerWeightTextField.setText(Integer.toString(player.getWeight()));
        playerArmourTextField.setText(Integer.toString(player.getArmour()));
        playerAttSpeedTextField.setText(Integer.toString(player.getAttackSpeed()));
        playerBaseAttDmgTextField.setText(Integer.toString(player.getBaseAttDmg()));
        playerMedAttDmgTextField.setText(Integer.toString(player.getMedianAttDmg()));
        playerAccuracyTextField.setText(String.format(Locale.ENGLISH, "%.4f" , player.getAccuracy()));
        playerInitiativeTextField.setText(Integer.toString(player.getInitiative()));
        playerSexTextField.setText(player.getSex().toString());
        playerTypeTextField.setText(player.getType());
        playerSymoblTextField.setText(player.getSymbol());
        playerCoordTextField.setText(player.getX() + ", " + player.getY());
        playerKillCountTextField.setText(Integer.toString(player.getKillCount()));
    }

    public void updatePlayerItemList() {
        playerInvItemNameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        playerInvItemTypeTableColumn.setCellValueFactory(celData -> new SimpleStringProperty(celData.getValue().getType()));
        playerInvItemWeightTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getWeight())));
        playerInvItemDefAttTableColumn
                .setCellValueFactory(cellData -> CustomSimpleStringProperty.simpleStrPropertyDefAtt(cellData.getValue()));
        playerInvItemLevelTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getLevel())));

        playerInvEquipButtonTableColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        playerInvEquipButtonTableColumn.setCellFactory(cellData -> new TableCell<>() {
            private final Button equipUnequipButton = new Button();

            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setGraphic(null);
                    return;
                }
                else if(item.isEquipped()) {
                    equipUnequipButton.setText("Unequip");
                }
                else {
                    equipUnequipButton.setText("Equip");
                }

                equipUnequipButton.setMinWidth(80);
                setGraphic(equipUnequipButton);
                equipUnequipButton.setOnAction(event -> {
                    if (!dataChangeAccessIsGranted()) {
                        return;
                    }

                    StringBuilder entChangeStrForFile = new StringBuilder();

                    if(item.isEquipped()) {
                        entChangeStrForFile.append("Unequip: player -> "
                                + player.getName() + " -> " + item.getName() + " ! ");

                        player.unEquipItem(item);
                        equipUnequipButton.setText("Equip");
                    }
                    else {
                        entChangeStrForFile.append("Equip: player -> "
                                + player.getName() + " -> " + item.getName() + " ! ");

                        player.getEquippedItemsList().stream()
                                .filter(param -> param.getType().equals(item.getType()))
                                .findFirst().ifPresent(value -> player.unEquipItem(value));
                        player.equipItem(item);
                        equipUnequipButton.setText("Unequip");
                    }

                    playerInvItemsTableView.getColumns()
                            .get(playerInvItemsTableView.getColumns().size() - 1).setVisible(false);
                    playerInvItemsTableView.getColumns()
                            .get(playerInvItemsTableView.getColumns().size() - 1).setVisible(true);
                    updatePlayerStatView();

                    entChangeStrForFile.append(" " + " ! " + " " + " ! " + LoginController.getUserName()
                            + " ! " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    loadEnemyChangeToFile(entChangeStrForFile)
                    ;loadEnemyChangeToDB(entChangeStrForFile);
                });
            }
        });

        playerInvItemsTableView.setItems(FXCollections.observableList(player.getInventoryList()));
    }

    public void onPlayerNameTextField() {
        if (dataChangeAccessIsGranted()) {
            StringBuilder entChangeStrForFile = new StringBuilder("Name: player -> " + player.getName() + " ! "
                    + player.getName() + " ! "
                    + playerNameTextField.getText() + " ! " + LoginController.getUserName() + " ! "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            player.setName(playerNameTextField.getText());

            loadEnemyChangeToFile(entChangeStrForFile);
            loadEnemyChangeToDB(entChangeStrForFile);
        }
    }
    public void onPlayerLevelTextField() {
        try {
            String tmpStr = playerLevelTextField.getText();
            int level = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (level < 0) {
                throw new NegativeStatInputException("Inputted level '" + level + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Level: player -> " + player.getName() + " ! "
                        + player.getLevel() + " ! "
                        + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setLevel(level);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerLevelTextField.setText(Integer.toString(player.getLevel()));
    }
    public void onPlayerBaseHealthTextField() {
        try {
            String tmpStr = playerBaseHealthTextField.getText();
            int baseHealth = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (baseHealth < 0) {
                throw new NegativeStatInputException("Inputted base health '" + baseHealth + "' is less than 0");
            }
            if (baseHealth == 0) {
                throw new ZeroValueInputException("Inputted base health '" + baseHealth + "' equals to 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Base health: player -> " + player.getName() + " ! "
                        + player.getBaseHealth()
                        + " ! " + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setBaseHealth(baseHealth);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | ZeroValueInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerBaseHealthTextField.setText(Integer.toString(player.getBaseHealth()));
    }
    public void onPlayerHealthTextField() {
        try {
            String tmpStr = playerHealthTextField.getText();
            int health = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (health < 0) {
                throw new NegativeStatInputException("Inputted health '" + health + "' is less than 0");
            }
            if (health == 0) {
                throw new ZeroValueInputException("Inputted health '" + health + "' equals to 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Health: player -> " + player.getName() + " ! "
                        + player.getHealth() + " ! "
                        + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setHealth(health);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | ZeroValueInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerHealthTextField.setText(Integer.toString(player.getHealth()));
    }
    public void onPlayerStaminaTextField() {
        try {
            String tmpStr = playerStaminaTextField.getText();
            int stamina = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (stamina < 0) {
                throw new NegativeStatInputException("Inputted stamina '" + stamina + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Stamina: player -> " + player.getName() + " ! "
                        + player.getStamina() + " ! "
                        + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setStamina(stamina);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerStaminaTextField.setText(Integer.toString(player.getStamina()));
    }

    public void onPlayerWeightTextField() {
        try {
            String tmpStr = playerWeightTextField.getText();
            int weight = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (weight < 0) {
                throw new NegativeStatInputException("Inputted weight '" + weight + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Weight: player -> " + player.getName() + " ! "
                        + player.getWeight() + " ! "
                        + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setWeight(weight);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerWeightTextField.setText(Integer.toString(player.getWeight()));
    }
    public void onPlayerArmourTextField() {
        try {
            String tmpStr = playerArmourTextField.getText();
            int armour = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (armour < 0) {
                throw new NegativeStatInputException("Inputted armour '" + armour + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Armour: player -> " + player.getName() + " ! "
                        + player.getArmour() + " ! "
                        + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setArmour(armour);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerArmourTextField.setText(Integer.toString(player.getArmour()));
    }
    public void onPlayerAttSpeedTextField() {
        try {
            String tmpStr = playerAttSpeedTextField.getText();
            int attSpeed = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (attSpeed < 0) {
                throw new NegativeStatInputException("Inputted attack speed '" + attSpeed + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Attack speed: player -> " + player.getName() + " ! "
                        + player.getAttackSpeed()
                        + " ! " + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setAttackSpeed(attSpeed);

                loadEnemyChangeToFile(entChangeStrForFile);


                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerAttSpeedTextField.setText(Integer.toString(player.getAttackSpeed()));
    }
    public void onPlayerBaseAttDmgTextField() {
        try {
            String tmpStr = playerBaseAttDmgTextField.getText();
            int baseAtt = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (baseAtt < 0) {
                throw new NegativeStatInputException("Inputted base attack '" + baseAtt + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Base attack damage: player -> "
                        + player.getName() + " ! "
                        + player.getBaseAttDmg() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setBaseAttDmg(baseAtt);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerBaseAttDmgTextField.setText(Integer.toString(player.getBaseAttDmg()));
    }
    public void onPlayerMedAttDmgTextField() {
        try {
            String tmpStr = playerMedAttDmgTextField.getText();
            int medAtt = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (medAtt < 0) {
                throw new NegativeStatInputException("Inputted median attack '" + medAtt + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Median attack damage: player -> "
                        + player.getName() + " ! "
                        + player.getMedianAttDmg() + " ! " + tmpStr
                        + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setMedianAttDmg(medAtt);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerMedAttDmgTextField.setText(Integer.toString(player.getMedianAttDmg()));
    }
    public void onPlayerAccuracyTextField() {
        try {
            String tmpStr = playerAccuracyTextField.getText();
            if (!tmpStr.matches("[0-9.]+")) {
                throw new NumberFormatException("Only decimal numbers allowed");
            }

            double accuracy = Double.parseDouble(tmpStr.substring(0, Math.min(tmpStr.length(), 8)));

            if (accuracy < 0) {
                throw new NegativeStatInputException("Inputted accuracy '" + accuracy + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Accuracy: player -> " + player.getName() + " ! "
                        + player.getAccuracy()
                        + " ! " + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setAccuracy(accuracy);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch(NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerAccuracyTextField.setText(String.format(Locale.ENGLISH, "%.4f" , player.getAccuracy()));
    }
    public void onPlayerInitiativeTextField() {
        try {
            String tmpStr = playerInitiativeTextField.getText();
            int initiative = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), 5)));

            if (initiative < 0) {
                throw new NegativeStatInputException("Inputted initiative '" + initiative + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Initiative: player -> " + player.getName() + " ! "
                        + player.getInitiative()
                        + " ! " + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setInitiative(initiative);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerInitiativeTextField.setText(Integer.toString(player.getInitiative()));
    }
    public void onPlayerSexTextField() {
        String tmpStr = playerSexTextField.getText();

        if (!SEX.isValidSex(tmpStr)) {
            playerSexTextField.setText(player.getSex().toString());
            showErrorAlert("Inputted sex is not valid (Political answer:" +
                    " not working with the game logic/code due to time constraints)", "Input error");

            return;
        }

        if (dataChangeAccessIsGranted()) {
            StringBuilder entChangeStrForFile = new StringBuilder("Sex: player -> " + player.getName() + " ! "
                    + player.getSex()
                    + " ! " + tmpStr + " ! " + LoginController.getUserName() + " ! "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            player.setSex(switch(playerSexTextField.getText().toLowerCase()) {
                case "male" -> SEX.MALE;
                case "female" -> SEX.FEMALE;
                default -> SEX.UNKNOWN;
            });

            loadEnemyChangeToFile(entChangeStrForFile);
            loadEnemyChangeToDB(entChangeStrForFile);
        }
        else {
            playerSexTextField.setText(Integer.toString(player.getSex().toString().charAt(0)));
        }
    }
    public void onPlayerSymbolTextField() {
        String tmpStr = playerSymoblTextField.getText();

        if (tmpStr.length() != 1) {
            playerSymoblTextField.setText(player.getSymbol());
            showErrorAlert("Inputted symbol needs to be of length 1", "Input error");

            return;
        }

        if (dataChangeAccessIsGranted()) {
            StringBuilder entChangeStrForFile = new StringBuilder("Symbol: player -> " + player.getName() + " ! "
                    + player.getSymbol()
                    + " ! " + tmpStr + " ! " + LoginController.getUserName() + " ! "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            player.setSymbol(tmpStr);

            loadEnemyChangeToFile(entChangeStrForFile);
            loadEnemyChangeToDB(entChangeStrForFile);
        }
        else {
            playerSymoblTextField.setText(player.getSymbol());
        }
    }

    public void onPlayerCoordTextField() {
        try {
            String tmpStr =playerCoordTextField.getText();

            String[] tmpStrArray = tmpStr.split(", ");
            if (tmpStrArray.length != 2) {
                throw new WrongCoordInputFormatException("Wrong inputted format\nCorrect: x, y");
            }

            int coordX = Integer.parseInt(tmpStrArray[0].substring(0, Math.min(tmpStrArray[0].length(), 5)));
            int coordY = Integer.parseInt(tmpStrArray[1].substring(0, Math.min(tmpStrArray[1].length(), 5)));

            if (coordX < 0) {
                throw new NegativeStatInputException("Inputted coordinate x '" + coordX + "' is less than 0");
            }
            else if (coordY < 0) {
                throw new NegativeStatInputException("Inputted coordinate y '" + coordY + "' is less than 0");
            }
            else if (coordX >= GameScreenController.gameBoardRowCount) {
                throw new NegativeStatInputException("Inputted coordinate x '" + coordX + "' is greater than max column count");
            }
            else if (coordY >= GameScreenController.gameBoardColumnCount) {
                throw new NegativeStatInputException("Inputted coordinate y '" + coordY + "' is greater than max row count");
            }

            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Coordinates: player -> " + player.getName() + " ! "
                        + player.getX() + ", " + player.getY() + " ! "
                        + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setX(coordX);
                player.setY(coordY);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException | WrongCoordInputFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerCoordTextField.setText(player.getX() + ", " + player.getY());
    }

    public void onPlayerKillCountTextField() {
        try {
            String tmpStr = playerKillCountTextField.getText();
            int killCount = Integer.parseInt(tmpStr.substring(0, Math.min(tmpStr.length(), Integer.MAX_VALUE)));

            if (killCount < 0) {
                throw new NegativeStatInputException("Inputted kill count '" + killCount + "' is less than 0");
            }
            if (dataChangeAccessIsGranted()) {
                StringBuilder entChangeStrForFile = new StringBuilder("Player kill count ! " + player.getKillCount()
                        + " ! " + tmpStr + " ! " + LoginController.getUserName() + " ! "
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

                player.setKillCount(killCount);

                loadEnemyChangeToFile(entChangeStrForFile);
                loadEnemyChangeToDB(entChangeStrForFile);

                return;
            }
        }
        catch (NegativeStatInputException | NumberFormatException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Input error");
        }

        playerKillCountTextField.setText(Integer.toString(player.getKillCount()));
    }

    public void onEnemyFilterByAllTextField() {
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy -> enemy.getName().toLowerCase().contains(enemyFilterByAllTextField.getText().toLowerCase())
                        || enemy.getType().toLowerCase().contains(enemyFilterByAllTextField.getText().toLowerCase())
                        || enemy.getSex().toString().toLowerCase().contains(enemyFilterByAllTextField.getText().toLowerCase())
                        || Integer.toString(enemy.getLevel()).toLowerCase().contains(enemyFilterByAllTextField.getText().toLowerCase()))
                .toList()
        ));
    }
    public void onEnemyFilterByNameTextField() {
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy -> enemy.getName().toLowerCase()
                        .contains(enemyFilterByNameTextField == null ? "" : enemyFilterByNameTextField.getText().toLowerCase()))
                .toList()
        ));
    }

    public void onEnemyFilterByTypeTextField() {
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy ->  enemy.getType().toLowerCase().contains(enemyFilterByTypeTextField.getText().toLowerCase()))
                .toList()
        ));
    }

    public void onEnemyFilterBySexTextField() {
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy -> enemy.getSex().toString().toLowerCase()
                        .contains(enemyFilterBySexTextField.getText().toLowerCase()))
                .toList()
        ));
    }

    public void onEnemyFilterByLevelTextField() {
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy -> Integer.toString(enemy.getLevel()).toLowerCase()
                        .contains(enemyFilterByLevelTextField.getText().toLowerCase()))
                .toList()
        ));
    }
    public void onEnemySearchNameTextField() {
        if (enemySearchNameTextField.getText().length() == 0) {
            enemyListTableView.setItems(FXCollections.observableList(enemyList));
            enemyListTableView.refresh();
            return;
        }
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy -> enemy.getName().equalsIgnoreCase(enemySearchNameTextField.getText()))
                .toList()
        ));
    }
    public void onEnemySearchTypeTextField() {
        if (enemySearchTypeTextField.getText().length() == 0) {
            enemyListTableView.setItems(FXCollections.observableList(enemyList));
            enemyListTableView.refresh();
            return;
        }
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy -> enemy.getType().equalsIgnoreCase(enemySearchTypeTextField.getText()))
                .toList()
        ));
    }
    public void onEnemySearchSexTextField() {
        if (enemySearchSexTextField.getText().length() == 0) {
            enemyListTableView.setItems(FXCollections.observableList(enemyList));
            enemyListTableView.refresh();
            return;
        }
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy -> enemy.getSex().toString().equalsIgnoreCase(enemySearchSexTextField.getText()))
                .toList()
        ));
    }
    public void onEnemySearchLevelTextField() {
        if (enemySearchLevelTextField.getText().length() == 0) {
            enemyListTableView.setItems(FXCollections.observableList(enemyList));
            enemyListTableView.refresh();
            return;
        }
        enemyListTableView.setItems(FXCollections.observableList(enemyList.stream()
                .filter(enemy -> Integer.toString(enemy.getLevel()).equalsIgnoreCase(enemySearchLevelTextField.getText()))
                .toList()
        ));
    }


    public void onItemFilterByAllTextField() {
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item ->
                        (item.getOwner() != null
                                && item.getOwner().getName().toLowerCase().contains(itemFilterByAllTextField.getText().toLowerCase()))
                        || item.getName().toLowerCase().contains(itemFilterByAllTextField.getText().toLowerCase())
                        || item.getType().toLowerCase().contains(itemFilterByAllTextField.getText().toLowerCase())
                        || (item instanceof Armour
                            ? Integer.toString(((Armour)item).getArmour()).equals(itemFilterByAllTextField.getText())
                            : Integer.toString(((Weapon)item).getDamage()).equals(itemFilterByAllTextField.getText()))
                        || Integer.toString(item.getWeight()).equals(itemFilterByAllTextField.getText())
                        || ("(" + item.getX() + ", " + item.getY() + ")").equals(itemFilterByAllTextField.getText())
                        || item.getSymbol().equals(itemFilterByAllTextField.getText())
                        || Integer.toString(item.getLevel()).equals(itemFilterByAllTextField.getText()))
                .toList()
        ));
    }

    public void onItemFilterByOwnerTextField() {
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item.getOwner() != null
                        && item.getOwner().getName().toLowerCase().contains(itemFilterByOwnerTextField.getText().toLowerCase()))
                .toList()
        ));
    }

    public void onItemFilterByNameTextField() {
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item.getName().toLowerCase()
                        .contains(itemFilterByNameTextField.getText().toLowerCase()))
                .toList()
        ));
    }

    public void onItemFilterByTypeTextField() {
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item.getType().toLowerCase()
                        .contains(itemFilterByTypeTextField.getText().toLowerCase()))
                .toList()
        ));
    }

    public void onItemFilterByDefDmgTextField() {
        if (itemFilterByDefDmgTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item instanceof Armour
                        ? Integer.toString(((Armour)item).getArmour()).equals(itemFilterByDefDmgTextField.getText())
                        : Integer.toString(((Weapon)item).getDamage()).equals(itemFilterByDefDmgTextField.getText()))
                .toList()
        ));
    }

    public void onItemFilterByWeightTextField() {
        if (itemFilterByWeightTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> Integer.toString(item.getWeight()).equals(itemFilterByWeightTextField.getText()))
                .toList()
        ));
    }

    public void onItemFilterBySymbolTextField() {
        if (itemFilterBySymbolTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item.getSymbol().equals(itemFilterBySymbolTextField.getText()))
                .toList()
        ));
    }

    public void onItemFilterByCoordTextField() {
        if (itemFilterByCoordTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> ("(" + item.getX() + ", " + item.getY() + ")").equals(itemFilterByCoordTextField.getText()))
                .toList()
        ));
    }

    public void onItemFilterByLevelTextField() {
        if (itemFilterByLevelTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> Integer.toString(item.getLevel()).equals(itemFilterByLevelTextField.getText()))
                .toList()
        ));
    }

    public void onItemSearchOwnerTextField() {
        if (itemSearchOwnerTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item.getOwner() != null
                        && item.getOwner().getName().equalsIgnoreCase(itemSearchOwnerTextField.getText().toLowerCase()))
                .toList()
        ));
    }
    public void onItemSearchNameTextField() {
        if (itemSearchNameTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item.getName().equalsIgnoreCase(itemSearchNameTextField.getText()))
                .toList()
        ));
    }

    public void onItemSearchTypeTextField() {
        if (itemSearchTypeTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item.getType().equalsIgnoreCase(itemSearchTypeTextField.getText()))
                .toList()
        ));
    }

    public void onItemSearchDefDmgTextField() {
        if (itemSearchDefDmgTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item instanceof Armour
                        ? Integer.toString(((Armour)item).getArmour()).equals(itemSearchDefDmgTextField.getText())
                        : Integer.toString(((Weapon)item).getDamage()).equals(itemSearchDefDmgTextField.getText()))
                .toList()
        ));
    }

    public void onItemSearchWeightTextField() {
        if (itemSearchNameTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> Integer.toString(item.getWeight()).equals(itemSearchWeightTextField.getText()))
                .toList()
        ));
    }

    public void onItemSearchSymbolTextField() {
        if (itemSearchSymbolTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> item.getSymbol().equalsIgnoreCase(itemSearchSymbolTextField.getText()))
                .toList()
        ));
    }

    public void onItemSearchCoordTextField() {
        if (itemSearchCoordTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> ("(" + item.getX() + ", " + item.getY() + ")").equals(itemSearchCoordTextField.getText()))
                .toList()
        ));
    }

    public void onItemSearchLevelTextField() {
        if (itemSearchLevelTextField.getText().length() == 0) {
            itemListTableView.setItems(FXCollections.observableList(itemList));
            itemListTableView.refresh();
            return;
        }
        itemListTableView.setItems(FXCollections.observableList(itemList.stream()
                .filter(item -> Integer.toString(item.getLevel()).equals(itemSearchLevelTextField.getText()))
                .toList()
        ));
    }

    public void createTypeItem() {
        Item item;
        String name;
        int depth = Integer.parseInt(GameScreenController.depthLevelString.get());
        int itemX, itemY;
        while((itemX = randNum.nextInt(GameScreenController.gameBoardRowCount)) == GameScreenController.gameBoardRowCount / 2);
        while((itemY = randNum.nextInt(GameScreenController.gameBoardColumnCount)) == GameScreenController.gameBoardColumnCount / 2);

        if (createHeadwareButton.isArmed()) {
            name = Names.nameMap.get("headware").get(randNum.nextInt(0,  Names.nameMap.get("headware").size()));
            item = new Headware(randNum.nextInt(6) + 1 + depth, depth
                    , randNum.nextInt(6) + 1, name, "X", itemX, itemY);
        }
        else if (createTrunkwareButton.isArmed()) {
            name = Names.nameMap.get("trunkware").get(randNum.nextInt(0,  Names.nameMap.get("trunkware").size()));
            item = new Trunkware(randNum.nextInt(11) + 1 + depth, depth
                    , randNum.nextInt(11) + 1, name, "X", itemX, itemY);
        }
        else if (createArmwareButton.isArmed()) {
            name = Names.nameMap.get("armware").get(randNum.nextInt(0,  Names.nameMap.get("armware").size()));
            item = new Armware(randNum.nextInt(4) + 1 + depth, depth
                    , randNum.nextInt(4) + 1, name, "X", itemX, itemY);
        }
        else if (createLegwareButton.isArmed()) {
            name = Names.nameMap.get("legware").get(randNum.nextInt(0,  Names.nameMap.get("legware").size()));
            item = new Legware(randNum.nextInt(8) + 1 + depth, depth
                    , randNum.nextInt(8) + 1, name, "X", itemX, itemY);
        }
        else if (createShieldButton.isArmed()) {
            name = Names.nameMap.get("shield").get(randNum.nextInt(0,  Names.nameMap.get("shield").size()));
            item = new Shield(randNum.nextInt(9) + 1 + depth, depth
                    , randNum.nextInt(9) + 1, name, "X", itemX, itemY);
        }
        else {
            name = Names.nameMap.get("weapon").get(randNum.nextInt(0,  Names.nameMap.get("weapon").size()));
            item = new Weapon(randNum.nextInt(10) + 1 + depth, depth
                    , randNum.nextInt(9) + 1, name, "Weapon", "X", itemX, itemY);
        }

        chest.getInventory().add(item);
        lclEntitiesInCellsList.get(item.getX()).get(item.getY()).add(item);
        itemList.add(item);
        itemListTableView.refresh();
        itemListTableView.scrollTo(item);
    }

    public void createTypeEnemy() {
        Enemy enemy;

        int x = 0, y = 0;
        MatrixPos mp = MatrixPos.values()[randNum.nextInt(4)];
        switch (mp) {
            case UP -> {
                // x = 0;
                y = randNum.nextInt(GameScreenController.gameBoardColumnCount);
            }
            case DOWN -> {
                x = GameScreenController.gameBoardRowCount - 1;
                y = randNum.nextInt(GameScreenController.gameBoardColumnCount);
            }
            case LEFT -> {
                x = randNum.nextInt(GameScreenController.gameBoardRowCount);
                // y = 0;
            }
            case RIGHT -> {
                x = randNum.nextInt(GameScreenController.gameBoardRowCount);
                y = GameScreenController.gameBoardColumnCount - 1;
            }
        }

        SEX sex = randNum.nextInt(2) == 0 ? SEX.MALE : SEX.FEMALE;
        String name;
        if (createHumanButton.isArmed()) {
            name = sex == SEX.MALE ? Names.nameMap.get("male_human").get(randNum.nextInt(Names.nameMap.get("male_human").size()))
                    : Names.nameMap.get("female_human").get(randNum.nextInt(Names.nameMap.get("female_human").size()));
            enemy = new HumanNPC(randNum.nextInt(20) + 30 , randNum.nextInt(50) + 30
                    , randNum.nextInt(100) + 35, randNum.nextInt(3), randNum.nextInt(11)
                    , randNum.nextInt(3) + 2, randNum.nextDouble(20) / 100
                    , randNum.nextInt(30) + 20, name, sex, "Human", "H"
                    , Integer.parseInt(GameScreenController.depthLevelString.get()), x, y);
            try {
                for(int j = 0; j < randNum.nextInt(7); ++j) {
                    Item item = GameScreenController.spawnItem(null);

                    enemy.pickUpItem(item);
                    item.setX(enemy.getX());
                    item.setY(enemy.getY());
                }
            }
            catch (OutOfGameGridException e) {
                logger.error(e.getMessage(), e);
            }
        }
        else if(createHoundButton.isArmed()) {
            name = sex == SEX.MALE ? Names.nameMap.get("male_dog").get(randNum.nextInt(Names.nameMap.get("male_dog").size()))
                    : Names.nameMap.get("female_dog").get(randNum.nextInt(Names.nameMap.get("female_dog").size()));
            enemy = new Hound(randNum.nextInt(5) + 3 , randNum.nextInt(5) + 3,
                    randNum.nextInt(7) + 9, randNum.nextInt(1), randNum.nextInt(11),
                    randNum.nextInt(3) + 3, randNum.nextDouble(20) / 100,
                    randNum.nextInt(12), name, sex, "Hound", "H",
                    Integer.parseInt(GameScreenController.depthLevelString.get()), x, y);
        }
        else {
            name = sex == SEX.MALE ?
                    Names.nameMap.get("male_rat").get(randNum.nextInt(Names.nameMap.get("male_rat").size())) :
                    Names.nameMap.get("female_rat").get(randNum.nextInt(Names.nameMap.get("female_rat").size()));
            enemy = new Rat(randNum.nextInt(5) + 3 , randNum.nextInt(5) + 3,
                    randNum.nextInt(7) + 6, randNum.nextInt(1), randNum.nextInt(11),
                    randNum.nextInt(2) + 1, randNum.nextDouble(20) / 100,
                    randNum.nextInt(8), name, sex, "Rat", "R",
                    Integer.parseInt(GameScreenController.depthLevelString.get()),  x, y);
        }
        enemy.setSpawnedPos(mp);
        
        enemyList.add(enemy);
        lclEntitiesInCellsList.get(enemy.getX()).get(enemy.getY()).add(enemy);
        updateEnemyLabels(enemy, LABEL_UPDATE_MODE.POPULATE);
        if(enemyLabelHBox.isDisabled()) {
            enemyLabelHBox.setDisable(false);
        }
        updateEnemyInvItems(enemy);
        // Using enemyListTableView.refresh() instead of using updateEnemyTableView(), for some reason, produces null
        //  as the return of the getSelectedItem() for the newly created enemy
        //enemyListTableView.refresh();
        updateEnemyTableView();
        enemyListTableView.getSelectionModel().select(enemy);
        enemyListTableView.scrollTo(enemy);
    }

    public void updateUsersTable() {
        List<List<String>> userList;
        try {
            //userList = FileIO.getListOfUsers();
            userList = Database.getListOfUsers();
        }
        catch (SQLException | IOException e) {
            userList = new ArrayList<>();
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Error");
        }
        userListTableView.setItems(FXCollections.observableList(userList));

        usernameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(0)));
        userRoleTableColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        userRoleTableColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        userRoleTableColumn.setCellFactory(cellData -> new TableCell<>() {
            private ChoiceBox<String> roleChangeChoiceBox;

            @Override
            protected void updateItem(List<String> userInf , boolean empty) {
                super.updateItem(userInf, empty);
                if (userInf == null) {
                    setGraphic(null);
                    return;
                }

                String userType = userInf.get(1);

                roleChangeChoiceBox = new ChoiceBox<>();
                roleChangeChoiceBox.getItems().addAll("Admin", "Basic");
                roleChangeChoiceBox.
                        setValue(userType.equalsIgnoreCase("Admin") ? "Admin" : "Basic");
                roleChangeChoiceBox.setPrefWidth(80);
                setGraphic(roleChangeChoiceBox);
                roleChangeChoiceBox.getSelectionModel().selectedItemProperty()
                        .addListener((observable, oldValue, newValue) -> {
                            if (!newValue.equalsIgnoreCase(userType)) {
                                if (dataChangeAccessIsGranted()) {
                                    String username = userInf.get(0);
                                    try {
                                        if (newValue.equalsIgnoreCase("Basic")) {
                                            FileIO.changeUserRoleNFreshlyReg(username, UserRole.NON_ADMIN,
                                                    true, false);
                                            Database.changeUserRole(username , UserRole.NON_ADMIN);
                                            userInf.set(1, "Basic");
                                        }
                                        else {
                                            FileIO.changeUserRoleNFreshlyReg(username, UserRole.ADMIN,
                                                    true, false);
                                            Database.changeUserRole(username , UserRole.ADMIN);
                                            userInf.set(1, "Admin");
                                        }
                                    }
                                    catch (SQLException | IOException e) {
                                        logger.error(e.getMessage(), e);
                                        showErrorAlert(e.getMessage(), "Failed to change the user role");
                                    }

                                    userListTableView.refresh();
                                }
                                else {
                                    roleChangeChoiceBox.
                                            setValue(userType.equalsIgnoreCase("Admin") ? "Admin" : "Basic");
                                }
                            }
                        });
            }
        });

        userDeleteTableColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        userDeleteTableColumn.setCellFactory(cellData -> new TableCell<>() {
            private final Button delButton = new Button("Delete");

            @Override
            protected void updateItem(List<String> userInf, boolean empty) {
                super.updateItem(userInf, empty);
                if (userInf == null) {
                    setGraphic(null);
                    return;
                }

                delButton.setMinWidth(80);
                setGraphic(delButton);

                delButton.setOnAction(event -> {
                    if (!dataChangeAccessIsGranted()) {
                        return;
                    }

                    try {
                        FileIO.deleteUser(userInf.get(0));
                        Database.deleteUser(userInf.get(0));
                    }
                    catch (SQLException | IOException e) {
                        logger.error(e.getMessage(), e);
                        showErrorAlert(e.getMessage(), "Failed to delete user '" + userInf.get(0) + "'");
                    }

                    if (userInf.get(0).equals(LoginController.getUserName())) {
                        MainController.replaceRootToLogin();
                    }

                    userListTableView.getItems().remove(userInf);
                });
            }
        });

    }

    public void onEnemyListRefreshButton() {
        enemyListTableView.setItems(FXCollections.observableList(enemyList));
        enemyListTableView.refresh();
    }
    public void onItemListRefreshButton() {
        itemListTableView.setItems(FXCollections.observableList(itemList));
        itemListTableView.refresh();
    }

    private void showErrorAlert(String message, String headerText) {
        baseStackPane.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        if (alert.showAndWait().isPresent()) {
            baseStackPane.setDisable(false);
        }
    }
    
    private void loadEnemyChangeToDB(StringBuilder stringBuilder) {
        LastEntityChangeThread insertEntChangeThread =
                new LastEntityChangeThread(Action.INSERT, stringBuilder);
        insertEntChangeThread.run();
    }
    
    private void loadEnemyChangeToFile(StringBuilder stringBuilder) {
        try {
            FileIO.saveEntChangesToFile(stringBuilder); 
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Error while loading enemy change to the file");
        }
    }

    public void onEnemyChangesButton() {
        MainController.replaceRootTAllEntityChangesView();
    }
}
