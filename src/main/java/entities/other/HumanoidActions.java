package entities.other;

import entities.items.Armour;
import entities.items.Item;
import entities.items.Weapon;

public interface HumanoidActions {
    void pickUpItem(Item item);
    public void equipItem(Item item);
    public void unEquipItem(Item item);
}
