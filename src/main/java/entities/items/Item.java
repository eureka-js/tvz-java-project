package entities.items;

import entities.WorldEntity;

import java.io.Serializable;

public abstract class Item extends WorldEntity implements Serializable {
    // Item's coordinates are not updated (If I do change that there is a possibility of me forgetting to remove this comment)

    protected int level;
    protected int weight;
    protected boolean isPickedUp;
    protected boolean isEquipped;
    protected WorldEntity owner;

    public Item (int level, int weight, String name, String type, String symbol, int x, int y) {
        super(name, type, symbol, x, y);
        this.level = level;
        this.weight = weight;
        isPickedUp = false;
        isEquipped = false;
    }
    public Item (int level, int weight, String name, String type, String symbol, int x, int y, long id,
                 boolean isPickedUp, boolean isEquipped) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(name, type, symbol, x, y, id);
        this.level = level;
        this.weight = weight;
        this.isPickedUp = isPickedUp;
        this.isEquipped = isEquipped;
    }

    public int getLevel() {
        return level;
    }

    public int getWeight() {
        return weight;
    }

    public WorldEntity getOwner() {
        return owner;
    }

    public boolean isPickedUp() {
        return isPickedUp;
    }

    public boolean isEquipped() {
        return isEquipped;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setPickedUp(boolean pickedUp) {
        isPickedUp = pickedUp;
    }

    public void setEquipped(boolean equipped) {
        isEquipped = equipped;
    }

    public void setOwner(WorldEntity owner) {
        this.owner = owner;
    }
}
