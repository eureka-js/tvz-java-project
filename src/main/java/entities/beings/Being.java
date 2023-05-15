package entities.beings;

import entities.WorldEntity;
import entities.items.Armour;
import entities.items.Item;
import entities.items.Weapon;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import main.GameScreenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Enums;
import util.Enums.SEX;
import util.MethodsHelper;
import util.MutableInt;
import util.thread.GameEngineThread;
import util.thread.ThreadHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public abstract class Being extends WorldEntity implements Serializable {
    static Logger logger = LoggerFactory.getLogger(GameScreenController.class);

    protected int baseHealth;
    protected int health;
    protected int stamina;
    protected int weight;
    protected int armour;
    protected int attackSpeed;
    protected int baseAttDmg;
    protected int medianAttDmg;
    protected double accuracy;
    protected int initiative;
    protected SEX sex;
    protected int level;

    protected List<Item> inventoryList;
    protected List<Item> equippedItemsList;


    Being(int health, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, double accuracy,
          int initiative, String name, SEX sex, String type, String symbol, int level, int x, int y) {
        super(name, type, symbol, x, y);

        this.baseHealth = health;
        this.health = this.baseHealth;
        this.weight = weight;
        this.stamina = stamina - weight / 4;
        this.armour = armour;
        this.attackSpeed = attackSpeed - weight / 100;
        this.baseAttDmg = baseAttDmg + weight / 7;
        this.accuracy = accuracy;
        this.initiative = initiative - this.weight / 20;
        this.sex = sex;
        this.level = 1;
        inventoryList = new ArrayList<>();
        equippedItemsList = new ArrayList<>();

        for(int i = 0; i < level - 1; ++i) {
            levelUp();
        }

        this.medianAttDmg = this.baseAttDmg + this.weight / (10 + 10) + this.stamina / (10 + 5);
    }

    Being(int health, int baseHealth, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg,
          int medianAttDmg, double accuracy, int initiative, String name, SEX sex, String type, String symbol,
          int level, int x, int y, long id) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(name, type, symbol, x, y, id);

        this.baseHealth = baseHealth;
        this.health = health;
        this.medianAttDmg = medianAttDmg;
        this.weight = weight;
        this.stamina = stamina;
        this.armour = armour;
        this.attackSpeed = attackSpeed;
        this.baseAttDmg = baseAttDmg;
        this.accuracy = accuracy;
        this.initiative = initiative;
        this.sex = sex;
        this.level = level;

        inventoryList = new ArrayList<>();
        equippedItemsList = new ArrayList<>();
    }

    public void setHealth(int health) {
        this.health = health;
    }
    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setArmour(int armour) {
        this.armour = armour;
    }
    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setInventoryList(List<Item> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public void setEquippedItemsList(List<Item> eqipedItemsList) {
        this.equippedItemsList = eqipedItemsList;
    }

    public void setBaseHealth(int baseHealth) {
        this.baseHealth = baseHealth;
    }

    public void setBaseAttDmg(int baseAttDmg) {
        this.baseAttDmg = baseAttDmg;
    }

    public void setMedianAttDmg(int avAattDmg) {
        this.medianAttDmg = avAattDmg;
    }

    public void setSex(SEX sex) {
        this.sex = sex;
    }

    public int getLevel() {
        return level;
    }
    public int getHealth() {
        return health;
    }
    public int getStamina() {
        return stamina;
    }
    public int getArmour() {
        return armour;
    }
    public int getAttackSpeed() {
        return attackSpeed;
    }

    public int getWeight() {
        return weight;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public int getInitiative() {
        return initiative;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public int getBaseAttDmg() {
        return baseAttDmg;
    }

    public int getMedianAttDmg() {
        return medianAttDmg;
    }

    public SEX getSex() {
        return sex;
    }

    public List<Item> getInventoryList() {
        return inventoryList;
    }

    public List<Item> getEquippedItemsList() {
        return equippedItemsList;
    }

    public void attack(Being defender, TextArea gameTxt, MutableInt missCount) throws InterruptedException {
        Random randNum = new Random();

        if(randNum.nextInt(100) <= (int)(accuracy*100)) {
            if (missCount.val > 0) {
                Platform.runLater(() -> MethodsHelper.missesToTextArea(this, missCount.val, gameTxt));
                ThreadHelper.platformRunLaterCatchUp();
                missCount.val = 0;
            }
            int dmgValue = (baseAttDmg + weight / (randNum.nextInt(20) + 10)
                    + stamina / (randNum.nextInt(20) + 5)) / (defender.armour / 2 + 1);
            int oldHealth = defender.health;
            defender.setHealth(defender.getHealth() - dmgValue);
            int newHealth = defender.health;
            Platform.runLater(() -> gameTxt.appendText("\t" + name + " deals " + dmgValue + " damage to " +
                    defender.getName() + " | " + defender.getName() +
                    "'s health: " + oldHealth + " -> " + newHealth + "\n"));
        }
        else {
            missCount.val += 1;
        }
    }

    public void levelUp() {
        ++level;
        baseHealth += 2 * level;
        health = baseHealth;
        stamina += 2 * level;
        attackSpeed += level;
        baseAttDmg += level;
        medianAttDmg = baseAttDmg + weight / (10 + 10) + stamina / (10 + 5);
        initiative += level;
        accuracy += (double)level / 100;
    }

    public void equipItem(Item item) {
        equippedItemsList.add(item);
        item.setEquipped(true);
        weight += item.getWeight();
        stamina -= item.getWeight() / 4;
        initiative -= item.getWeight() / 20;
        attackSpeed -= item.getWeight() / 100;
        baseAttDmg += item.getWeight() / 7;
        if(item instanceof Armour) {
            armour += ((Armour) item).getArmour();
        }
        else if(item instanceof Weapon) {
            baseAttDmg += ((Weapon) item).getDamage();
        }
        medianAttDmg = baseAttDmg + weight / (10 + 10) + stamina / (10 + 5);

    }

    public void unEquipItem(Item item) {
        equippedItemsList.remove(item);
        item.setEquipped(false);
        weight -= item.getWeight();
        stamina += item.getWeight() / 4;
        initiative += item.getWeight() / 20;
        attackSpeed += item.getWeight() / 100;
        baseAttDmg -= item.getWeight() / 7;
        if(item instanceof Armour) {
            armour -= ((Armour) item).getArmour();
        }
        else if(item instanceof Weapon) {
            baseAttDmg -= ((Weapon) item).getDamage();
        }
        medianAttDmg = baseAttDmg + weight / (10 + 10) + stamina / (10 + 5);

    }

    public abstract void death();
}
