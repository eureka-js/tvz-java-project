package entities.beings;

import entities.other.PlayerActions;
import entities.items.Item;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import main.MainController;
import util.Enums.SEX;
import java.io.Serializable;


public final class Player extends Human implements PlayerActions, Serializable {
    private int killCount;

    public Player (int health, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, double accuracy, int initiative
            , String name, SEX sex, String type, String symbol, int level, int x, int y) {
        super(health, stamina, weight, armour, attackSpeed, baseAttDmg, accuracy, initiative, name, sex, type, symbol, level, x, y);
        this.killCount = 0;
    }
    public Player (int health, int baseHealth, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg,
                   int medianAttDmg, double accuracy, int initiative, String name, SEX sex, String type, String symbol,
                   int level, int x, int y, long id, int killCount) {
        // This Constructor is for constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg, medianAttDmg, accuracy,
                initiative, name, sex, type, symbol, level, x, y, id);
        this.killCount = killCount;
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(final int killCount) {
        this.killCount = killCount;
    }

    @Override
    public <T> void move(final T event) {
        if (event instanceof KeyEvent) {
            switch (((KeyEvent) event).getCode()) {
                case W -> setX(getX() - 1);
                case S -> setX(getX() + 1);
                case A -> setY(getY() - 1);
                case D -> setY(getY() + 1);
            }
        }
        inventoryList.forEach(item -> {
            item.setX(x);
            item.setY(y);
        });
    }

    @Override
    public void pickUpItem(Item item) {
        if (getEquippedItemsList().stream().noneMatch(itm -> itm.getClass().equals(item.getClass()))) {
            equipItem(item);
        }
        getInventoryList().add(item);
        item.setPickedUp(true);
        item.setOwner(this);
    }

    @Override
    public void grabLoot(Enemy enemy, TextArea gameText) {
        if (enemy.getInventoryList().size() > 0) {
            Platform.runLater(() -> gameText.appendText("\t" + name + " loots: "));
            for(int i = 0; i < enemy.getInventoryList().size(); ++i) {
                Item item = enemy.getInventoryList().get(i);
                if (i == enemy.getInventoryList().size() - 1) {
                    Platform.runLater(() ->  gameText.appendText(item.getType() + "\n"));
                }
                else {
                    Platform.runLater(() ->  gameText.appendText(item.getType() + ", "));
                }
                item.setEquipped(false);
                pickUpItem(item);
            }
        }
    }
}
