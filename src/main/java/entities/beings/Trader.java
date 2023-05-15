package entities.beings;

import entities.items.Item;
import util.Enums.SEX;

import java.util.Map;

public class Trader<T1, T2> extends Being {
    private boolean hasDoneExchange;
    private Map<T1, T2> inventory;
    private int killCount;

    public Trader(int health, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg, double accuracy,
                  int initiative, String name, SEX sex, String type, String symbol, int level, int x, int y,
                  Map<T1, T2> inventory) {
        super(health, stamina, weight, armour, attackSpeed, baseAttDmg, accuracy, initiative, name, sex, type, symbol,
                level, x, y);
        killCount = 0;
        this.inventory = inventory;
        for(T1 key: this.inventory.keySet()) {
            ((Item)this.inventory.get(key)).setOwner(this);
        }
        hasDoneExchange = false;
    }
    public Trader(int health, int baseHealth, int stamina, int weight, int armour, int attackSpeed, int baseAttDmg,
                  int medianAttDmg, double accuracy, int initiative, String name, SEX sex, String type, String symbol,
                  int level, int x, int y, long id, Map<T1, T2> inventory, int killCount, boolean hasDoneExchange) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg, medianAttDmg, accuracy, initiative,
                name, sex, type, symbol, level, x, y, id);
        this.killCount = killCount;
        this.inventory = inventory;
        for(T1 key: this.inventory.keySet()) {
            ((Item)this.inventory.get(key)).setOwner(this);
        }
        this.hasDoneExchange = hasDoneExchange;
    }

    public Map<T1, T2> getInventory() {
        return inventory;
    }

    public void exchangeWith(Player player) {
        int price = -1;
        int priceOfLife = 99999;
        for(T1 key: inventory.keySet()) {
            if (price < Integer.parseInt(key.toString()) && Integer.parseInt(key.toString()) <= player.getKillCount()) {
                price = (Integer) key;
            }
            if (priceOfLife > Integer.parseInt(key.toString())) {
                priceOfLife = (Integer) key;
            }
        }
        if (price != -1) {
            player.pickUpItem((Item) inventory.get(price));
            ((Item) inventory.get(price)).setOwner(player);
            player.setKillCount(player.getKillCount() - price);
            this.killCount += price;
            inventory.remove(price);

        }
        else {
            player.pickUpItem((Item) inventory.get(priceOfLife));
            ((Item) inventory.get(priceOfLife)).setOwner(player);
            this.killCount += player.getKillCount();
            player.setHealth(player.getHealth() - (player.getKillCount() - priceOfLife));
            player.setKillCount(0);
            inventory.remove(priceOfLife);
        }

        hasDoneExchange = true;
    }

    public boolean hasDoneExchange() {
        return hasDoneExchange;
    }

    public void setHasDoneExchange(boolean hasDoneExchange) {
        this.hasDoneExchange = hasDoneExchange;
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    @Override
    public void death() {

    }
}
