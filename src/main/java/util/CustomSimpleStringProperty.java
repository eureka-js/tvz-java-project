package util;

import entities.items.Armour;
import entities.items.Item;
import entities.items.Weapon;
import javafx.beans.property.SimpleStringProperty;

public interface CustomSimpleStringProperty {
    static SimpleStringProperty simpleStrPropertyDefAtt(final Item tmpItem) {
        if(tmpItem instanceof Armour) {
            return new SimpleStringProperty(Integer.toString(((Armour) tmpItem).getArmour()));
        }
        else if(tmpItem instanceof Weapon) {
            return new SimpleStringProperty(Integer.toString(((Weapon)tmpItem).getDamage()));
        }
        else {
            return null;
        }
    }

}
