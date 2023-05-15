package main;

import entities.beings.*;
import entities.items.*;
import entities.EscapeHole;
import entities.other.Chest;
import entities.other.Names;
import exceptions.checked.OutOfGameGridException;
import exceptions.unchecked.DBObjectNotPresentException;
import exceptions.unchecked.UserDoesntExistException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import util.FileIO;
import util.thread.GameEngineThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.CustomSimpleStringProperty;
import util.Enums.SEX;
import util.database.Database;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class GameScreenController {
    private static final Logger logger = LoggerFactory.getLogger(GameScreenController.class);
    protected static volatile Random randNum = new Random();

    public static GameEngineThread gameEngineThread;
    public static ExecutorService executorService;

    public volatile static Player player;
    public volatile static List<Enemy> enemies;
    public volatile static Trader<Integer, Item> trader;
    public volatile static Chest<Item> chest;
    public volatile static Button[][] buttonArray;
    public volatile static List<List<List<Object>>> entitiesInCellsList;
    public volatile static EscapeHole escapeHole;
    public volatile static AtomicReference<String> depthLevelString = new AtomicReference<>();
    public volatile static int gameBoardRowCount, gameBoardColumnCount;


    @FXML
    protected VBox playerDeathVBox;
    @FXML
    BorderPane characterBorderPane;
    @FXML
    BorderPane guideBorderPane;
    @FXML
    BorderPane aboutBorderPane;
    @FXML
    VBox combatVBox;
    @FXML
    GridPane mainGridPane;
    @FXML
    protected GridPane gameBoardGridPane;
    @FXML
    protected StackPane stackPane;
    @FXML
    protected Label playerNameLabel;
    @FXML
    protected Label playerComScreenNameLabel;
    @FXML
    protected Label playerLevelLabel;
    @FXML
    protected Label playerComScreenLevelLabel;
    @FXML
    protected Label playerBaseHealthLabel;
    @FXML
    protected Label playerHealthLabel;
    @FXML
    protected Label playerComScreenHealthLabel;
    @FXML
    protected Label playerStaminaLabel;
    @FXML
    protected Label playerComScreenStamLabel;
    @FXML
    protected Label playerWeightLabel;
    @FXML
    protected Label playerArmourLabel;
    @FXML
    protected Label playerAttSpeedLabel;
    @FXML
    protected Label playerComScreenAttSpeedLabel;
    @FXML
    protected Label playerBaseAttDmgLabel;
    @FXML
    protected Label playerMedAttDmgLabel;
    @FXML
    protected Label playerComScreenMedAttDmgLabel;
    @FXML
    protected Label playerAccuracyLabel;
    @FXML
    protected Label playerInitiativeLabel;
    @FXML
    protected Label playerSexLabel;
    @FXML
    protected Label playerTypeLabel;
    @FXML
    protected Label playerSymoblLabel;
    @FXML
    protected Label playerKillCountLabel;
    @FXML
    protected ProgressBar playerTurnProgressBar;
    @FXML
    protected TableView<Item> playerInvItemsTableView;
    @FXML
    TableColumn<Item, String> playerInvItemNameTableColumn;
    @FXML
    TableColumn<Item, String> playerInvItemTypeTableColumn;
    @FXML
    TableColumn<Item, String> playerInvItemDefAttTableColumn;
    @FXML
    TableColumn<Item, String> playerInvItemWeightTableColumn;
    @FXML
    TableColumn<Item, String> playerInvItemLevelTableColumn;
    @FXML
    TableColumn<Item, Item> playerInvEquipButtonTableColumn;
    @FXML
    protected TableView<Item> playerEquippedItemsTableView;
    @FXML
    protected TableColumn<Item, String> playerEquippedItemDefAttTableColumn;
    @FXML
    protected TableColumn<Item, String> playerEquippedItemNameTableColumn;
    @FXML
    protected TableColumn<Item, String> playerEquippedItemLevelTableColumn;
    @FXML
    protected TableColumn<Item, String> playerEquippedItemWeightTableColumn;
    @FXML TextField filtPlInvItemsTextField;
    @FXML GridPane enemyCombatGridPane;
    @FXML
    protected ProgressBar enemyTurnProgressBar;
    @FXML
    protected Label enemyComScreenNameLabel;
    @FXML
    protected Label enemyComScreenHealthLabel;
    @FXML
    protected Label enemyComScreenStamLabel;
    @FXML
    protected Label enemyComScreenMedAttDmgLabel;
    @FXML
    protected Label enemyComScreenAttSpeedLabel;
    @FXML
    protected Label enemyComScreenLevelLabel;
    @FXML
    protected TableView<Item> enemyEquippedItemsTableView;
    @FXML
    protected TableColumn<Item, String> enemyEquippedItemNameTableColumn;
    @FXML
    protected TableColumn<Item, String> enemyEquippedItemDefAttTableColumn;
    @FXML
    protected TableColumn<Item, String> enemyEquippedItemWeightTableColumn;
    @FXML
    protected TableColumn<Item, String> enemyEquippedItemLevelTableColumn;
    @FXML
    protected Label depthLabel;
    @FXML
    protected TextArea gameTextArea;
    @FXML
    protected Button characterButton;
    @FXML
    protected Button guideButton;
    @FXML
    protected Button aboutButton;
    @FXML
    protected Button logOutButton;
    @FXML
    protected Button combSkipButton;
    @FXML
    protected Button newGameButton;
    @FXML
    protected Button entContrButton;




    @FXML
    public void initialize() {
        try {
            Names.initLoadFromDB();
        }
        catch (SQLException | IOException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Failed to load the names from the Database");
            return;
        }

        gameBoardRowCount = gameBoardGridPane.getColumnCount();
        gameBoardColumnCount = gameBoardGridPane.getRowCount();

        buttonArray = new Button[gameBoardRowCount][gameBoardColumnCount];
        for(int i = 0; i < buttonArray.length; ++i) {
            for (int j = 0; j < buttonArray[i].length; ++j) {
                buttonArray[i][j] = new Button("");
                buttonArray[i][j].setStyle("-fx-text-fill: silver; -fx-background-color: transparent; -fx-font-size: 17");
                buttonArray[i][j].setMinSize(40, 40);
                buttonArray[i][j].setMaxSize(40, 40);
                buttonArray[i][j].setTooltip(new Tooltip());
                buttonArray[i][j].getTooltip().setStyle("-fx-font-size: 14;");
                buttonArray[i][j].getTooltip().setShowDelay(Duration.ZERO);
                buttonArray[i][j].getTooltip().setShowDuration(Duration.ZERO);
                gameBoardGridPane.add(buttonArray[i][j], j, i);
                GridPane.setHalignment(buttonArray[i][j], HPos.CENTER);
                GridPane.setValignment(buttonArray[i][j], VPos.CENTER);
            }
        }

        if (!LoginController.isFreshlyRegistered) {
            try {
                /*Path path = Paths.get("IO_files/user_entities/" + LoginController.getUserName() + "_entities.dat");
                FileIO.loadObjectsFromFileToLocal(path);*/

                Database.loadObjectsFromDBToLocal();
            }
            catch (SQLException | IOException | DBObjectNotPresentException | UserDoesntExistException e) {
                logger.error(e.getMessage(), e);
                showErrorAlert(e.getMessage(), "Error with loading objects from the database/file");

                MainController.replaceRootToLogin();
            }

            player = Database.player;
            if (player.getHealth() <= 0) {
                playerDeath();
            }
            escapeHole = Database.escapeHole;
            depthLevelString.set(Database.depthString);
            enemies = Database.enemyList;
            trader = Database.trader;
            chest = Database.chest;

            EntitiesInCellsListControl(false, false, true, true);
        }
        else {
            spawnEscapeHole();
            depthLevelString.set("0");
            try {
                trader = spawnTrader();
                chest = spawnChest();
                player = spawnPlayer();
            }
            catch (OutOfGameGridException | SQLException | IOException |
                   UserDoesntExistException e) {
                logger.error(e.getMessage(), e);
                showErrorAlert(e.getMessage(), "Error");

                return;
            }

            enemies = new ArrayList<>(gameBoardRowCount * gameBoardColumnCount);

            EntitiesInCellsListControl(false, false, false, true);
        }

        depthLabel.setText(depthLevelString.get());
        playerKillCountLabel.setText(Integer.toString(player.getKillCount()));

        resetBoardButtonText();
        updateButtonTooltip();
        showPlayerEquippedItems();
        initPlayerInventoryItems();
        updateCombatScreen(player, true);
        combSkipButton.setDisable(true);

        gameEngineThread = new GameEngineThread(depthLabel, gameTextArea, playerTurnProgressBar, enemyTurnProgressBar,
                enemyComScreenNameLabel, playerKillCountLabel, enemyEquippedItemNameTableColumn,
                enemyEquippedItemLevelTableColumn, enemyEquippedItemDefAttTableColumn, enemyEquippedItemWeightTableColumn,
                enemyEquippedItemsTableView, playerEquippedItemNameTableColumn, playerEquippedItemLevelTableColumn,
                playerEquippedItemDefAttTableColumn, playerEquippedItemWeightTableColumn, playerEquippedItemsTableView,
                playerComScreenNameLabel, playerComScreenHealthLabel, playerComScreenStamLabel, playerComScreenMedAttDmgLabel,
                playerComScreenAttSpeedLabel, playerComScreenLevelLabel, enemyComScreenHealthLabel, enemyComScreenStamLabel,
                enemyComScreenMedAttDmgLabel, enemyComScreenAttSpeedLabel, enemyComScreenLevelLabel, playerNameLabel,
                playerLevelLabel, playerBaseHealthLabel, playerHealthLabel, playerStaminaLabel, playerWeightLabel,
                playerArmourLabel, playerAttSpeedLabel, playerBaseAttDmgLabel, playerMedAttDmgLabel, playerAccuracyLabel,
                playerInitiativeLabel, playerSymoblLabel, playerSexLabel, playerTypeLabel, stackPane, gameBoardGridPane,
                characterButton, aboutButton, guideButton, logOutButton, playerDeathVBox, combSkipButton, newGameButton,
                entContrButton, playerInvItemsTableView);
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(gameEngineThread);
    }

    public void onKeyPressed(KeyEvent ke) {
        switch(ke.getCode()) {
            case W, S, A, D, C -> {
                gameEngineThread.ke = ke;
                gameEngineThread.runCycle.set(true);
            }
        }
    }

    public void onCombSkipButtonPress() {
        GameEngineThread.skipCombat.set(true);
    }

    public void onCharacterButtonPress() {
        characterBorderPane.toFront();
        characterBorderPane.setPickOnBounds(false);
        updatePlayerStats();
        playerInvItemsTableView.setItems(FXCollections.observableList(player.getInventoryList()));
    }

    public void onEntityControlButtonPress() {
        try {
            Path path = Paths.get("IO_files/user_entities/" + LoginController.getUserName() + "_entities.dat");
            FileIO.saveObjectsToFile(path, entitiesInCellsList, escapeHole, depthLevelString.get(),
                    enemies, player, trader, chest);

            Database.executeFlushAndSave(player, enemies, trader, chest, escapeHole, depthLevelString.get());
        }
        catch (SQLException | IOException | UserDoesntExistException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Error");


            MainController.replaceRootToLogin();
        }

        executorServiceShutdown();
        MainController.replaceRootToEntityControl();
    }

    public void onGuideButtonPress() {
        guideBorderPane.toFront();
        guideBorderPane.setPickOnBounds(false);
    }
    public void onAboutButtonPress() {
        aboutBorderPane.toFront();
        aboutBorderPane.setPickOnBounds(false);
    }
    public void onLogOutButtonPress() {
        try {
            Path path = Paths.get("IO_files/user_entities/" + LoginController.getUserName() + "_entities.dat");
            FileIO.saveObjectsToFile(path, entitiesInCellsList, escapeHole, depthLevelString.get(),
                    enemies, player, trader, chest);

            Database.executeFlushAndSave(player, enemies, trader, chest, escapeHole, depthLevelString.get());
        }
        catch (SQLException | IOException | UserDoesntExistException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Error");
        }

        executorServiceShutdown();
        MainController.replaceRootToLogin();
    }

    public void onNewGameButtonPress() {
        if (player.getHealth() <= 0) {
            deathNewGame();

            return;
        }
        if (newGameIsGranted()) {
            resetGame();
        }
    }

    public void onDeathNewGameButtonPress() {
        deathNewGame();
    }

    public void deathNewGame() {
        resetGame();

        gameBoardGridPane.setDisable(false);
        characterButton.setDisable(false);
        aboutButton.setDisable(false);
        guideButton.setDisable(false);
        logOutButton.setDisable(false);
        gameTextArea.clear();
        combatVBox.toFront();
        stackPane.setOnKeyPressed(this::onKeyPressed);
    }

    public boolean newGameIsGranted() {
        stackPane.setDisable(true);
        Alert confirmWind = new Alert(Alert.AlertType.CONFIRMATION);
        confirmWind.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        confirmWind.initOwner(MainApplication.mainStage);
        confirmWind.initStyle(StageStyle.UNDECORATED);
        confirmWind.setGraphic(null);
        confirmWind.setHeaderText("Are you sure You want to start a new game?");
        Optional<ButtonType> result = confirmWind.showAndWait();
        if (result.isPresent()) {
            stackPane.setDisable(false);
        }
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public void resetGame() {
        try {
            Database.bufferDelItemIds(player.getInventoryList());

            depthLevelString.set("0");
            depthLabel.setText(depthLevelString.get());

            player = spawnPlayer();
            resetEscapeHole();
            resetChest();
            resetTrader();
            enemiesDelete();

            entitiesInCellsList.forEach(entities -> entities.forEach(List::clear));
            EntitiesInCellsListControl(false, false, false, true);
            resetBoardButtonText();
            updateButtonTooltip();
            updatePlayerStats();
            playerInvItemsTableView.setItems(FXCollections.observableList(player.getInventoryList()));
            resetAllCombatScreens();
            showPlayerEquippedItems();
            showEnemyEquippedItems(null);
        }
        catch (SQLException | IOException | OutOfGameGridException | UserDoesntExistException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Error: on reset game");

            executorServiceShutdown();
            MainController.replaceRootToLogin();
        }
    }

    public void onCloseButtonPress() {
        combatVBox.toFront();
    }

    public static Item spawnItem(Integer level) throws OutOfGameGridException {
        int depth = level != null ? level : Integer.parseInt(depthLevelString.get());

        int itemX, itemY;
        while((itemX = randNum.nextInt(gameBoardRowCount)) == gameBoardRowCount / 2);
        while((itemY = randNum.nextInt(gameBoardColumnCount)) == gameBoardColumnCount / 2);

        if (itemX < 0 || itemX >= gameBoardRowCount || itemY < 0 || itemY >= gameBoardColumnCount) {
            throw new OutOfGameGridException("Item was about to be spawned out of the playable area");
        }

        int randomItemType = randNum.nextInt(6);
        String name = switch (randomItemType) {
            case 0 -> Names.nameMap.get("armware").get(randNum.nextInt(0,  Names.nameMap.get("armware").size()));
            case 1 -> Names.nameMap.get("headware").get(randNum.nextInt(0,  Names.nameMap.get("headware").size()));
            case 2 -> Names.nameMap.get("legware").get(randNum.nextInt(0,  Names.nameMap.get("legware").size()));
            case 3 -> Names.nameMap.get("trunkware").get(randNum.nextInt(0,  Names.nameMap.get("trunkware").size()));
            case 4 -> Names.nameMap.get("shield").get(randNum.nextInt(0,  Names.nameMap.get("shield").size()));
            default -> Names.nameMap.get("weapon").get(randNum.nextInt(0,  Names.nameMap.get("weapon").size()));
        };

        return switch (randomItemType) {
            case 0 -> new Armware(randNum.nextInt(4) + 1 + depth, depth
                    , randNum.nextInt(4) + 1, name, "X", itemX, itemY);
            case 1 -> new Headware(randNum.nextInt(6) + 1 + depth, depth
                    , randNum.nextInt(6) + 1, name, "X", itemX, itemY);
            case 2 -> new Legware(randNum.nextInt(8) + 1 + depth, depth
                    , randNum.nextInt(8) + 1, name, "X", itemX, itemY);
            case 3 -> new Trunkware(randNum.nextInt(11) + 1 + depth, depth
                    , randNum.nextInt(11) + 1, name, "X", itemX, itemY);
            case 4 -> new Shield(randNum.nextInt(9) + 1 + depth, depth
                    , randNum.nextInt(9) + 1, name, "X", itemX, itemY);
            default -> new Weapon(randNum.nextInt(10) + 1 + depth, depth
                    , randNum.nextInt(9) + 1, name, "Weapon", "X", itemX, itemY);
        };
    }

    public void spawnEscapeHole() {
        int x, y;
        while((x = randNum.nextInt(gameBoardRowCount)) == gameBoardRowCount / 2);
        while((y = randNum.nextInt(gameBoardColumnCount)) == gameBoardColumnCount / 2);

        escapeHole = new EscapeHole.Builder(x, y)
                .withSymbol("\uD83D\uDD73")
                .build();
    }

    public void resetAllCombatScreens() {
        playerComScreenNameLabel.setText(player.getName());
        playerComScreenHealthLabel.setText(Integer.toString(player.getHealth()));
        playerComScreenStamLabel.setText(Integer.toString(player.getStamina()));
        playerComScreenMedAttDmgLabel.setText(Integer.toString(player.getMedianAttDmg()));;
        playerComScreenAttSpeedLabel.setText(Integer.toString(player.getAttackSpeed()));
        playerComScreenLevelLabel.setText(Integer.toString(player.getLevel()));
        playerTurnProgressBar.setProgress(0);
        enemyComScreenNameLabel.setText("");
        enemyComScreenHealthLabel.setText("");
        enemyComScreenStamLabel.setText("");
        enemyComScreenMedAttDmgLabel.setText("");
        enemyComScreenAttSpeedLabel.setText("");
        enemyComScreenLevelLabel.setText("");
        enemyTurnProgressBar.setProgress(0);
    }

    public void initPlayerInventoryItems() {
        playerInvItemNameTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        playerInvItemTypeTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
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
                    if(item.isEquipped()) {
                        player.unEquipItem(item);
                        equipUnequipButton.setText("Equip");
                    }
                    else {
                        player.getEquippedItemsList().stream()
                                .filter(param -> param.getClass().equals(item.getClass()))
                                .findFirst().ifPresent(value -> player.unEquipItem(value));
                        player.equipItem(item);
                        equipUnequipButton.setText("Equip");
                    }

                    playerInvItemsTableView
                            .getColumns().get(playerInvItemsTableView.getColumns().size() - 1).setVisible(false);
                    playerInvItemsTableView
                            .getColumns().get(playerInvItemsTableView.getColumns().size() - 1).setVisible(true);
                    updatePlayerStats();
                    updateCombatScreen(player, true);
                    showPlayerEquippedItems();
                });
            }
        });

        playerInvItemsTableView.setItems(FXCollections.observableList(player.getInventoryList()));
    }

    public void filterPlayerInvItems() {
        playerInvItemsTableView.setItems(FXCollections.observableList(player.getInventoryList().stream()
                .filter(item -> item.getType().toLowerCase().contains(filtPlInvItemsTextField.getText().toLowerCase()))
                .toList()
        ));
    }

    public Player spawnPlayer() throws OutOfGameGridException, SQLException, IOException, UserDoesntExistException {
        int x = gameBoardColumnCount / 2, y = gameBoardRowCount / 2;
        if (x < 0 || x >= gameBoardRowCount || y < 0 || y >= gameBoardColumnCount) {
            throw new OutOfGameGridException("The player was about to be spawned out of the playable area");
        }

        long oldId = -1;
        if (!LoginController.isFreshlyRegistered) {
            oldId = player.getId();
        }

        Player player = new Player(randNum.nextInt(20) + 50 , randNum.nextInt(50) + 30
                , randNum.nextInt(100) + 35, randNum.nextInt(3), randNum.nextInt(11)
                , randNum.nextInt(3) + 2, (randNum.nextDouble(45) + 35) / 100,
                randNum.nextInt(10), LoginController.getUserName(), SEX.MALE, "Human", "P",
                1, x, y);

        if (!LoginController.isFreshlyRegistered) {
            player.setId(oldId);
        }

        if (LoginController.getUserName().equalsIgnoreCase("alexander radovan")) {
            player.pickUpItem(new Armware(1905, 0
                    , 0, "Chelsea' captains armband", "X"
                    , player.getX(), player.getY()));
        }

        return player;
    }

    public Trader<Integer, Item> spawnTrader() throws OutOfGameGridException {
        int x, y;
        while((x = randNum.nextInt(gameBoardRowCount)) == gameBoardRowCount / 2);
        while((y = randNum.nextInt(gameBoardColumnCount)) == gameBoardColumnCount / 2);

        Map<Integer, Item> map = new HashMap<>();
        if (depthLevelString.equals("0")) {
            map.put(0, spawnItem(Integer.parseInt(depthLevelString.get()) + 5));
            map.get(0).setOwner(trader);
        }
        for(int i = 0; i < randNum.nextInt(7); ++i) {
            int value = randNum.nextInt(11) + 1;
            map.put(value, spawnItem(Integer.parseInt(depthLevelString.get()) + 5));
            map.get(value).setOwner(trader);
        }

        return new Trader<>(randNum.nextInt(501) + 500, randNum.nextInt(501) + 500,
                randNum.nextInt(40) + 20, randNum.nextInt(501) + 500,
                randNum.nextInt(45) + 50, randNum.nextInt(175) + 50,
                (randNum.nextDouble(50) + 40) / 100, randNum.nextInt(501) + 500,
                "\uD80C\uDE34\uD80C\uDE43\uD80C\uDE3E\uD80C\uDE21", SEX.UNKNOWN, "Trader", "T",
                randNum.nextInt(501) + 500, x, y, map);
    }

    public Chest<Item> spawnChest() {
        int x, y;
        while((x = randNum.nextInt(gameBoardRowCount)) == gameBoardRowCount / 2);
        while((y = randNum.nextInt(gameBoardColumnCount)) == gameBoardColumnCount / 2);

        List<Item> itemList = new ArrayList<>();
        try {
            for(int i = 0; i < randNum.nextInt(1) + 1; ++i) {
                itemList.add(spawnItem(Integer.parseInt(depthLevelString.get()) + 2));
            }
        }
        catch (OutOfGameGridException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Error");
        }

        return new Chest<>("Chest", "Chest", "C", x, y, itemList);
    }

    public void playerDeath() {
        stackPane.setOnKeyPressed(key -> {});
        gameBoardGridPane.setDisable(true);
        characterButton.setDisable(true);
        aboutButton.setDisable(true);
        guideButton.setDisable(true);
        logOutButton.setDisable(true);
        playerDeathVBox.toFront();
    }

    public void EntitiesInCellsListControl(boolean skipBaseInitialize,boolean clear,
                                           boolean addEnemies,boolean addNonEnemies) {
        if (!skipBaseInitialize) {
            entitiesInCellsList = new ArrayList<>(gameBoardRowCount);
            for(int i = 0; i < gameBoardRowCount; ++i) {
                entitiesInCellsList.add(new ArrayList<>(gameBoardColumnCount));
                for(int j = 0; j < gameBoardColumnCount; ++j) {
                    entitiesInCellsList.get(i).add(new ArrayList<>());
                }
            }
        }
        if (clear) {
            for(List<List<Object>> entitiesX: entitiesInCellsList) {
                for(List<Object> entitiesY: entitiesX) {
                    entitiesY.clear();
                }
            }
        }
        if (addNonEnemies) {
            entitiesInCellsList.get(player.getX()).get(player.getY()).add(player);
            entitiesInCellsList.get(escapeHole.getX()).get(escapeHole.getY()).add(escapeHole);
            if (!chest.isLooted()) {
                entitiesInCellsList.get(chest.getX()).get(chest.getY()).add(chest);
            }
            if (!trader.hasDoneExchange()) {
                entitiesInCellsList.get(trader.getX()).get(trader.getY()).add(trader);
            }
        }
        if (addEnemies) {
            enemies.forEach(enemy -> entitiesInCellsList.get(enemy.getX()).get(enemy.getY()).add(enemy));
        }
    }

    private void showErrorAlert(String message, String headerText) {
        stackPane.setDisable(true);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getDialogPane().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("general_style.css")).toExternalForm());
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        if (alert.showAndWait().isPresent()) {
            stackPane.setDisable(false);
        }

        executorServiceShutdown();
        MainController.replaceRootToLogin();
    }

    public void updateButtonTooltip() {
        StringBuilder tmpString = new StringBuilder();

        for (int i = 0; i < entitiesInCellsList.size(); ++i) {
            for (int j = 0; j < entitiesInCellsList.get(i).size(); ++j) {
                if (entitiesInCellsList.get(i).get(j).size() == 0) {
                    buttonArray[i][j].getTooltip().setShowDuration(Duration.ZERO);
                } else {
                    tmpString.setLength(0);
                    for (int k = 0; k < entitiesInCellsList.get(i).get(j).size(); ++k) {
                        if (entitiesInCellsList.get(i).get(j).get(k) instanceof Enemy) {
                            tmpString.append(((Enemy) entitiesInCellsList.get(i).get(j).get(k)).getSymbol()).append(" ")
                                    .append(((Enemy) entitiesInCellsList.get(i).get(j).get(k)).getType()).append(" ")
                                    .append(((Enemy) entitiesInCellsList.get(i).get(j).get(k)).getName()).append("\n");
                        } else if (entitiesInCellsList.get(i).get(j).get(k) instanceof Player) {
                            tmpString.append(((Player) entitiesInCellsList.get(i).get(j).get(k)).getName()).append("\n");
                        } else if (entitiesInCellsList.get(i).get(j).get(k) instanceof Item) {
                            tmpString.append(((Item) entitiesInCellsList.get(i).get(j).get(k)).getType()).append("\n");
                        } else if (entitiesInCellsList.get(i).get(j).get(k) instanceof Trader<?, ?>) {
                            tmpString.append(((Trader<?, ?>) entitiesInCellsList.get(i).get(j).get(k)).getType()).append("\n");
                        } else if (entitiesInCellsList.get(i).get(j).get(k) instanceof Chest<?>) {
                            tmpString.append(((Chest<?>) entitiesInCellsList.get(i).get(j).get(k)).getType()).append("\n");
                        } else {
                            tmpString.append(((EscapeHole) entitiesInCellsList.get(i).get(j).get(k)).getSymbol()).append("\n");
                        }
                    }

                    buttonArray[i][j].getTooltip().setText(tmpString.toString());
                    buttonArray[i][j].getTooltip().setShowDuration(Duration.INDEFINITE);
                }
            }
        }
    }

    public void updatePlayerButtonText() {
        buttonArray[player.getX()][player.getY()].setText(player.getSymbol());
        buttonArray[player.getX()][player.getY()]
                .setStyle("-fx-background-color: transparent; -fx-text-fill: green;");
    }
    public void updateEscapeHoleButtonText() {
        buttonArray[escapeHole.getX()][escapeHole.getY()].setText(escapeHole.getSymbol());
        buttonArray[escapeHole.getX()][escapeHole.getY()]
                .setStyle("-fx-background-color: transparent; -fx-text-fill: silver;");
    }
    public void updateTraderButtonText() {
        buttonArray[trader.getX()][trader.getY()].setText(trader.getSymbol());
        buttonArray[trader.getX()][trader.getY()]
                .setStyle("-fx-background-color: transparent; -fx-text-fill: Gold;");
    }
    public void updateChestButtonText() {
        buttonArray[chest.getX()][chest.getY()].setText(chest.getSymbol());
        buttonArray[chest.getX()][chest.getY()]
                .setStyle("-fx-background-color: transparent; -fx-text-fill: Gold;");
    }

    public void updateEnemyButtonText(Enemy en) {
        buttonArray[en.getX()][en.getY()].setText((en.getSymbol()));
        buttonArray[en.getX()][en.getY()]
                .setStyle("-fx-background-color: transparent; -fx-text-fill: red;");
    }

    public void resetBoardButtonText() {
        for (Button[] buttons : buttonArray) {
            for (Button button : buttons) {
                button.setText("");
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: silver;");
            }
        }

        if (!chest.isLooted()) {
            updateChestButtonText();
        }
        if (Integer.parseInt(depthLevelString.get()) % 5 == 0 && !trader.hasDoneExchange()) {
            updateTraderButtonText();
        }
        for(int i = 0; i < entitiesInCellsList.size(); ++i) {
            for (int j = 0; j < entitiesInCellsList.get(i).size(); ++j) {
                if (entitiesInCellsList.get(i).get(j).size() > 0) {
                    Object entity = entitiesInCellsList.get(i).get(j).get(0);

                    if (entity instanceof EscapeHole) {
                        updateEscapeHoleButtonText();
                    }
                    else if (entity instanceof Enemy) {
                        updateEnemyButtonText((Enemy)entity);
                    }
                    else if (entity instanceof Player) {
                        updatePlayerButtonText();
                    }
                }
            }
        }
    }

    public void updateCombatScreen(Being being, boolean updateEqItemTableView) {
        if (being instanceof Player) {
            playerComScreenNameLabel.setText(being.getName());
            playerComScreenHealthLabel.setText(Integer.toString(being.getHealth()));
            playerComScreenStamLabel.setText(Integer.toString(being.getStamina()));
            playerComScreenMedAttDmgLabel.setText(Integer.toString(being.getMedianAttDmg()));
            playerComScreenAttSpeedLabel.setText(Integer.toString(being.getAttackSpeed()));
            playerComScreenLevelLabel.setText(Integer.toString(being.getLevel()));
            if (updateEqItemTableView) {
                showPlayerEquippedItems();
            }
        }
        else {
            enemyComScreenNameLabel.setText(being.getName());
            enemyComScreenHealthLabel.setText(Integer.toString(being.getHealth()));
            enemyComScreenStamLabel.setText(Integer.toString(being.getStamina()));
            enemyComScreenMedAttDmgLabel.setText(Integer.toString(being.getMedianAttDmg()));
            enemyComScreenAttSpeedLabel.setText(Integer.toString(being.getAttackSpeed()));
            enemyComScreenLevelLabel.setText(Integer.toString(being.getLevel()));
            if (updateEqItemTableView) {
                showEnemyEquippedItems((Enemy)being);
            }
        }
    }

    public void showPlayerEquippedItems() {
        playerEquippedItemNameTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        playerEquippedItemWeightTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getWeight())));
        playerEquippedItemDefAttTableColumn
                .setCellValueFactory(cellData -> CustomSimpleStringProperty.simpleStrPropertyDefAtt(cellData.getValue()));
        playerEquippedItemLevelTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getLevel())));
        playerEquippedItemsTableView.setItems(FXCollections.observableList(player.getEquippedItemsList()));
    }

    public void showEnemyEquippedItems(Enemy enemy) {
        enemyEquippedItemNameTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getType()));
        enemyEquippedItemLevelTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getLevel())));
        enemyEquippedItemDefAttTableColumn
                .setCellValueFactory(cellData -> CustomSimpleStringProperty.simpleStrPropertyDefAtt(cellData.getValue()));
        enemyEquippedItemWeightTableColumn
                .setCellValueFactory(cellData -> new SimpleStringProperty(Integer.toString(cellData.getValue().getWeight())));
        enemyEquippedItemsTableView.setItems(FXCollections.observableList(enemy == null ? new ArrayList<>() : enemy.getEquippedItemsList()));
    }

    public void updatePlayerStats() {
        playerNameLabel.setText(player.getName());
        playerLevelLabel.setText(Integer.toString(player.getLevel()));
        playerBaseHealthLabel.setText(Integer.toString(player.getBaseHealth()));
        playerHealthLabel.setText(Integer.toString(player.getHealth()));
        playerStaminaLabel.setText(Integer.toString(player.getStamina()));
        playerWeightLabel.setText(Integer.toString(player.getWeight()));
        playerArmourLabel.setText(Integer.toString(player.getArmour()));
        playerAttSpeedLabel.setText(Integer.toString(player.getAttackSpeed()));
        playerBaseAttDmgLabel.setText(Integer.toString(player.getBaseAttDmg()));
        playerMedAttDmgLabel.setText(Integer.toString(player.getMedianAttDmg()));
        playerAccuracyLabel.setText(Double.toString(player.getAccuracy()));
        playerInitiativeLabel.setText(Integer.toString(player.getInitiative()));
        playerSymoblLabel.setText(player.getSymbol());
        playerSexLabel.setText(player.getSex().toString());
        playerTypeLabel.setText(player.getType());
    }

    public void resetTrader() {
        int x, y;
        while((x = randNum.nextInt(gameBoardRowCount)) == gameBoardRowCount / 2);
        while((y = randNum.nextInt(gameBoardColumnCount)) == gameBoardColumnCount / 2);
        trader.setX(x);
        trader.setY(y);
        trader.setHasDoneExchange(false);

        Database.bufferDelItemIds(new ArrayList<>(trader.getInventory().values()));
        trader.getInventory().clear();
        try {
            int value;
            if (depthLevelString.equals("0")) {
                trader.getInventory().put(0, spawnItem(Integer.parseInt(depthLevelString.get()) + 5));
                trader.getInventory().get(0).setOwner(trader);
            }
            for(int i = 0; i < randNum.nextInt(7); ++i) {
                value = randNum.nextInt(11) + 1;
                trader.getInventory().put(value,
                        GameScreenController.spawnItem(Integer.parseInt(depthLevelString.get()) + 5));
                trader.getInventory().get(value).setOwner(trader);
            }
        }
        catch (OutOfGameGridException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Error while resetting the trader");

            executorServiceShutdown();
            //MainController.replaceRootToLogin();
            MainController.toStageLogin();
        }
    }

    public void resetChest()  {
        int x, y;
        while((x = randNum.nextInt(gameBoardRowCount)) == gameBoardRowCount / 2);
        while((y = randNum.nextInt(gameBoardColumnCount)) == gameBoardColumnCount / 2);
        chest.setX(x);
        chest.setY(y);
        chest.setLooted(false);

        try {
            Database.bufferDelItemIds(chest.getInventory());
            chest.getInventory().clear();
            for(int i = 0; i < randNum.nextInt(1) + 1; ++i) {
                chest.getInventory().add(spawnItem(Integer.parseInt(depthLevelString.get()) + 2));
                chest.getInventory().get(i).setOwner(chest);
            }
        } catch (OutOfGameGridException ex) {
            logger.error(ex.getMessage(), ex);
            showErrorAlert(ex.getMessage(), "Error while resetting the chest");
        }
    }

    public void resetEscapeHole() {
        int x, y;
        while((x = randNum.nextInt(gameBoardRowCount)) == gameBoardRowCount / 2);
        while((y = randNum.nextInt(gameBoardColumnCount)) == gameBoardColumnCount / 2);
        escapeHole.setX(x);
        escapeHole.setY(y);
    }

    public void enemiesDelete() {
        Database.bufferDelEnemyIds(enemies);
        enemies = new ArrayList<>(gameBoardRowCount * gameBoardColumnCount);
    }

    public void executorServiceShutdown() {
        try {
            gameEngineThread.toBeShutDown = true;
            executorService.shutdownNow();
            executorService.awaitTermination(1, TimeUnit.NANOSECONDS);
        }
        catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert(e.getMessage(), "Threads shutdown unsuccessful");
        }
    }
}
