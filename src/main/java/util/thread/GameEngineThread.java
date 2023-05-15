package util.thread;

import entities.beings.*;
import entities.items.Item;
import entities.other.Names;
import exceptions.checked.OutOfGameGridException;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import main.GameScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Enums;
import util.MethodsHelper;
import util.MutableInt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameEngineThread extends GameScreenController implements Runnable {
    public static AtomicBoolean inCombat = new AtomicBoolean(false);
    public static AtomicBoolean skipCombat = new AtomicBoolean(false);
    public boolean toBeShutDown = false;

    Logger logger = LoggerFactory.getLogger(GameEngineThread.class);

    public AtomicBoolean runCycle = new AtomicBoolean(false);
    public KeyEvent ke;

    public GameEngineThread(Label depthLabel, TextArea gameTextArea, ProgressBar playerTurnProgressBar,
            ProgressBar enemyTurnProgressBar, Label enemyComScreenNameLabel, Label playerKillCountLabel, TableColumn<Item,
            String> enemyEquippedItemNameTableColumn, TableColumn<Item, String> enemyEquippedItemLevelTableColumn,
            TableColumn<Item, String> enemyEquippedItemDefAttTableColumn,
            TableColumn<Item, String> enemyEquippedItemWeightTableColumn, TableView<Item> enemyEquippedItemsTableView,
            TableColumn<Item, String> playerEquippedItemNameTableColumn,
            TableColumn<Item, String> playerEquippedItemLevelTableColumn,
            TableColumn<Item, String> playerEquippedItemDefAttTableColumn, TableColumn<Item,
            String> playerEquippedItemWeightTableColumn, TableView<Item> playerEquippedItemsTableView,
            Label playerComScreenNameLabel, Label playerComScreenHealthLabel, Label playerComScreenStamLabel,
            Label playerComScreenMedAttDmgLabel, Label playerComScreenAttSpeedLabel, Label playerComScreenLevelLabel,
            Label enemyComScreenHealthLabel, Label enemyComScreenStamLabel, Label enemyComScreenMedAttDmgLabel,
            Label enemyComScreenAttSpeedLabel, Label enemyComScreenLevelLabel, Label playerNameLabel,
            Label playerLevelLabel, Label playerBaseHealthLabel, Label playerHealthLabel, Label playerStaminaLabel,
            Label playerWeightLabel, Label playerArmourLabel, Label playerAttSpeedLabel, Label playerBaseAttDmgLabel,
            Label playerMedAttDmgLabel, Label playerAccuracyLabel, Label playerInitiativeLabel, Label playerSymoblLabel,
            Label playerSexLabel, Label playerTypeLabel, StackPane stackPane, GridPane gameBoardGridPane,
            Button characterButton, Button aboutButton, Button guideButton, Button logOutButton, VBox playerDeathVBox,
            Button combSkipButton, Button newGameButton,Button entContrButton,  TableView<Item> playerInvItemsTableView) {
        this.depthLabel = depthLabel;
        this.gameTextArea = gameTextArea;
        this.playerTurnProgressBar = playerTurnProgressBar;
        this.enemyTurnProgressBar = enemyTurnProgressBar;
        this.enemyComScreenNameLabel = enemyComScreenNameLabel;
        this.playerKillCountLabel = playerKillCountLabel;
        this.enemyEquippedItemNameTableColumn = enemyEquippedItemNameTableColumn;
        this.enemyEquippedItemLevelTableColumn = enemyEquippedItemLevelTableColumn;
        this.enemyEquippedItemDefAttTableColumn = enemyEquippedItemDefAttTableColumn;
        this.enemyEquippedItemWeightTableColumn = enemyEquippedItemWeightTableColumn;
        this.enemyEquippedItemsTableView = enemyEquippedItemsTableView;
        this.playerEquippedItemNameTableColumn = playerEquippedItemNameTableColumn;
        this.playerEquippedItemLevelTableColumn = playerEquippedItemLevelTableColumn;
        this.playerEquippedItemDefAttTableColumn = playerEquippedItemDefAttTableColumn;
        this.playerEquippedItemWeightTableColumn = playerEquippedItemWeightTableColumn;
        this.playerEquippedItemsTableView = playerEquippedItemsTableView;
        this.playerComScreenNameLabel = playerComScreenNameLabel;
        this.playerComScreenHealthLabel = playerComScreenHealthLabel;
        this.playerComScreenStamLabel = playerComScreenStamLabel;
        this.playerComScreenMedAttDmgLabel = playerComScreenMedAttDmgLabel;
        this.playerComScreenAttSpeedLabel = playerComScreenAttSpeedLabel;
        this.playerComScreenLevelLabel = playerComScreenLevelLabel;
        this.enemyComScreenHealthLabel = enemyComScreenHealthLabel;
        this.enemyComScreenStamLabel = enemyComScreenStamLabel;
        this.enemyComScreenMedAttDmgLabel = enemyComScreenMedAttDmgLabel;
        this.enemyComScreenAttSpeedLabel = enemyComScreenAttSpeedLabel;
        this.enemyComScreenLevelLabel = enemyComScreenLevelLabel;
        this.playerNameLabel = playerNameLabel;
        this.playerLevelLabel = playerLevelLabel;
        this.playerBaseHealthLabel = playerBaseHealthLabel;
        this.playerHealthLabel = playerHealthLabel;
        this.playerStaminaLabel = playerStaminaLabel;
        this.playerWeightLabel = playerWeightLabel;
        this.playerArmourLabel = playerArmourLabel;
        this.playerAttSpeedLabel = playerAttSpeedLabel;
        this.playerBaseAttDmgLabel = playerBaseAttDmgLabel;
        this.playerMedAttDmgLabel = playerMedAttDmgLabel;
        this.playerAccuracyLabel = playerAccuracyLabel;
        this.playerInitiativeLabel = playerInitiativeLabel;
        this.playerSymoblLabel = playerSymoblLabel;
        this.playerSexLabel = playerSexLabel;
        this.playerTypeLabel = playerTypeLabel;
        this.playerInvItemsTableView = playerInvItemsTableView;
        this.stackPane = stackPane;
        this.gameBoardGridPane = gameBoardGridPane;
        this.characterButton = characterButton;
        this.aboutButton = aboutButton;
        this.guideButton = guideButton;
        this.logOutButton = logOutButton;
        this.playerDeathVBox = playerDeathVBox;
        this.combSkipButton = combSkipButton;
        this.newGameButton = newGameButton;
        this.entContrButton = entContrButton;
    }


    @Override
    public void run() {
        try {
            while(true) {
                if (runCycle.get()) {
                    Platform.runLater(() -> {
                        newGameButton.setDisable(true);
                        entContrButton.setDisable(true);
                        logOutButton.setDisable(true);
                    });
                    mainLogic(ke);
                    runCycle.set(false);
                    Platform.runLater(() -> {
                        newGameButton.setDisable(false);
                        entContrButton.setDisable(false);
                        logOutButton.setDisable(false);
                    });
                }

                Thread.sleep(50);
            }
        } catch (InterruptedException e) {
            if (!toBeShutDown) {
                logger.error(e.getMessage(), "GameEngine thread error");
                showErrorAlert("GameEngine thread error");
            }
        }
    }


    public <T> void mainLogic(T event) throws InterruptedException {
        if(((KeyEvent)event).getCode() != KeyCode.C) {
            Platform.runLater(() -> {
                buttonArray[player.getX()][player.getY()].setText("");
                buttonArray[player.getX()][player.getY()]
                        .setStyle("-fx-background-color: transparent; -fx-text-fill: silver;");
            });
            ThreadHelper.platformRunLaterCatchUp();
            entitiesInCellsList.get(player.getX()).get(player.getY()).remove(player);
            player.move(event);
            entitiesInCellsList.get(player.getX()).get(player.getY()).add(player);
            Platform.runLater(this::updatePlayerButtonText);
        }

        ThreadHelper.platformRunLaterCatchUp();
        Thread.sleep(50);

        enemies.forEach(en -> {
            Platform.runLater(() -> {
                buttonArray[en.getX()][en.getY()].setText("");
                buttonArray[en.getX()][en.getY()]
                        .setStyle("-fx-background-color: transparent; -fx-text-fill: silver;");
            });
            entitiesInCellsList.get(en.getX()).get(en.getY()).remove(en);
            en.move();
            entitiesInCellsList.get(en.getX()).get(en.getY()).add(en);
        });

        ThreadHelper.platformRunLaterCatchUp();
        Platform.runLater(this::updateButtonTooltip);
        ThreadHelper.platformRunLaterCatchUp();

        if (player.getX() == escapeHole.getX() && player.getY() == escapeHole.getY()) {
            // If player enters the escape hole

            // Odd of the player leaving instead of engaging enemies (based on enemy count)
            long numOfEnemies = entitiesInCellsList.get(player.getX()).get(player.getY()).stream()
                    .filter(ent -> ent instanceof Enemy)
                    .count();
            // combat initialization by percentage
            if (numOfEnemies != 0 && randNum.nextLong(numOfEnemies) + 1 != numOfEnemies) {
                combat();
            }

            potentialPlChestInteraction();
            potentialPlTraderInter();

            player.setX(gameBoardRowCount / 2);
            player.setY(gameBoardColumnCount / 2);
            resetChest();
            resetEscapeHole();
            enemiesDelete();
            depthLevelString.set(Integer.toString(Integer.parseInt(depthLevelString.get()) + 1));
            Platform.runLater(() -> depthLabel.setText(depthLevelString.get()));

            try {
                if(enemies.size() < gameBoardRowCount * gameBoardColumnCount - 50) {
                    spawnEnemies(enemies);
                }
            }
            catch (OutOfGameGridException e) {
                logger.error(e.getMessage(), e);
                showErrorAlert("GameEngine thread error");
            }
            EntitiesInCellsListControl(false, true, true, true);

            if (Integer.parseInt(depthLevelString.get()) % 5 == 4) {
                resetTrader();
                trader.setHasDoneExchange(false);
            }
            else {
                entitiesInCellsList.get(trader.getX()).get(trader.getY()).remove(trader);
            }

            Platform.runLater(() -> {
                resetBoardButtonText();
                updateButtonTooltip();
            });

            return;
        }

        if (entitiesInCellsList.get(player.getX()).get(player.getY()).stream()
                .anyMatch(entity -> entity instanceof Enemy)) {
            combat();
        }

        potentialPlChestInteraction();
        potentialPlTraderInter();

        try {
            if(enemies.size() < gameBoardRowCount * gameBoardColumnCount - 50) {
                spawnEnemies(enemies);
            }
        }
        catch (OutOfGameGridException e) {
            logger.error(e.getMessage(), e);
            showErrorAlert("Error: enemy spawned out of the playable area");
        }
        EntitiesInCellsListControl(false,true, true, true);

        Platform.runLater(() -> {
            resetBoardButtonText();
            updateButtonTooltip();
        });
    }

    public void combat() throws InterruptedException {
        Platform.runLater(() -> combSkipButton.setDisable(false));
        inCombat.set(true);
        Platform.runLater(this::resetBoardButtonText);

        Platform.runLater(() -> gameTextArea.setText(""));
        MutableInt combatant1MissCount = new MutableInt();
        MutableInt combatant2MissCount = new MutableInt();
        List<Enemy> enemiesToAdd = new ArrayList<>();

        Iterator<Object> iter = entitiesInCellsList.get(player.getX()).get(player.getY()).iterator();
        while(iter.hasNext()) {
            Object entity = iter.next();
            if (entity instanceof Enemy) {
                Platform.runLater(() -> gameTextArea.appendText("Combat: " + player.getName() + " vs. a " +
                        ((Enemy) entity).getType().toLowerCase() + " named " + ((Enemy) entity).getName() + "\n"));

                Being combatant1, combatant2;
                ProgressBar combatant1TurnProgressBar, combatant2TurnProgressBar;
                combatant1MissCount.val = 0;
                combatant2MissCount.val = 0;

                if(player.getInitiative() > ((Enemy) entity).getInitiative()) {
                    combatant1 = player;
                    combatant1TurnProgressBar = playerTurnProgressBar;
                    combatant2 = (Enemy)entity;
                    Platform.runLater(() -> updateCombatScreen(combatant2, true));
                    combatant2TurnProgressBar = enemyTurnProgressBar;
                    showEnemyEquippedItems((Enemy)combatant2);
                }
                else {
                    combatant1 = (Enemy)entity;
                    Platform.runLater(() -> updateCombatScreen(combatant1, true));
                    combatant1TurnProgressBar = enemyTurnProgressBar;
                    showEnemyEquippedItems((Enemy)combatant1);
                    combatant2 = player;
                    combatant2TurnProgressBar = playerTurnProgressBar;
                }

                Platform.runLater(() -> {
                    combProgressBarSetUp(combatant1, combatant1TurnProgressBar);
                    combProgressBarSetUp(combatant2, combatant2TurnProgressBar);
                });
                ThreadHelper.platformRunLaterCatchUp();
                while (true) {
                    if (combatant1TurnProgressBar.getProgress() >= 1) {
                        combatant1.attack(combatant2, gameTextArea, combatant1MissCount);
                        if (combatant2 instanceof Player) {
                            Platform.runLater(() ->
                                    playerComScreenHealthLabel.setText(Integer.toString(combatant2.getHealth())));
                        }
                        else {
                            Platform.runLater(() ->
                                    enemyComScreenHealthLabel.setText(Integer.toString(combatant2.getHealth())));
                        }
                        ThreadHelper.platformRunLaterCatchUp();

                        if (combatant2.getHealth() <= 0) {
                            if (combatant2MissCount.val > 0 ){
                                Platform.runLater(() -> MethodsHelper
                                        .missesToTextArea(combatant2, combatant2MissCount.val, gameTextArea));
                                ThreadHelper.platformRunLaterCatchUp();
                            }
                            Platform.runLater(() -> gameTextArea.appendText("\t" + combatant2.getName() + " dies\n"));

                            if (combatant2 instanceof Player) {
                                Platform.runLater(this::playerDeath);
                                inCombat.set(false);
                                skipCombat.set(false);
                                return;
                            }

                            Platform.runLater(() ->
                                    enemyComScreenNameLabel.setText(enemyComScreenNameLabel.getText() + " (Dead)"));
                            if (combatant2.getInventoryList().size() > 0) {
                                player.grabLoot((Enemy)combatant2, gameTextArea);
                                Platform.runLater(() -> playerInvItemsTableView.refresh());
                            }
                            Platform.runLater(this::updatePlayerStats);

                            ThreadHelper.platformRunLaterCatchUp();
                            iter.remove();
                            combatant2.death();
                            enemiesToAdd.add((Enemy)combatant2);

                            Platform.runLater(() -> {
                                ((Player)combatant1).setKillCount(((Player)combatant1).getKillCount() + 1);
                                playerKillCountLabel.setText(Integer.toString(((Player)combatant1).getKillCount()));
                            });
                            if (((Player)combatant1).getKillCount() % 10 == 0
                                    && combatant1.getLevel() <= Integer.parseInt(depthLevelString.get()) + 3) {
                                combatant1.levelUp();
                            }

                            Platform.runLater(() -> updateCombatScreen(player, true));
                            break;
                        }

                        if (combatant1.getAttackSpeed() < 100) {
                            Platform.runLater(() ->
                                    combatant1TurnProgressBar.setProgress((double)combatant1.getAttackSpeed() / 100));
                        }
                    }
                    else {
                        Platform.runLater(() ->
                                combatant1TurnProgressBar.setProgress(combatant1TurnProgressBar.getProgress() + 0.01));
                    }

                    if (combatant2TurnProgressBar.getProgress() >= 1) {
                        combatant2.attack(combatant1, gameTextArea, combatant2MissCount);
                        if (combatant1 instanceof Player) {
                            Platform.runLater(() ->
                                    playerComScreenHealthLabel.setText(Integer.toString(combatant1.getHealth())));
                        }
                        else {
                            Platform.runLater(() ->
                                    enemyComScreenHealthLabel.setText(Integer.toString(combatant1.getHealth())));
                        }
                        ThreadHelper.platformRunLaterCatchUp();

                        if (combatant1.getHealth() <= 0) {
                            if (combatant1MissCount.val > 0 ){
                                Platform.runLater(() -> MethodsHelper
                                        .missesToTextArea(combatant1, combatant1MissCount.val, gameTextArea));
                                ThreadHelper.platformRunLaterCatchUp();
                            }
                            Platform.runLater(() -> gameTextArea.appendText("\t" + combatant1.getName() + " dies\n"));

                            if (combatant1 instanceof Player) {
                                playerDeath();
                                inCombat.set(false);
                                skipCombat.set(false);
                                return;
                            }

                            Platform.runLater(() ->
                                    enemyComScreenNameLabel.setText(enemyComScreenNameLabel.getText() + " (Dead)"));
                            if (combatant1.getInventoryList().size() > 0) {
                                player.grabLoot((Enemy)combatant1, gameTextArea);
                                Platform.runLater(() -> playerInvItemsTableView.refresh());
                            }
                            Platform.runLater(this::updatePlayerStats);

                            ThreadHelper.platformRunLaterCatchUp();
                            iter.remove();
                            combatant1.death();
                            enemiesToAdd.add((Enemy)combatant1);

                            Platform.runLater(() -> {
                                ((Player)combatant2).setKillCount(((Player)combatant2).getKillCount() + 1);
                                playerKillCountLabel.setText(Integer.toString(((Player)combatant2).getKillCount()));
                            });
                            if (((Player)combatant2).getKillCount() % 10 == 0
                                    && combatant2.getLevel() <= Integer.parseInt(depthLevelString.get()) + 3) {
                                combatant2.levelUp();
                            }

                            Platform.runLater(() -> updateCombatScreen(player, true));
                            break;
                        }

                        if (combatant2.getAttackSpeed() < 100) {
                            Platform.runLater(() ->
                                    combatant2TurnProgressBar.setProgress((double)combatant2.getAttackSpeed() / 100));
                        }
                    }
                    else {
                        Platform.runLater(() ->
                                combatant2TurnProgressBar.setProgress(combatant2TurnProgressBar.getProgress() + 0.01));
                    }

                    if (!skipCombat.get()) {
                        Thread.sleep(50);
                    }
                }

                Platform.runLater(() -> gameTextArea.appendText("\n"));
            }
        }

        enemiesToAdd.forEach(en -> entitiesInCellsList.get(en.getX()).get(en.getY()).add(en));

        skipCombat.set(false);
        inCombat.set(false);
        Platform.runLater(() -> combSkipButton.setDisable(true));
    }

    public void spawnEnemies(List<Enemy> enemyList) throws OutOfGameGridException {
        for(int i = 0; i < randNum.nextInt(10) + 13; ++i) {
            int x = 0, y = 0;
            Enums.MatrixPos mp = Enums.MatrixPos.values()[randNum.nextInt(4)];
            switch (mp) {
                case UP -> {
                    // x = 0;
                    y = randNum.nextInt(gameBoardColumnCount);
                }
                case DOWN -> {
                    x = gameBoardRowCount - 1;
                    y = randNum.nextInt(gameBoardColumnCount);
                }
                case LEFT -> {
                    x = randNum.nextInt(gameBoardRowCount);
                    // y = 0;
                }
                case RIGHT -> {
                    x = randNum.nextInt(gameBoardRowCount);
                    y = gameBoardColumnCount - 1;
                }
            }

            if (x < 0 || x >= gameBoardRowCount || y < 0 || y >= gameBoardColumnCount) {
                throw new OutOfGameGridException("Enemy was about to be spawned out of the playable area");
            }

            Enums.SEX sex = randNum.nextInt(2) == 0 ? Enums.SEX.MALE : Enums.SEX.FEMALE;
            String name;
            switch(randNum.nextInt(3)) {
                case 0 -> {
                    name = sex == Enums.SEX.MALE
                            ? Names.nameMap.get("male_rat").get(randNum.nextInt(Names.nameMap.get("male_rat").size()))
                            : Names.nameMap.get("female_rat").get(randNum.nextInt(Names.nameMap.get("female_rat").size()));
                    enemyList.add(new Rat(randNum.nextInt(5) + 3 , randNum.nextInt(5) + 3
                            , randNum.nextInt(7) + 6, randNum.nextInt(1), randNum.nextInt(11)
                            , randNum.nextInt(2) + 1, (randNum.nextDouble(45) + 35) / 100
                            , randNum.nextInt(8), name, sex, "Rat", "R",
                            Integer.parseInt(depthLevelString.get()),  x, y));
                }
                case 1 -> {
                    name = sex == Enums.SEX.MALE
                            ? Names.nameMap.get("male_dog").get(randNum.nextInt(Names.nameMap.get("male_dog").size()))
                            : Names.nameMap.get("female_dog").get(randNum.nextInt(Names.nameMap.get("female_dog").size()));
                    enemyList.add(new Hound(randNum.nextInt(5) + 3 , randNum.nextInt(5) + 3
                            , randNum.nextInt(7) + 9, randNum.nextInt(1), randNum.nextInt(11)
                            , randNum.nextInt(3) + 3, (randNum.nextDouble(45) + 35) / 100
                            , randNum.nextInt(12), name, sex, "Hound", "H",
                            Integer.parseInt(depthLevelString.get()), x, y));
                }
                case 2 -> {
                    name = sex == Enums.SEX.MALE
                            ? Names.nameMap.get("male_human").get(randNum.nextInt(Names.nameMap.get("male_human").size()))
                            : Names.nameMap.get("female_human").get(randNum.nextInt(Names.nameMap.get("female_human").size()));
                    enemyList.add(new HumanNPC(randNum.nextInt(20) + 30 , randNum.nextInt(50) + 30
                            , randNum.nextInt(100) + 35, randNum.nextInt(3), randNum.nextInt(11)
                            , randNum.nextInt(3) + 2, (randNum.nextDouble(45) + 35) / 100
                            , randNum.nextInt(30) + 20, name, sex, "Human", "H",
                            Integer.parseInt(depthLevelString.get()), x, y));
                    try {
                        for(int j = 0; j < randNum.nextInt(7); ++j) {
                            Item item = spawnItem(null);

                            enemyList.get(enemyList.size() - 1).pickUpItem(item);
                            item.setX(enemyList.get(enemyList.size() - 1).getX());
                            item.setY(enemyList.get(enemyList.size() - 1).getY());
                        }
                    }
                    catch (OutOfGameGridException e) {
                        logger.error(e.getMessage(), e);
                        showErrorAlert("Error: enemy spawned out of the playable area");
                    }
                }
            }
            enemyList.get(enemyList.size() - 1).setSpawnedPos(mp);
        }
    }

    public void combProgressBarSetUp(Being combatant, ProgressBar pBar) {
        if (combatant.getAttackSpeed() < 100) {
            pBar.setProgress((double)combatant.getAttackSpeed() / 100);
        }
        else {
            pBar.setProgress(1);
        }
    }

    public void potentialPlTraderInter() {
        if(player.isOnTheSameTileAs(trader) && Integer.parseInt(depthLevelString.get()) % 5 == 0 &&
                !trader.hasDoneExchange()) {
            trader.exchangeWith(player);
            Platform.runLater(() -> {
                playerKillCountLabel.setText(Integer.toString(player.getKillCount()));
                playerHealthLabel.setText(Integer.toString(player.getHealth()));
            });
            showPlayerEquippedItems();
            if (player.getHealth() <= 0) {
                playerDeath();
            }
            entitiesInCellsList.get(trader.getX()).get(trader.getY()).remove(trader);
            Platform.runLater(() -> playerInvItemsTableView.refresh());
        }
    }

    public void potentialPlChestInteraction() {
        if(player.isOnTheSameTileAs(chest)) {
            chest.lootActionBy(player);
            showPlayerEquippedItems();
            entitiesInCellsList.get(chest.getX()).get(chest.getY()).remove(chest);
            Platform.runLater(() -> playerInvItemsTableView.refresh());
        }
    }
    
    private void showErrorAlert(final String headerTxt) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initStyle(StageStyle.UNDECORATED);
        alert.setGraphic(null);
        alert.setHeaderText("Error: game engine thread");
        alert.setContentText(headerTxt);
        if (alert.showAndWait().isPresent()) {
            System.exit(1);
        }
    }
}
