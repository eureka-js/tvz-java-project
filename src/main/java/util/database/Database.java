package util.database;

import entities.EscapeHole;
import entities.WorldEntity;
import entities.beings.*;
import entities.items.*;
import entities.other.Chest;
import exceptions.unchecked.DBObjectNotPresentException;
import exceptions.unchecked.UserDoesntExistException;
import util.thread.GameEngineThread;
import main.LoginController;
import util.Enums.ItemType;
import util.Enums.UserRole;
import util.Enums.EntityType;
import util.Enums.SEX;
import util.Enums.MatrixPos;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

public class Database {
    private static final String DATABASE_FILE = "IO_files/database/database.properties";

    public static EscapeHole escapeHole;
    public static String depthString;
    public static List<Enemy> enemyList;
    public static Player player;
    public static Trader<Integer, Item> trader;
    public static Chest<Item> chest;
    public static List<Long> deletedEnemyIds = new ArrayList<>();
    public static List<Long> deletedItemIds = new ArrayList<>();
    public static List<ItemType> deletedItemTypes = new ArrayList<>();


    public static boolean userAccessGranted(final String candUsername,
                                            final String candPassword) throws SQLException, IOException {
        try(Connection connection = connectToDataBase()) {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT password FROM USER_PLAYER WHERE username = ?");
            statement.setString(1, candUsername);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next() && candPassword.equals(resultSet.getString("password"))) {
                return true;
            }

            statement = connection
                    .prepareStatement("SELECT password FROM USER_ADMIN WHERE username = ?");
            statement.setString(1, candUsername);
            resultSet = statement.executeQuery();
            if (resultSet.next() && candPassword.equals(resultSet.getString("password"))) {
                return true;
            }

        }

