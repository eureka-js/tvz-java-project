package util;

import java.util.InputMismatchException;

public record Enums() {
    public enum EntityType {
        HUMAN, HOUND, RAT, PLAYER, TRADER, CHEST;

        int value;

        public static EntityType stringToEntType(final String strType) {
            return switch (strType.toLowerCase()) {
                case "human" -> HUMAN;
                case "hound" -> HOUND;
                case "rat" -> RAT;
                case "player" -> PLAYER;
                case "trader" -> TRADER;
                case "chest" -> CHEST;
                default -> throw new InputMismatchException("Invalid entity type input");
            };
        }

        @Override
        public String toString() {
            return switch (this.value) {
                case 0 -> "human";
                case 1 -> "hound";
                case 2 -> "rat";
                case 3 -> "player";
                case 4 -> "trader";
                default -> "chest";
            };
        }
    }

    public enum ItemType {
        ARMWARE, HEADWARE, LEGWARE, TRUNKWARE, SHIELD, WEAPON;

        public static ItemType stringToItemType(final String strType) {
            return switch (strType.toLowerCase()) {
                case "armware" -> ARMWARE;
                case "headware" -> HEADWARE;
                case "legware" -> LEGWARE;
                case "trunkware" -> TRUNKWARE;
                case "shield" -> SHIELD;
                case "weapon" -> WEAPON;
                default -> throw new InputMismatchException("Invalid item type input");
            };
        }
    }
    public enum UserRole {
        NON_ADMIN, ADMIN;

        int val;

        @Override
        public String toString() {
            return val == 0 ? "Non admin" : "Admin";
        }
    }
    public enum LABEL_UPDATE_MODE {
        POPULATE(true), EMPTY(false);

        public final boolean state;

        LABEL_UPDATE_MODE(boolean state) {
            this.state = state;
        }
    }

    public static enum Action {
        SHOW, INSERT
    }

    public enum MatrixPos {
        UP(0), DOWN(1), LEFT(2), RIGHT(3);

        private int pos;
        MatrixPos(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public static Enums.MatrixPos generateMatrixPos(int value) {
            return switch(value) {
                case 0 -> UP;
                case 1 -> DOWN;
                case 2 -> LEFT;
                default -> RIGHT;
            };
        }
    }

    public enum SEX {
        MALE, FEMALE, UNKNOWN;

        @Override
        public String toString() {
            return switch(this) {
                case MALE -> "Male";
                case FEMALE -> "Female";
                case UNKNOWN -> "Unknown";
            };
        }

        public static boolean isValidSex(final String tmpStr) {
            if(tmpStr.equalsIgnoreCase("male") || tmpStr.equalsIgnoreCase("female")
                    || tmpStr.equalsIgnoreCase("unkown") ) {
                return true;
            }
            return false;
        }

        public static SEX generateSEXFromString(final String tmpStr) throws InputMismatchException{
            return switch (tmpStr.toLowerCase()) {
                case "male" -> MALE;
                case "female" -> FEMALE;
                default -> UNKNOWN;
            };
        }
    }
}
