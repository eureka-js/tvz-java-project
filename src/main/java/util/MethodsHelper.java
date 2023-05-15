package util;

import entities.beings.Being;
import entities.beings.Human;
import entities.beings.HumanNPC;
import javafx.scene.control.TextArea;

public interface MethodsHelper {
    static void missesToTextArea(Being being, int missCount, TextArea gameTxt) {
        if (being instanceof HumanNPC || being instanceof Human) {
            gameTxt.appendText("\t\t" + being.getName() + " missed " + missCount +
                    (missCount == 1 ? " time" : " times") + " from " +
                    (being.getSex() == Enums.SEX.FEMALE ? "her" : "his") + " last hit\n");
        }
        else {
            gameTxt.appendText("\t\t" + being.getName() + " missed " + missCount +
                    (missCount == 1 ? " time" : " times") + " from it's last hit\n");
        }
    }
}
