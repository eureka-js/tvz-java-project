package entities.beings;

import entities.other.Names;
import exceptions.checked.OutOfGameGridException;
import main.GameScreenController;
import util.Enums.SEX;
import util.Enums.MatrixPos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import static main.GameScreenController.spawnItem;

public class HumanNPC extends HumanoidNPC implements Serializable {

    public HumanNPC (int health, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, double accuracy, int initiative
            , String name, SEX sex, String type, String symbol, int level, int x, int y) {
        super(health, stamina, weight, armour, attackSpeed, baseAttDmg, accuracy, initiative, name, sex, type, symbol, level, x, y);
    }

    public HumanNPC (int health, int baseHealth, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg,
                     int medianAttDmg, double accuracy, int initiative, String name, SEX sex, String type, String symbol,
                     int level, int x, int y, long id,
                     MatrixPos spawnedPosition) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg, medianAttDmg, accuracy,
                initiative, name, sex, type, symbol, level, x, y, id, spawnedPosition);
    }

    @Override
    public void death() {
        Random randNum = new Random();

        sex = randNum.nextInt(2) == 0 ? SEX.MALE : SEX.FEMALE;
        name = sex == SEX.MALE ? Names.nameMap.get("male_human").get(randNum.nextInt(Names.nameMap.get("male_human").size()))
                : Names.nameMap.get("female_human").get(randNum.nextInt(Names.nameMap.get("female_human").size()));

        baseHealth = randNum.nextInt(20) + 50;
        health = baseHealth;
        weight = randNum.nextInt(100) + 35;
        stamina = (randNum.nextInt(50) + 30 - weight / 4);
        armour = randNum.nextInt(5);
        attackSpeed = randNum.nextInt(11) + 4 - weight / 100;
        baseAttDmg = randNum.nextInt(3) + 5+ weight / 7;
        medianAttDmg = baseAttDmg + (weight / (10 + 10) + stamina / (10 + 5));
        accuracy = randNum.nextDouble(20) / 100;
        initiative = randNum.nextInt(30) + 20 - weight / 20;

        inventoryList = new ArrayList<>();
        equippedItemsList = new ArrayList<>();

        for(int i = 0; i < level - 1; ++i) {
            levelUp();
        }

        switch (MatrixPos.values()[randNum.nextInt(4)]) {
            case UP -> {
                setX(0);
                setY(randNum.nextInt(GameScreenController.gameBoardColumnCount));
                setSpawnedPos(MatrixPos.UP);
            }
            case DOWN -> {
                setX(GameScreenController.gameBoardRowCount - 1);
                setY(randNum.nextInt(GameScreenController.gameBoardColumnCount));
                setSpawnedPos(MatrixPos.DOWN);
            }
            case LEFT -> {
                setX(randNum.nextInt(GameScreenController.gameBoardRowCount));
                setY(0);
                setSpawnedPos(MatrixPos.LEFT);
            }
            case RIGHT -> {
                setX(randNum.nextInt(GameScreenController.gameBoardRowCount));
                setY(GameScreenController.gameBoardRowCount -1);
                setSpawnedPos(MatrixPos.RIGHT);
            }
        }

        try {
            for(int i = 0; i < randNum.nextInt(7); ++i) {
                this.pickUpItem(spawnItem(null));
            }
        }
        catch (OutOfGameGridException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