        return false;
    }

    public static boolean usernameIsTaken(final String candUsername) throws SQLException, IOException {
        try(Connection connection = connectToDataBase()) {
            PreparedStatement statement = connection
                    .prepareStatement("SELECT username FROM USER_PLAYER WHERE username = ?");
            statement.setString(1, candUsername);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }

            statement = connection.prepareStatement("SELECT username FROM USER_ADMIN WHERE username = ?");
            statement.setString(1, candUsername);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        }

        return false;
    }

    public static void registerUser(final String candUsername, final String candPassword,
                                    final UserRole userType) throws SQLException, IOException {
        try(Connection connection = connectToDataBase()) {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO " +
                    (userType == UserRole.NON_ADMIN ? "USER_PLAYER" : "USER_ADMIN") +
                    " (username, password, isFreshlyRegistered) VALUES(?, ?, ?)");
            statement.setString(1, candUsername);
            statement.setString(2, candPassword);
            statement.setBoolean(3, true);
            statement.executeUpdate();
        }
    }

    public static boolean isUserFreshlyRegistered(Connection FNSConnection)
            throws SQLException, IOException, UserDoesntExistException {
        Connection connection = null;
        PreparedStatement prepStatement = null;
        ResultSet resultSet = null;

        try {
            connection = FNSConnection == null ? connectToDataBase() : FNSConnection;

            prepStatement = connection.prepareStatement("SELECT isFreshlyRegistered FROM USER_PLAYER" +
                    " WHERE username = ?");
            prepStatement.setString(1, LoginController.getUserName());
            resultSet = prepStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("isFreshlyRegistered");
            }

            prepStatement = connection.prepareStatement("SELECT isFreshlyRegistered FROM USER_ADMIN" +
                    " WHERE username = ?");
            prepStatement.setString(1, LoginController.getUserName());
            resultSet = prepStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("isFreshlyRegistered");
            }

            throw new UserDoesntExistException("User(You) doesn't exist");
        }
        finally {
            if (FNSConnection == null) {
                connection.close();
                prepStatement.close();
                resultSet.close();
            }
        }
    }

    public static void setUserIsFreshlyRegisteredToFalse(Connection FNSConnection) throws SQLException, IOException {
        Connection connection = null;
        try {
            connection = FNSConnection == null ? connectToDataBase() : FNSConnection;

            PreparedStatement statement;
            if (LoginController.userIsAdmin()) {
                statement = connection.prepareStatement("UPDATE USER_ADMIN SET isFreshlyRegistered = ?"
                        + " WHERE username = ?");
            }
            else {
                statement = connection.prepareStatement("UPDATE USER_PLAYER SET isFreshlyRegistered = ?"
                        + " WHERE username = ?");
            }
            statement.setBoolean(1, false);
            statement.setString(2, LoginController.getUserName());
            statement.executeUpdate();
        }
        finally {
            if (FNSConnection == null) {
                connection.close();
            }
        }
    }

    public static boolean userIsAdmin(final String username) throws SQLException, IOException {
        try(Connection connection = connectToDataBase()) {
            PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM USER_ADMIN WHERE username = ?");
            prepStatement.setString(1, username);
            if (prepStatement.executeQuery().next()) {
                return true;
            }
        }

        return false;
    }

    public static Map<String, List<String>> loadNamesFromDB() throws SQLException, IOException {
        Map<String, List<String>> nameMapLcl = new HashMap<>();

        try (Connection connection = connectToDataBase()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ARMWARE_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "armware");
            resultSet = statement.executeQuery("SELECT * FROM FEMALE_DOG_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "female_dog");
            resultSet = statement.executeQuery("SELECT * FROM FEMALE_HUMAN_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "female_human");
            resultSet = statement.executeQuery("SELECT * FROM FEMALE_RAT_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "female_rat");
            resultSet = statement.executeQuery("SELECT * FROM HEADWARE_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "headware");
            resultSet = statement.executeQuery("SELECT * FROM LEGWARE_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "legware");
            resultSet = statement.executeQuery("SELECT * FROM MALE_DOG_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "male_dog");
            resultSet = statement.executeQuery("SELECT * FROM MALE_HUMAN_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "male_human");
            resultSet = statement.executeQuery("SELECT * FROM MALE_RAT_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "male_rat");
            resultSet = statement.executeQuery("SELECT * FROM SHIELD_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "shield");
            resultSet = statement.executeQuery("SELECT * FROM TRUNKWARE_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "trunkware");
            resultSet = statement.executeQuery("SELECT * FROM WEAPON_NAMES");
            getFromResultSet(resultSet, nameMapLcl, "weapon");
        }

        return nameMapLcl;
    }

    public static void loadObjectsFromDBToLocal() throws SQLException, IOException, DBObjectNotPresentException,
            UserDoesntExistException {
        try (Connection connection = connectToDataBase()) {
            String username = LoginController.getUserName();

            if (!usernameIsTaken(username)) {
                throw new UserDoesntExistException("User(You) doesn't exist");
            }

            PreparedStatement statement = connection
                    .prepareStatement("SELECT * FROM ESCAPE_HOLE WHERE fromUser = ?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new DBObjectNotPresentException("Object " + "'escape hole'" + " not found in the database");
            }
            escapeHole = new EscapeHole.Builder(resultSet.getInt("x"), resultSet.getInt("y"))
                    .withId(resultSet.getLong("id"))
                    .build();

            statement = connection.prepareStatement("SELECT * FROM DEPTH WHERE fromUser = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new DBObjectNotPresentException("Object " + "'depth'" + " not found in the database");
            }
            depthString = Integer.toString(resultSet.getInt("deepness"));

            statement = connection.prepareStatement("SELECT * FROM ENEMY WHERE fromUser = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            enemyList = new ArrayList<>();
            while (resultSet.next()) {
                Enemy localEnemy = switch(resultSet.getString("type").toLowerCase()) {
                    case "human" -> (HumanNPC) generateBeing(resultSet, EntityType. HUMAN);
                    case "hound" -> (Hound) generateBeing(resultSet, EntityType. HOUND);
                    default -> (Rat) generateBeing(resultSet, EntityType. RAT);
                };
                enemyList.add(localEnemy);
            }

            statement = connection.prepareStatement("SELECT * FROM PLAYER WHERE fromUser = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new DBObjectNotPresentException("Object " + "'player'" + " not found in the database");
            }
            player = (Player) generateBeing(resultSet, EntityType.PLAYER);

            statement = connection.prepareStatement("SELECT * FROM TRADER WHERE fromUser = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new DBObjectNotPresentException("Object " + "'trader'" + " not found in the database");
            }
            trader = (Trader<Integer, Item>) generateBeing(resultSet, EntityType.TRADER);

            statement = connection.prepareStatement("SELECT * FROM CHEST WHERE fromUser = ?");
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                throw new DBObjectNotPresentException("Object " + "'chest'" + " not found in the database");
            }
            chest = new Chest<>(resultSet.getString("name"),
                    resultSet.getString("type"),
                    resultSet.getString("symbol"),
                    resultSet.getInt("x"),
                    resultSet.getInt("y"),
                    resultSet.getLong("id"),
                    new ArrayList<>(populateEntityInv(resultSet, EntityType.CHEST)),
                    resultSet.getBoolean("isLooted")
            );
            chest.getInventory().forEach(item -> item.setOwner(chest));
        }
    }

    private static Being generateBeing(ResultSet resultSet, final EntityType entityType) throws SQLException, IOException {
        long id = resultSet.getLong("id");
        String type = resultSet.getString("type");
        String symbol = resultSet.getString("symbol");
        String name = resultSet.getString("name");
        int x = resultSet.getInt("x");
        int y = resultSet.getInt("y");
        int baseHealth = resultSet.getInt("baseHealth");
        int health = resultSet.getInt("health");
        int stamina = resultSet.getInt("stamina");
        int weight = resultSet.getInt("weight");
        int armour = resultSet.getInt("armour");
        int attackSpeed = resultSet.getInt("attackSpeed");
        int baseAttDmg = resultSet.getInt("baseAttDmg");
        int medianAttDmg = resultSet.getInt("medianAttDmg");
        double accuracy = resultSet.getDouble("accuracy");
        int initiative = resultSet.getInt("initiative");
        SEX sex = SEX.generateSEXFromString(resultSet.getString("sex"));
        int level = resultSet.getInt("level");

        MatrixPos spawnedPos = null;
        int killCount = 0;
        if (entityType == EntityType.PLAYER || entityType == EntityType.TRADER) {
            killCount = resultSet.getInt("killCount");
        }
        else {
            spawnedPos = MatrixPos.generateMatrixPos(resultSet.getInt("spawnedPos"));
        }

        List<Item> itemList = new ArrayList<>(populateEntityInv(resultSet, entityType));

        return switch (entityType) {
            case RAT -> new Rat(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg, medianAttDmg,
                    accuracy, initiative, name, sex, type, symbol, level, x, y, id, spawnedPos);
            case HOUND -> new Hound(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg, medianAttDmg,
                    accuracy, initiative, name, sex, type, symbol, level, x, y, id, spawnedPos);
            case HUMAN -> {
                Enemy human = new HumanNPC(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg,
                        medianAttDmg, accuracy, initiative, name, sex, type, symbol, level, x, y, id, spawnedPos);
                human.setInventoryList(itemList);
                human.getInventoryList().forEach(item -> {
                    item.setOwner(human);
                    if (item.isEquipped()) {
                        human.getEquippedItemsList().add(item);
                    }
                });

                yield human;
            }
            case PLAYER -> {
                Player tmpPlayer = new Player(health, baseHealth, stamina, weight, armour, attackSpeed, baseAttDmg,
                        medianAttDmg, accuracy, initiative, name, sex, type, symbol, level, x, y, id, killCount);
                tmpPlayer.setInventoryList(itemList);
                tmpPlayer.getInventoryList().forEach(item -> {
                    item.setOwner(tmpPlayer);
                    if (item.isEquipped()) {
                        tmpPlayer.getEquippedItemsList().add(item);
                    }
                });

                yield tmpPlayer;
            }
            case TRADER -> {
                Map<Integer, Item> invMap = new HashMap<>();
                List<Integer> keyList = new ArrayList<>();
                for (Object o: (Object[])resultSet.getArray("inventoryKey").getArray()) {
                    keyList.add((Integer)o);
                }
                for(int i = 0; i < itemList.size(); ++i) {
                    invMap.put(keyList.get(i), itemList.get(i));
                }
                Trader<Integer, Item> trader = new Trader<>(health, baseHealth, stamina, weight, armour, attackSpeed,
                        baseAttDmg, medianAttDmg, accuracy, initiative, name, sex, type, symbol, level, x, y, id,
                        invMap, killCount, resultSet.getBoolean("hasDoneExchange"));
                trader.getInventory().values().forEach(item -> item.setOwner(trader));

                yield trader;
            }
            default -> null;
        };
    }

    private static List<Item> populateEntityInv (ResultSet resultSet,
                                                 final EntityType entityType) throws SQLException, IOException {
        List<Item> itemList = new ArrayList<>();

        List<Long> itemIdList = getItemIdsFromDb(resultSet, entityType);
        List<ItemType> itemTypeList = getItemTypesFromDb(resultSet, entityType);

        List<Long> armwareIdList = new ArrayList<>(), headwareIdList = new ArrayList<>(),
                legwareIdList = new ArrayList<>(), trunkwareIdList = new ArrayList<>(),
                shieldIdList = new ArrayList<>(), weaponIdList = new ArrayList<>();
        for (int i = 0; i < itemTypeList.size(); ++i) {
            switch(itemTypeList.get(i)) {
                case ARMWARE -> armwareIdList.add(itemIdList.get(i));
                case HEADWARE-> headwareIdList.add(itemIdList.get(i));
                case LEGWARE -> legwareIdList.add(itemIdList.get(i));
                case TRUNKWARE -> trunkwareIdList.add(itemIdList.get(i));
                case SHIELD -> shieldIdList.add(itemIdList.get(i));
                case WEAPON -> weaponIdList.add(itemIdList.get(i));
            }
        }

        try(Connection connection = connectToDataBase()) {
            PreparedStatement prepStatement;

            if (armwareIdList.size() > 0) {
                prepStatement = connection.prepareStatement("SELECT * FROM ARMWARE WHERE ID = ?");
                generateItemsToList(itemList, armwareIdList, prepStatement, ItemType.ARMWARE);
            }
            if (headwareIdList.size() > 0) {
                prepStatement = connection.prepareStatement("SELECT * FROM HEADWARE WHERE ID = ?");
                generateItemsToList(itemList, headwareIdList, prepStatement, ItemType.HEADWARE);
            }
            if (legwareIdList.size() > 0) {
                prepStatement = connection.prepareStatement("SELECT * FROM LEGWARE WHERE ID = ?");
                generateItemsToList(itemList, legwareIdList, prepStatement, ItemType.LEGWARE);
            }
            if (trunkwareIdList.size() > 0) {
                prepStatement = connection.prepareStatement("SELECT * FROM TRUNKWARE WHERE ID = ?");
                generateItemsToList(itemList, trunkwareIdList, prepStatement, ItemType.TRUNKWARE);
            }
            if (shieldIdList.size() > 0) {
                prepStatement = connection.prepareStatement("SELECT * FROM SHIELD WHERE ID = ?");
                generateItemsToList(itemList, shieldIdList, prepStatement, ItemType.SHIELD);
            }
            if (weaponIdList.size() > 0) {
                prepStatement = connection.prepareStatement("SELECT * FROM WEAPON WHERE ID = ?");
                generateItemsToList(itemList, weaponIdList, prepStatement, ItemType.WEAPON);
            }
        }

        return itemList;
    }

    public static List<Long> getItemIdsFromDb(ResultSet resultSet, final EntityType entityType) throws SQLException {
        String dbColumnName = (entityType == EntityType.TRADER || entityType == EntityType.CHEST) ?
                "inventory" : "inventoryList";
        List<Long> itemIdList = new ArrayList<>();
        for(Object val: (Object[]) resultSet.getArray(dbColumnName).getArray()) {
            itemIdList.add(Integer.toUnsignedLong((Integer) val));
        }

        return  itemIdList;
    }

    public static List<ItemType> getItemTypesFromDb(final ResultSet resultSet,
                                                    EntityType entityType) throws SQLException {
        String dbColumnName = entityType == EntityType.TRADER ? "shopInvItemType" : "invItemType";
        List<ItemType> itemTypeList = new ArrayList<>();
        for(Object val : (Object[]) resultSet.getArray(dbColumnName).getArray()) {
            itemTypeList.add(ItemType.stringToItemType((String)val));
        }

        return itemTypeList;
    }


    private static void generateItemsToList (List<Item> itemList,  List<Long> itemIdList, PreparedStatement prepStatement,
                                             final ItemType itemType) throws SQLException {
        long id;
        String symbol;
        String name;
        int x, y;
        int level;
        int weight;
        boolean isPickedUp;
        boolean isEquipped;
        int armourOrDmg;

        ResultSet resultSet;
        for(long idIter: itemIdList) {
            prepStatement.setLong(1, idIter);
            resultSet = prepStatement.executeQuery();
            resultSet.next();

            id = resultSet.getLong("id");
            symbol = resultSet.getString("symbol");
            name = resultSet.getString("name");
            x = resultSet.getInt("x");
            y = resultSet.getInt("y");
            level = resultSet.getInt("level");
            weight = resultSet.getInt("weight");
            isPickedUp = resultSet.getBoolean("isPickedUp");
            isEquipped = resultSet.getBoolean("isEquipped");
            armourOrDmg = itemType == ItemType.WEAPON ?
                    resultSet.getInt("damage") : resultSet.getInt("armour");

            Item item = switch (itemType) {
                case ARMWARE -> new Armware(armourOrDmg, level, weight, name, symbol, x, y, id, isPickedUp, isEquipped);
                case HEADWARE -> new Headware(armourOrDmg, level, weight, name, symbol, x, y, id, isPickedUp, isEquipped);
                case LEGWARE -> new Legware(armourOrDmg, level, weight, name, symbol, x, y, id, isPickedUp, isEquipped);
                case TRUNKWARE -> new Trunkware(armourOrDmg, level, weight, name, symbol, x, y, id, isPickedUp, isEquipped);
                case SHIELD -> new Shield(armourOrDmg, level, weight, name, symbol, x, y, id, isPickedUp, isEquipped);
                default -> new Weapon(armourOrDmg, level, weight, name,"Weapon" , symbol, x, y, id, isPickedUp, isEquipped);
            };

            itemList.add(item);
        }
    }

    public static void saveObjectsFromLocalToDB(Player player, List<Enemy> enemyList, Trader<Integer, Item> trader,
                                                Chest<Item> chest, EscapeHole escapeHole, String depthString,
                                                Connection FNSConnection) throws SQLException, IOException {
        String username = LoginController.getUserName();

        Connection connection = null;
        PreparedStatement prepStatement = null;

        try {
            connection = FNSConnection == null ? connectToDataBase() : FNSConnection;
            if (FNSConnection == null) {
                connection.setAutoCommit(false);
            }

            uploadItems(player.getInventoryList(), connection, prepStatement);
            if (player.getId() == -1) {
                insertBeing(player, connection);
            }
            else {
                updateBeing(player, connection);
            }

            uploadItems(trader.getInventory().values().stream().toList(), connection, prepStatement);
            if (trader.getId() == -1) {
                insertBeing(trader, connection);
            }
            else {
                updateBeing(trader, connection);
            }

            for (Enemy enemy : enemyList) {
                uploadItems(enemy.getInventoryList(), connection, prepStatement);
                if (enemy.getId() == -1) {
                    insertBeing(enemy, connection);
                }
                else {
                    updateBeing(enemy, connection);
                }
            }

            if (escapeHole.getid() == -1) {
                insertEscapeHole(escapeHole, connection);
            }
            else {
                updateEscapeHole(escapeHole, connection);
            }


            prepStatement = connection.prepareStatement("SELECT * FROM DEPTH WHERE fromUser = ?");
            prepStatement.setString(1, username);
            if (prepStatement.executeQuery().next()) {
                updateDepth(depthString, connection);
            }
            else {
                insertDepth(depthString, connection);
            }

            uploadItems(chest.getInventory(), connection, prepStatement);
            if (chest.getId() == -1) {
                insertChest(chest, connection);
            }
            else {
                updateChest(chest, connection);
            }

            if (FNSConnection == null) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        }
        finally {
            if (FNSConnection == null) {
                connection.close();
                prepStatement.close();
            }
        }
    }

    private static List<Long> uploadItems(List<Item> itemList, Connection connection,
                                         PreparedStatement prepStatement) throws SQLException {
        List<Long> InsertedIds = new ArrayList<>();
        ResultSet resultSet;

        for (Item item : itemList) {
            if (item.getId() == -1) {
                prepStatement = connection
                        .prepareStatement( "INSERT INTO "+ item.getType().toUpperCase() +
                                        "(fromUser, type, symbol, name, level, weight, isPickedUp, isEquipped, " +
                                        (item instanceof Weapon ? "damage" : "armour") +
                                        ", x, y, ownerID, ownerType) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS);
                prepStatement.setString(1, LoginController.getUserName());
                prepStatement.setString(2, item.getType());
                prepStatement.setString(3, item.getSymbol());
                prepStatement.setString(4, item.getName());
                prepStatement.setInt(5, item.getLevel());
                prepStatement.setInt(6, item.getWeight());
                prepStatement.setBoolean(7, item.isPickedUp());
                prepStatement.setBoolean(8, item.isEquipped());
                prepStatement.setInt(9,
                        (item instanceof Weapon ? ((Weapon) item).getDamage() : ((Armour) item).getArmour()));
                prepStatement.setInt(10, item.getX());
                prepStatement.setInt(11, item.getY());
                prepStatement.setLong(12, item.getOwner() == null ? -1 : item.getOwner().getId());
                prepStatement.setString(13, item.getOwner() == null ? "None" : item.getOwner().getType());

                prepStatement.execute();
                resultSet = prepStatement.getGeneratedKeys();
                resultSet.next();
                item.setId(resultSet.getLong(1));
            }
            else {
                prepStatement = connection.prepareStatement("UPDATE " + item.getType().toUpperCase() +
                        " SET X = ?, Y = ?, ISPICKEDUP = ?, ISEQUIPPED = ? WHERE OWNERID = ?");
                prepStatement.setLong(1, item.getX());
                prepStatement.setLong(2, item.getY());
                prepStatement.setBoolean(3, item.isPickedUp());
                prepStatement.setBoolean(4, item.isEquipped());
                prepStatement.setLong(5, item.getOwner().getId());
                prepStatement.executeUpdate();
            }
        }

        return InsertedIds;
    }

    private static void updateItemOwnerId(WorldEntity worldEntity, Connection connection,
                                            PreparedStatement prepStatement) throws SQLException {
        List<Item> itemList = null;
        Integer[] invItemIds;
        Integer[] eqItemIds;

        if (worldEntity instanceof Enemy){
            itemList = ((Enemy)worldEntity).getInventoryList();
            if (itemList.size() == 0) {
                return;
            }
            invItemIds = itemList.stream().map(it -> Math.toIntExact(it.getId())).toList().toArray(new Integer[0]);
            eqItemIds = ((Enemy) worldEntity).getEquippedItemsList().stream()
                    .map(it -> Math.toIntExact(it.getId())).toList().toArray(new Integer[0]);

            prepStatement = connection.prepareStatement("UPDATE ENEMY SET INVENTORYLIST = ?, EQUIPPEDITEMSLIST = ?" +
                    " WHERE ID = ?");
            prepStatement.setArray(1,
                    connection.createArrayOf("INTEGER", invItemIds.length == 0 ? new Object[]{} : invItemIds));
            prepStatement.setArray(2,
                    connection.createArrayOf("INTEGER", eqItemIds.length == 0 ? new Object[]{} : eqItemIds));
            prepStatement.setLong(3, worldEntity.getId());
        }
        else if (worldEntity instanceof Player) {
            itemList = ((Player)worldEntity).getInventoryList();
            if (itemList.size() == 0) {
                return;
            }
            invItemIds = itemList.stream().map(it -> Math.toIntExact(it.getId())).toList().toArray(new Integer[0]);
            eqItemIds = ((Player) worldEntity).getEquippedItemsList().stream()
                    .map(it -> Math.toIntExact(it.getId())).toList().toArray(new Integer[0]);

            prepStatement = connection.prepareStatement("UPDATE PLAYER SET INVENTORYLIST = ?, EQUIPPEDITEMSLIST = ?" +
                    " WHERE ID = ?");
            prepStatement.setArray(1,
                    connection.createArrayOf("INTEGER", invItemIds.length == 0 ? new Object[]{} : invItemIds));
            prepStatement.setArray(2,
                    connection.createArrayOf("INTEGER", eqItemIds.length == 0 ? new Object[]{} : eqItemIds));
            prepStatement.setLong(3, worldEntity.getId());
        }
        else if (worldEntity instanceof Trader<?, ?>) {
            itemList = ((Trader<Integer, Item>) worldEntity).getInventory().values().stream().toList();
            if (itemList.size() == 0) {
                return;
            }
            invItemIds = itemList.stream().map(it -> Math.toIntExact(it.getId())).toList().toArray(new Integer[0]);

            prepStatement = connection.prepareStatement("UPDATE TRADER SET INVENTORY = ? WHERE ID = ?");
            prepStatement.setArray(1,
                    connection.createArrayOf("INTEGER", invItemIds.length == 0 ? new Object[]{} : invItemIds));
            prepStatement.setLong(2, worldEntity.getId());
        }
        else if (worldEntity instanceof Chest<?>) {
            itemList = ((Chest)worldEntity).getInventory();
            if (itemList.size() == 0) {
                return;
            }
            invItemIds = itemList.stream().map(it -> Math.toIntExact(it.getId())).toList().toArray(new Integer[0]);

            prepStatement = connection.prepareStatement("UPDATE CHEST SET INVENTORY = ? WHERE ID = ?");
            prepStatement.setArray(1,
                    connection.createArrayOf("INTEGER", invItemIds.length == 0 ? new Object[]{} : invItemIds));
            prepStatement.setLong(2, worldEntity.getId());
        }

        prepStatement.executeUpdate();

        for (Item item : itemList) {
            prepStatement = connection.prepareStatement("UPDATE " + item.getType().toUpperCase() + " SET ownerID = " +
                    worldEntity.getId() + " WHERE ID = " + item.getId());
            prepStatement.executeUpdate();
        }
    }

    public static void insertBeing(Being being, Connection connection) throws SQLException {
        PreparedStatement prepStatement = null;

        if (being instanceof Enemy) {
            prepStatement = connection.prepareStatement("INSERT INTO" +
                            " ENEMY(fromUser, TYPE, SYMBOL, NAME, X, Y, BASEHEALTH, HEALTH, " +
                            "STAMINA, WEIGHT, ARMOUR, ATTACKSPEED, BASEATTDMG, MEDIANATTDMG, ACCURACY, INITIATIVE, SEX, " +
                            "LEVEL, INVENTORYLIST, INVITEMTYPE, EQUIPPEDITEMSLIST, EQITEMTYPE, SPAWNEDPOS)" +
                            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            sharedStatementSetters(being, prepStatement, connection);
            sharedEnStatementSetters(being, prepStatement);
        }
        else if (being instanceof Player) {
            prepStatement = connection.prepareStatement("INSERT INTO" +
                            " PLAYER(fromUser, TYPE, SYMBOL, NAME, X, Y, BASEHEALTH, HEALTH, STAMINA," +
                            " WEIGHT, ARMOUR, ATTACKSPEED, BASEATTDMG, MEDIANATTDMG, ACCURACY, INITIATIVE, SEX, LEVEL," +
                            " INVENTORYLIST, INVITEMTYPE, EQUIPPEDITEMSLIST, EQITEMTYPE, KILLCOUNT)" +
                            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            sharedStatementSetters(being, prepStatement, connection);
            sharedPlStatementSetters(being, prepStatement);
        }
        else if (being instanceof Trader) {
            prepStatement = connection.prepareStatement("INSERT INTO" +
                    " TRADER(fromUser, TYPE, SYMBOL, NAME, X, Y, BASEHEALTH, HEALTH," +
                            " STAMINA, WEIGHT, ARMOUR, ATTACKSPEED, BASEATTDMG, MEDIANATTDMG, ACCURACY, INITIATIVE," +
                            " SEX, LEVEL, INVENTORYLIST, INVITEMTYPE, EQUIPPEDITEMSLIST, EQITEMTYPE, HASDONEEXCHANGE," +
                            " INVENTORYKEY, INVENTORY, SHOPINVITEMTYPE, KILLCOUNT)" +
                            " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            sharedStatementSetters(being, prepStatement, connection);
            sharedTrStatementSetters(being, prepStatement, connection);
        }
        assert prepStatement != null;
        prepStatement.execute();

        ResultSet resultSet = prepStatement.getGeneratedKeys();
        resultSet.next();
        being.setId(resultSet.getLong(1));

        updateItemOwnerId(being, connection, prepStatement);
    }

    public static void updateBeing(Being being, Connection connection) throws SQLException {
        PreparedStatement prepStatement = null;
        StringBuilder sqlQuery = new StringBuilder("fromUser = ?, TYPE = ?, SYMBOL = ?, NAME = ?," +
                " X = ?, Y = ?, BASEHEALTH = ?, HEALTH = ?, STAMINA = ?, WEIGHT = ?, ARMOUR = ?, ATTACKSPEED = ?, " +
                " BASEATTDMG = ?, MEDIANATTDMG = ?, ACCURACY = ?, INITIATIVE = ?, SEX = ?, LEVEL = ?," +
                " INVENTORYLIST = ?, INVITEMTYPE = ?, EQUIPPEDITEMSLIST = ?, EQITEMTYPE = ?");

        if (being instanceof Enemy) {
            prepStatement = connection.prepareStatement("UPDATE ENEMY SET " + sqlQuery +
                    ", SPAWNEDPOS = ? WHERE ID = " + being.getId());

            sharedStatementSetters(being, prepStatement, connection);
            sharedEnStatementSetters(being, prepStatement);
        }
        else if (being instanceof Player) {
            prepStatement = connection.prepareStatement("UPDATE PLAYER SET " + sqlQuery +
                    ", KILLCOUNT = ? WHERE ID = " +  being.getId());

            sharedStatementSetters(being, prepStatement, connection);
            sharedPlStatementSetters(being, prepStatement);
        }
        else if (being instanceof Trader) {
            prepStatement = connection.prepareStatement("UPDATE TRADER SET " + sqlQuery + ", HASDONEEXCHANGE = ?," +
                    " INVENTORYKEY = ?, INVENTORY = ?, SHOPINVITEMTYPE = ?, KILLCOUNT = ? WHERE ID = " + being.getId());

            sharedStatementSetters(being, prepStatement, connection);
            sharedTrStatementSetters(being, prepStatement, connection);
        }

        assert prepStatement != null;
        prepStatement.executeUpdate();
    }

    private static void sharedStatementSetters(Being being, PreparedStatement prepStatement,
                                                  Connection connection) throws SQLException {
        prepStatement.setString(1, LoginController.getUserName());
        prepStatement.setString(2, being.getType());
        prepStatement.setString(3, being.getSymbol());
        prepStatement.setString(4, being.getName());
        prepStatement.setInt(5, being.getX());
        prepStatement.setInt(6, being.getY());
        prepStatement.setInt(7, being.getBaseHealth());
        prepStatement.setInt(8, being.getHealth());
        prepStatement.setInt(9, being.getStamina());
        prepStatement.setInt(10, being.getWeight());
        prepStatement.setInt(11, being.getArmour());
        prepStatement.setInt(12, being.getAttackSpeed());
        prepStatement.setInt(13, being.getBaseAttDmg());
        prepStatement.setInt(14, being.getMedianAttDmg());
        prepStatement.setDouble(15, being.getAccuracy());
        prepStatement.setInt(16, being.getInitiative());
        prepStatement.setString(17, being.getSex().toString());
        prepStatement.setInt(18, being.getLevel());

        Integer[] itemIds = new Integer[being.getInventoryList().size()];
        String[] itemTypes = new String[being.getInventoryList().size()];
        for(int i = 0; i < being.getInventoryList().size(); ++i) {
            itemIds[i] = Math.toIntExact(being.getInventoryList().get(i).getId());
            itemTypes[i] = being.getInventoryList().get(i).getType();
        }
        prepStatement.setArray(19,
                connection.createArrayOf("INTEGER", itemIds.length == 0 ? new Object[]{} : itemIds));
        prepStatement.setArray(20,
                connection.createArrayOf("VARCHAR", itemTypes.length == 0 ? new Object[]{} : itemTypes));

        itemIds = new Integer[being.getEquippedItemsList().size()];
        itemTypes = new String[being.getEquippedItemsList().size()];
        for(int i = 0; i < being.getEquippedItemsList().size(); ++i) {
            itemIds[i] = Math.toIntExact(being.getEquippedItemsList().get(i).getId());
            itemTypes[i] = being.getEquippedItemsList().get(i).getType();
        }
        prepStatement.setArray(21,
                connection.createArrayOf("INTEGER", itemIds.length == 0 ? new Object[]{} : itemIds));
        prepStatement.setArray(22,
                connection.createArrayOf("VARCHAR", itemTypes.length == 0 ? new Object[]{} : itemTypes));
    }

    private static void sharedPlStatementSetters(Being being, PreparedStatement prepStatement) throws SQLException {
        prepStatement.setInt(23, ((Player) being).getKillCount());
    }

    private static void sharedEnStatementSetters(Being being, PreparedStatement prepStatement) throws SQLException {
        prepStatement.setInt(23, ((Enemy) being).getSpawnedPos().getPos());
    }

    private static void sharedTrStatementSetters(Being being, PreparedStatement prepStatement,
                                                 Connection connection) throws SQLException {
        prepStatement.setBoolean(23, ((Trader<?, ?>) being).hasDoneExchange());
        prepStatement.setObject(24, ((Trader<?, ?>) being).getInventory().keySet().toArray());

        Integer[] idArray = new Integer[((Trader<?, ?>) being).getInventory().values().size()];
        String[] typeArray = new String[((Trader<?, ?>) being).getInventory().values().size()];
        int i = 0;
        for(Item item: ((Trader<Integer, Item>) being).getInventory().values()) {
            idArray[i] = Math.toIntExact(item.getId());
            typeArray[i] = item.getType();
            ++i;
        }
        prepStatement.setArray(25,
                connection.createArrayOf("INTEGER", idArray.length == 0 ? new Object[]{} : idArray));
        prepStatement.setArray(26,
                connection.createArrayOf("VARCHAR", typeArray.length == 0 ? new Object[]{} : typeArray));
        prepStatement.setInt(27, ((Trader) being).getKillCount());
    }

    private static void insertEscapeHole(EscapeHole escapeHole, Connection connection) throws SQLException {
        PreparedStatement prepStatement = connection
                .prepareStatement("INSERT INTO ESCAPE_HOLE(fromUser, x, y) VALUES(?, ?, ?)");
        prepStatement.setString(1, LoginController.getUserName());
        prepStatement.setInt(2, escapeHole.getX());
        prepStatement.setInt(3, escapeHole.getY());
        prepStatement.execute();
    }

    private static void insertDepth(final String depthString, Connection connection) throws SQLException {
        PreparedStatement prepStatement = connection
                .prepareStatement("INSERT INTO DEPTH(fromUser, deepness) VALUES(?, ?)");
        prepStatement.setString(1, LoginController.getUserName());
        prepStatement.setInt(2, Integer.parseInt(depthString));
        prepStatement.execute();
    }

    private static void insertChest(Chest<Item> chest, Connection connection) throws SQLException {
        PreparedStatement prepStatement = connection
                .prepareStatement("INSERT INTO CHEST(fromUser, type, SYMBOL, name, X, Y," +
                        " INVENTORY, INVITEMTYPE, ISLOOTED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
        prepStatement.setString(1, LoginController.getUserName());
        prepStatement.setString(2, chest.getType());
        prepStatement.setString(3, chest.getSymbol());
        prepStatement.setString(4, chest.getName());
        prepStatement.setInt(5, chest.getX());
        prepStatement.setInt(6, chest.getY());

        Integer[] itemIds = new Integer[chest.getInventory().size()];
        String[] itemTypes = new String[chest.getInventory().size()];
        for(int i = 0; i < chest.getInventory().size(); ++i) {
            itemIds[i] = Math.toIntExact(chest.getInventory().get(i).getId());
            itemTypes[i] = chest.getInventory().get(i).getType();
        }
        prepStatement.setArray(7,
                connection.createArrayOf("INTEGER", itemIds.length == 0 ? new Object[]{} : itemIds));
        prepStatement.setArray(8,
                connection.createArrayOf("VARCHAR", itemTypes.length == 0 ? new Object[]{} : itemTypes));

        prepStatement.setBoolean(9, chest.isLooted());

        prepStatement.execute();

        ResultSet resultSet = prepStatement.getGeneratedKeys();
        resultSet.next();
        chest.setId(resultSet.getLong(1));
        updateItemOwnerId(chest, connection, prepStatement);
    }

    private static void updateEscapeHole(EscapeHole escapeHole, Connection connection) throws SQLException {
        PreparedStatement prepStatement = connection
                .prepareStatement("UPDATE ESCAPE_HOLE SET X = ?, Y = ? WHERE ID = ?");
        prepStatement.setInt(1, escapeHole.getX());
        prepStatement.setInt(2, escapeHole.getY());
        prepStatement.setLong(3, escapeHole.getid());

        prepStatement.execute();
    }

    private static void updateDepth(final String depthString, Connection connection) throws SQLException {
        PreparedStatement prepStatement = connection
                .prepareStatement("UPDATE DEPTH SET deepness = ? WHERE fromUser = ?");
        prepStatement.setInt(1, Integer.parseInt(depthString));
        prepStatement.setString(2, LoginController.getUserName());

        prepStatement.executeUpdate();
    }

    private static void updateChest(Chest<?> chest, Connection connection) throws SQLException {
        PreparedStatement prepStatement = connection
                .prepareStatement("UPDATE CHEST SET TYPE = ?, SYMBOL = ?, NAME = ?," +
                        " X = ?, Y = ?, INVENTORY = ?, INVITEMTYPE = ?, ISLOOTED = ? WHERE ID = ?");
        prepStatement.setString(1, chest.getType());
        prepStatement.setString(2, chest.getSymbol());
        prepStatement.setString(3, chest.getName());
        prepStatement.setInt(4, chest.getX());
        prepStatement.setInt(5, chest.getY());

        Integer[] itemIds = new Integer[chest.getInventory().size()];
        String[] itemTypes = new String[chest.getInventory().size()];
        for(int i = 0; i < chest.getInventory().size(); ++i) {
            itemIds[i] = Math.toIntExact(((Item) chest.getInventory().get(i)).getId());
            itemTypes[i] = ((Item)chest.getInventory().get(i)).getType();
        }
        prepStatement.setArray(6,
                connection.createArrayOf("INTEGER", itemIds.length == 0 ? new Object[]{} : itemIds));
        prepStatement.setArray(7,
                connection.createArrayOf("VARCHAR", itemTypes.length == 0 ? new Object[]{} : itemTypes));

        prepStatement.setBoolean(8, chest.isLooted());
        prepStatement.setLong(9, chest.getId());

        prepStatement.execute();
    }

    public static Connection connectToDataBase() throws SQLException, IOException {
        Properties properties = new Properties();
        properties.load(new FileReader(DATABASE_FILE));

        return DriverManager.getConnection(properties.getProperty("databaseUrl"), properties.getProperty("username")
                , properties.getProperty("password"));
    }

    public static void getFromResultSet(ResultSet resultSet, Map<String, List<String>> nameMapLcl, String key)
            throws SQLException {
        nameMapLcl.put(key, new ArrayList<>());
        while (resultSet.next()) {
            nameMapLcl.get(key).add(resultSet.getString("name"));
        }
    }

    public static void bufferDelEnemyIds(List<Enemy> enemyList) {
        for (Enemy enemy: enemyList) {
            if (enemy.getId() == -1) {
                continue;
            }
            bufferDelItemIds(enemy.getInventoryList());
            deletedEnemyIds.add(enemy.getId());
        }
    }

    public static void bufferDelEnemyId(Enemy enemy) {
        if (enemy.getId() != -1) {
            bufferDelItemIds(enemy.getInventoryList());
            deletedEnemyIds.add(enemy.getId());
        }
    }

    public static void flushBufferDelEnemyIds(Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM ENEMY WHERE ID = ?");
        for (Long id: deletedEnemyIds) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        }
        deletedEnemyIds.clear();
    }

    public static void bufferDelItemIds(List<Item> itemList) {
        for (Item item: itemList) {
            bufferDelItemId(item);
        }
    }

    public static void bufferDelItemId(Item item) {
        if (item.getId() == -1) {
            return;
        }
        deletedItemIds.add(item.getId());
        if (item instanceof Headware) {
            deletedItemTypes.add(ItemType.HEADWARE);
        }
        else if (item instanceof Armware) {
            deletedItemTypes.add(ItemType.ARMWARE);
        }
        else if (item instanceof Legware) {
            deletedItemTypes.add(ItemType.LEGWARE);
        }
        else if (item instanceof Shield) {
            deletedItemTypes.add(ItemType.SHIELD);
        }
        else if (item instanceof Trunkware) {
            deletedItemTypes.add(ItemType.TRUNKWARE);
        }
        else if (item instanceof Weapon) {
            deletedItemTypes.add(ItemType.WEAPON);
        }
    }


    public static void flushBufferDelItemIds(Connection connection) throws SQLException {
        PreparedStatement prepStatement;
        for (int i = 0; i < deletedItemIds.size(); ++i) {
            prepStatement = switch (deletedItemTypes.get(i)) {
                case HEADWARE -> connection.prepareStatement("DELETE FROM HEADWARE WHERE ID = ?");
                case ARMWARE -> connection.prepareStatement("DELETE FROM ARMWARE WHERE ID = ?");
                case LEGWARE -> connection.prepareStatement("DELETE FROM LEGWARE WHERE ID = ?");
                case SHIELD -> connection.prepareStatement("DELETE FROM SHIELD WHERE ID = ?");
                case TRUNKWARE -> connection.prepareStatement("DELETE FROM TRUNKWARE WHERE ID = ?");
                case WEAPON ->  connection.prepareStatement("DELETE FROM WEAPON WHERE ID = ?");
            };
            prepStatement.setLong(1, deletedItemIds.get(i));
            prepStatement.execute();
        }

        deletedItemIds.clear();
        deletedItemTypes.clear();
    }

    public static void flushDeleteBuffers(Connection NFSConnection) throws SQLException, IOException {
        Connection connection = null;

        try {
            connection = NFSConnection == null ? connectToDataBase() : NFSConnection;
            if (NFSConnection == null) {
                connection.setAutoCommit(false);
            }

            flushBufferDelEnemyIds(connection);
            flushBufferDelItemIds(connection);

            if (NFSConnection == null) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        }
        finally {
            if (NFSConnection == null) {
                connection.close();
            }
        }
    }

    public static void executeFlushAndSave(Player player,List<Enemy> enemyList, Trader<Integer, Item> trader, Chest<Item> chest,
                                           EscapeHole escapeHole, String depthLevelString)
            throws SQLException, IOException, UserDoesntExistException {
        if (GameEngineThread.inCombat.get()) {
            return;
        }
        if (!usernameIsTaken(LoginController.getUserName())) {
            throw new UserDoesntExistException("User(You) doesn't exist. Could not save");
        }

        try (Connection connection = connectToDataBase()) {
            connection.setAutoCommit(false);

            flushDeleteBuffers(connection);
            saveObjectsFromLocalToDB(player, enemyList, trader, chest, escapeHole, depthLevelString, connection);

            // Can use LoginController.isFreshlyRegistered, but this is because I have to save them both in a file and in a DB
            //  (FileIO or Database, if used before the other, would change it before the other class used it).
            // Otherwise, the code would get unnecessarily more complicated
            if (Database.isUserFreshlyRegistered(connection)) {
                Database.setUserIsFreshlyRegisteredToFalse(connection);
                LoginController.isFreshlyRegistered = false;
            }

            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public static List<List<String>> loadAllEntityChanges() throws SQLException, IOException {
        List<List<String>> changes = new ArrayList<>();
        List<String> tmpList;

        try(Connection connection = connectToDataBase()) {
            PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM ENTITY_CHANGES");
            ResultSet resultSet = prepStatement.executeQuery();
            while (resultSet.next()) {
                tmpList = new ArrayList<>();
                tmpList.add(resultSet.getString("data") );
                tmpList.add(resultSet.getString("old_value"));
                tmpList.add(resultSet.getString("new_value"));
                tmpList.add(resultSet.getString("changed_by"));
                tmpList.add(resultSet.getTimestamp("time_and_date").toString());
                changes.add(tmpList);
            }
        }

        return changes;
    }

    public static void insertEntityChange(StringBuilder strBld) throws SQLException, IOException, ParseException {
        try(Connection connection = connectToDataBase()) {
            String[] strArray = strBld.toString().split(" ! ");

            PreparedStatement prepStatement = connection.prepareStatement("INSERT INTO ENTITY_CHANGES" +
                    "(DATA, OLD_VALUE, NEW_VALUE, CHANGED_BY, TIME_AND_DATE) VALUES(?, ?, ?, ?, ?)");
            prepStatement.setString(1, strArray[0]);
            prepStatement.setString(2, strArray[1]);
            prepStatement.setString(3, strArray[2]);
            prepStatement.setString(4, strArray[3]);
            prepStatement.setTimestamp(5, Timestamp.valueOf(strArray[4]));

            prepStatement.execute();
        }
    }

    public static String loadLastEntityChange() throws SQLException, IOException {
        try(Connection connection = connectToDataBase()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ENTITY_CHANGES ORDER BY ID DESC LIMIT 0, 1");
            if (resultSet.next()) {
                return  "Data: " + resultSet.getString("data") +
                        " | Old value: " + resultSet.getString("old_value") +
                        " | New value: " + resultSet.getString("new_value") +
                        " | Changed by: " + resultSet.getString("changed_by") +
                        " | Time and date: " + resultSet.getTimestamp("time_and_date").toString();
            }
        }

        return "";
    }

    public static List<List<String>> getListOfUsers() throws SQLException, IOException {
        List<List<String>> userList = new ArrayList<>();

        try (Connection connection = connectToDataBase()) {
            PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM USER_ADMIN");
            prepStatement.execute();
            ResultSet resultSet = prepStatement.getResultSet();
            while (resultSet.next()) {
                userList.add(new ArrayList<>());
                userList.get(userList.size() - 1).add(resultSet.getString("username"));
                userList.get(userList.size() - 1).add("Admin");
            }

            prepStatement = connection.prepareStatement("SELECT * FROM USER_PLAYER");
            prepStatement.execute();
            resultSet = prepStatement.getResultSet();
            while (resultSet.next()) {
                userList.add(new ArrayList<>());
                userList.get(userList.size() - 1).add(resultSet.getString("username"));
                userList.get(userList.size() - 1).add("Basic");
            }
        }

        return userList;
    }

    public static void deleteUser(final String username) throws SQLException, IOException {
        try (Connection connection  = connectToDataBase()) {
            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM ENEMY WHERE fromUser = '" + username + "'");
            for (ItemType it : ItemType.values()) {
                statement.execute("DELETE FROM " +  it.toString() + " WHERE fromUser = '" + username + "'");
            }
            statement.execute("DELETE FROM PLAYER WHERE fromUser = '" + username + "'");
            statement.execute("DELETE FROM ESCAPE_HOLE WHERE fromUser = '" + username + "'");
            statement.execute("DELETE FROM TRADER WHERE fromUser = '" + username + "'");
            statement.execute("DELETE FROM CHEST WHERE fromUser = '" + username + "'");
            statement.execute("DELETE FROM DEPTH WHERE fromUser = '" + username + "'");
            statement.execute("DELETE FROM WEAPON WHERE fromUser = '" + username + "'");
            statement.execute("DELETE FROM " + (Database.userIsAdmin(username) ? "USER_ADMIN" : "USER_PLAYER" ) +
                    " WHERE username = '" + username + "'");

            connection.commit();
        }
    }

    public static void changeUserRole(final String username, final UserRole newUserType) throws SQLException, IOException {
        try (Connection connection = connectToDataBase()) {
            connection.setAutoCommit(false);

            PreparedStatement prepStatement = connection.prepareStatement("SELECT * FROM " +
                    (newUserType == UserRole.ADMIN ? "USER_PLAYER" : "USER_ADMIN") + " WHERE username = ?" );
            prepStatement.setString(1, username);
            ResultSet resultset = prepStatement.executeQuery();
            resultset.next();
            String password = resultset.getString("password");
            boolean isFreshlyRegistered = resultset.getBoolean("isFreshlyRegistered");

            prepStatement = connection.prepareStatement("INSERT INTO " +
                    (newUserType == UserRole.ADMIN ? "USER_ADMIN" : "USER_PLAYER") +
                    "(username, password, isFreshlyRegistered) VALUES(?, ?, ?)");
            prepStatement.setString(1, username);
            prepStatement.setString(2, password);
            prepStatement.setBoolean(3, isFreshlyRegistered);
            prepStatement.execute();

            prepStatement = connection.prepareStatement("DELETE FROM " +
                    (newUserType == UserRole.ADMIN ? "USER_PLAYER" : "USER_ADMIN") + " WHERE username = ?");
            prepStatement.setString(1, username);
            prepStatement.execute();

            connection.commit();
        }
    }
}
