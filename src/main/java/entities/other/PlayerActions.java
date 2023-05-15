package entities.other;

import entities.beings.Enemy;
import entities.beings.Player;
import javafx.scene.control.TextArea;

public sealed interface PlayerActions extends HumanoidActions permits Player {
    <T> void move(T event);
    void grabLoot(Enemy enemy, TextArea gameText);
}
