package entities.beings;

import java.io.Serializable;
import util.Enums.SEX;

public class Human extends Being implements Serializable {
    public Human (int health, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, double accuracy, int initiative
            , String name, SEX sex, String type, String symbol, int level, int x, int y) {
        super(health, stamina, weight, armour, attackSpeed, baseAttDmg, accuracy, initiative, name, sex, type, symbol, level, x, y);
    }
    public Human (int health, int baseHealth, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg,
                  int medianAttDmg, double accuracy, int initiative, String name, SEX sex, String type, String symbol,
                  int level, int x, int y, long id) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg, medianAttDmg, accuracy,
                initiative, name, sex, type, symbol, level, x, y, id);
    }

    @Override
    public void death() {

    }
}
