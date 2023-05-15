package entities.items;

import java.io.Serializable;

public class Weapon extends Item implements Serializable {
    protected int damage;

    public Weapon(int damage, int level, int weight, String name, String type, String symbol, int x, int y) {
        super(level, weight, name, type, symbol, x, y);
        this.damage = damage + level * 2;
    }
    public Weapon(int damage, int level, int weight, String name, String type, String symbol, int x, int y, long id,
                  boolean isPickedUp, boolean isEquipped) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(level, weight, name, type, symbol, x, y, id, isPickedUp, isEquipped);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
    public void setDamage(int damage) {
        this.damage = damage ;
    }
}
