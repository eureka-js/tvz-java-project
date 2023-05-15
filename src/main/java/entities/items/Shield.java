package entities.items;

import java.io.Serializable;

public class Shield extends Armour implements Serializable {
    public Shield(int armour, int level, int weight, String name, String symbol, int x, int y) {
        super(armour, level, weight, name,  "Shield",symbol,  x, y);
    }
    public Shield(int armour, int level, int weight, String name, String symbol, int x, int y, long id,
                  boolean isPickedUp, boolean isEquipped) {
        // This Constructor is for Constructing this object from the Database
        // If used in the game logic, it will interfere with the game design
        super(armour, level, weight, name,  "Shield",symbol,  x, y, id, isPickedUp, isEquipped);
    }
}
