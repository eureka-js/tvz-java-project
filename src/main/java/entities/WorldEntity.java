package entities;

import main.GameScreenController;

import java.io.Serializable;

public class WorldEntity implements Serializable {
    protected long id;

    protected String type;
    protected String symbol;
    protected String name;
    protected int x, y;

    public WorldEntity(String name, String type, String symbol, int x, int y) {
        this.name = name;
        this.type = type;
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        id = -1;
    }

    public WorldEntity(String name, String type, String symbol, int x, int y, long id) {
        this.name = name;
        this.type = type;
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.id = id;
    }

    public int getX() {
        return x;
    }
    public void setX(int x) {
        if(0 <= x && x < GameScreenController.gameBoardRowCount) {
            this.x = x;
        }
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        if(0 <= y && y < GameScreenController.gameBoardColumnCount) {
            this.y = y;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOnTheSameTileAs(WorldEntity we) {
        return (x == we.x && y == we.y);
    }
}
