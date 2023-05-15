package entities.other;

import entities.beings.Enemy;

public sealed interface EnemyActions extends HumanoidActions permits Enemy {
    void move();
}
