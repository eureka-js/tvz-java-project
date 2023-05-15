package entities.beings;

import entities.other.EnemyActions;
import entities.items.Item;
import main.GameScreenController;
import util.Enums.SEX;
import util.Enums.MatrixPos;

import java.io.Serializable;

public non-sealed abstract class Enemy extends Being implements EnemyActions, Serializable {
    private  MatrixPos spawnedPos;

    public Enemy (int health, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, double accuracy
            , int initiative, String name, SEX sex, String type, String symbol, int level, int x, int y) {
        super(health, stamina, weight, armour, attackSpeed, baseAttDmg, accuracy,
                initiative, name, sex, type, symbol, level, x, y);
    }
    public Enemy (int health, int baseHealth, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg,
                  int medianAttDmg, double accuracy, int initiative, String name, SEX sex, String type, String symbol,
                  int level, int x, int y, long id, MatrixPos spawnedPos) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg, medianAttDmg, accuracy,
                initiative, name, sex, type, symbol, level, x, y, id);
        this.spawnedPos = spawnedPos;
    }

    public  MatrixPos getSpawnedPos() {
        return spawnedPos;
    }

    public void setSpawnedPos(MatrixPos spawnedPos) {
        this.spawnedPos = spawnedPos;
        switch (spawnedPos) {
            case UP -> setSymbol("↓");
            case DOWN ->  setSymbol("↑");
            case LEFT ->  setSymbol("→");
            case RIGHT ->  setSymbol("←");
        }
    }

    @Override
    public void move() {
        switch(getSpawnedPos()) {
            case UP -> {
                setX(getX() + 1);
                if (getX() == GameScreenController.gameBoardRowCount - 1) turnAround();
            }
            case DOWN -> {
                setX(getX() - 1);
                if (getX() == 0) turnAround();
            }
            case LEFT -> {
                setY(getY() + 1);
                if (getY() == GameScreenController.gameBoardColumnCount - 1) turnAround();
            }
            case RIGHT -> {
                setY(getY() - 1);
                if (getY() == 0) turnAround();
            }
        }
        inventoryList.forEach(item -> {
            item.setX(x);
            item.setY(y);
        });
    }

    public void turnAround() {
        switch(getSpawnedPos()) {
            case UP -> setSpawnedPos(MatrixPos.DOWN);
            case DOWN -> setSpawnedPos(MatrixPos.UP);
            case LEFT -> setSpawnedPos(MatrixPos.RIGHT);
            case RIGHT -> setSpawnedPos(MatrixPos.LEFT);
        }
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

}
