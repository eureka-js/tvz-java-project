package entities.other;

import entities.WorldEntity;
import entities.beings.Player;
import entities.items.Item;

import java.util.List;

public class Chest<T> extends WorldEntity {
    private List<T> inventory;
    private boolean isLooted;

    public Chest(String name, String type, String symbol, int x, int y, List<T> inventory) {
        super(name, type, symbol, x, y);
        this.inventory = inventory;
        this.inventory.forEach(item -> ((Item)item).setOwner(this));
        this.isLooted = false;
    }
    public Chest(String name, String type, String symbol, int x, int y, long id, List<T> inventory, boolean isLooted) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design

        super(name, type, symbol, x, y, id);
        this.inventory = inventory;
        this.isLooted = isLooted;
    }

    public List<T> getInventory() {
        return inventory;
    }

    public void setInventory(List<T> inventory) {
        this.inventory = inventory;
    }

    public boolean isLooted() {
        return isLooted;
    }

    public void setLooted(boolean looted) {
        isLooted = looted;
    }

    public void lootActionBy(Player player) {
        inventory.forEach(item -> player.pickUpItem((Item) item));
        inventory.clear();
        this.isLooted = true;
    }
}
