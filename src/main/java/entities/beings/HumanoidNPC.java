package entities.beings;

import entities.other.HumanoidActions;
import util.Enums.SEX;
import util.Enums.MatrixPos;

import java.io.Serializable;

public abstract class HumanoidNPC extends Enemy implements HumanoidActions, Serializable {
    public HumanoidNPC (int health, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, double accuracy,
                        int initiative, String name, SEX sex, String type, String symbol, int level, int x, int y) {
        super(health, stamina, weight, armour, attackSpeed, baseAttDmg, accuracy,
                initiative, name, sex, type, symbol, level, x, y);
    }
    public HumanoidNPC (int health, int baseHealth, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg,
                        int medianAttDmg, double accuracy, int initiative, String name, SEX sex, String type,
                        String symbol, int level, int x, int y, long id,  MatrixPos spawnedPosition) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg, medianAttDmg, accuracy,
                initiative, name, sex, type, symbol, level, x, y, id , spawnedPosition);
    }
}
