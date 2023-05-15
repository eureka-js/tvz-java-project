package entities.beings;

import entities.other.Names;
import main.GameScreenController;
import util.Enums.SEX;
import util.Enums.MatrixPos;

import java.io.Serializable;
import java.util.Random;


public class Rat extends Enemy implements Serializable {
    public Rat (int health, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, double accuracy, int initiative
            , String name, SEX sex, String type, String symbol, int level, int x, int y) {
        super(health, stamina, armour, weight,  attackSpeed, baseAttDmg, accuracy, initiative, name, sex, type, symbol, level, x, y);
    }
    public Rat (int health, int baseHealth, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, int medianAttDmg,
                double accuracy, int initiative, String name, SEX sex, String type, String symbol, int level, int x,
                int y, long id, MatrixPos spawnedPosition) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(health,baseHealth, stamina, armour, weight,  attackSpeed, baseAttDmg, medianAttDmg, accuracy,
                initiative, name, sex, type, symbol, level, x, y, id, spawnedPosition);
    }

    @Override
    public void death() {
        Random randNum = new Random();

        sex = randNum.nextInt(2) == 0 ? SEX.MALE : SEX.FEMALE;
        name = sex == SEX.MALE ? Names.nameMap.get("male_rat").get(randNum.nextInt(Names.nameMap.get("male_rat").size()))
                : Names.nameMap.get("female_rat").get(randNum.nextInt(Names.nameMap.get("female_rat").size()));
        baseHealth = randNum.nextInt(5) + 3 ;
        health = baseHealth;
        weight = randNum.nextInt(10) + 2;
        stamina = randNum.nextInt(25) + 20 - weight / 4;
        armour = randNum.nextInt(3);
        attackSpeed = randNum.nextInt(11) + 7 - weight / 100;
        baseAttDmg = randNum.nextInt(2) + 3 + weight / 7;
        medianAttDmg = baseAttDmg + (weight / (10 + 10) + stamina / (10 + 5));
        accuracy = randNum.nextDouble(35) / 80;
        initiative =  randNum.nextInt(35) + 25 - weight / 20;

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
    }
}
