package entities;


import java.io.Serializable;

public class EscapeHole implements Serializable {
    private long id;
    private int x;
    private int y;
    private String symbol;

    public static class Builder {
        private long id;
        private int x;
        private int y;
        private String symbol = "\uD83D\uDD73";

        public Builder (int x, int y) {
            this.x = x;
            this.y = y;
            this.id = -1;
        }

        public Builder withSymbol(String symbol) {
            this.symbol = symbol;

            return this;
        }

        public Builder withId(long id) {
            this.id = id;

            return this;
        }

        public EscapeHole build() {
            EscapeHole eHole = new EscapeHole();
            eHole.x = this.x;
            eHole.y = this.y;
            eHole.symbol = this.symbol;
            eHole.id = this.id;

            return eHole;
        }
    }
    
    private EscapeHole() {};

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }
    public long getid() { return id; }
    public void setid(long id) { this.id = id; }

    public void setY(int y) {
        this.y = y;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
