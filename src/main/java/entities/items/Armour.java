package entities.items;

import java.io.Serializable;

public abstract class Armour extends Item implements Serializable {
    protected int armour;

    public Armour(int armour, int level, int weight, String name, String type, String symbol, int x, int y) {
        super(level, weight, name, type, symbol, x, y);
        this.armour = armour + level * 2;
    }
    public Armour(int armour, int level, int weight, String name, String type, String symbol, int x, int y, long id,
                  boolean isPickedUp, boolean isEquipped) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(level, weight, name, type, symbol, x, y, id, isPickedUp, isEquipped);
        this.armour = armour;
    }

    public int getArmour() {
        return armour;
    }

    public void setArmour(int armour) {
        this.armour = armour;
    }
}
