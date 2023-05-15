package util;

import entities.beings.Enemy;
import entities.beings.Player;
import entities.beings.Trader;
import entities.EscapeHole;
import entities.items.Item;
import entities.other.Chest;
import main.LoginController;
import main.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import util.Enums.UserRole;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class FileIO {
    static Logger logger = LoggerFactory.getLogger(FileIO.class);

    public static EscapeHole escapeHole;
    public static String depthString;
    public static List<Enemy> enemyList;
    public static Player player;
    public static Trader<Integer, Item> trader;
    public static Chest<Item> chest;
    public static List<List<List<Object>>> entitiesInCellsList;


    public static Boolean[] getUser(final String candUserName, final String candHashedPass) throws IOException {
        if (Files.notExists(Paths.get("IO_files/users.txt"))) {
            Files.createFile(Paths.get("IO_files/users.txt"));
        }

        try(BufferedReader br = new BufferedReader(new FileReader("IO_files/users.txt"))) {
            String tmpUsNameStr, tmpPassStr;
            while(true) {
                if ((tmpUsNameStr = br.readLine()) == null) {
                    return null;
                }

                tmpPassStr = br.readLine();
                if ((tmpUsNameStr.equals(candUserName)) && tmpPassStr.equals(candHashedPass) ) {
                    return new Boolean[]{br.readLine().equalsIgnoreCase("true"),
                            br.readLine().equalsIgnoreCase("true")};
                }
                br.readLine();
                br.readLine();
            }
        }
    }

    public static boolean usernameIsTaken(final String candUserName) throws IOException {
        if (Files.notExists(Paths.get("IO_files/users.txt"))) {
            Files.createFile(Paths.get("IO_files/users.txt"));
        }

        try (BufferedReader br = new BufferedReader(new FileReader("IO_files/users.txt"))) {
            String tmpStr;
            while ((tmpStr = br.readLine()) != null) {
                if (tmpStr.equals(candUserName)) {
                    return true;
                }
                br.readLine();
                br.readLine();
                br.readLine();
            }

            return false;
        }
    }

    public static boolean userAccessGranted(final String candUserName, final String candPass) throws IOException {
        if (Files.notExists(Paths.get("IO_files/users.txt"))) {
            Files.createFile(Paths.get("IO_files/users.txt"));
        }

        try (BufferedReader br = new BufferedReader(new FileReader("IO_files/users.txt"))) {
            String tmpStr, tmpPass;
            while ((tmpStr = br.readLine()) != null) {
                tmpPass = br.readLine();
                if (tmpStr.equals(candUserName) && tmpPass.equals(candPass)) {
                    return true;
                }
                br.readLine();
                br.readLine();
            }

            return false;
        }
    }

    public static void registerUser(final String username, final String password,
                                    final  Enums.UserRole uRole) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("IO_files/users.txt", true))) {
            bw.write(username + "\n" + password + "\n" + (uRole == UserRole.ADMIN ? "true" : "false") + "\ntrue\n");
        }
    }

    public static void deleteUser(final String username) throws IOException {
        if (Files.notExists(Paths.get("IO_files/users.txt"))) {
            Files.createFile(Paths.get("IO_files/users.txt"));
        }

        Files.deleteIfExists(Paths.get("IO_files/user_entities/" + username + "_entities.dat"));

        try (BufferedReader br = new BufferedReader(new FileReader("IO_files/users.txt"));
             BufferedWriter bw = new BufferedWriter(new FileWriter("IO_files/newUsers.txt", true))) {
            String tmpUsername, tmpPass, tmpRole, tmpFresh;
            while((tmpUsername = br.readLine()) != null) {
                tmpPass = br.readLine();
                tmpRole = br.readLine();
                tmpFresh = br.readLine();
                if (!tmpUsername.equals(username)) {
                    bw.write(tmpUsername + "\n" + tmpPass + "\n" + tmpRole + "\n" + tmpFresh + "\n");
                }
            }
        }

        Files.deleteIfExists(Paths.get("IO_files/users.txt"));
        (new File("IO_files/newUsers.txt")).renameTo(new File("IO_files/users.txt"));
    }

    public static void changeUserRoleNFreshlyReg(final String username, final UserRole userRole, final boolean changeRole,
                                                 final boolean freshlyRegToFalse) throws IOException {
        if (Files.notExists(Paths.get("IO_files/users.txt"))) {
            Files.createFile(Paths.get("IO_files/users.txt"));
        }

        boolean roleChanged = false;
        try (BufferedReader br = new BufferedReader(new FileReader("IO_files/users.txt"));
             BufferedWriter bw = new BufferedWriter(new FileWriter("IO_files/newUsers.txt", true))) {
            String tmpUsername, tmpPass, tmpRole, tmpFresh;
            while((tmpUsername = br.readLine()) != null) {
                tmpPass = br.readLine();
                tmpRole = br.readLine();
                tmpFresh = br.readLine();
                if (tmpUsername.equals(username) && !roleChanged) {
                    if (changeRole) {
                        tmpRole = userRole == UserRole.ADMIN ? "true" : "false";
                    }
                    if (freshlyRegToFalse) {
                        tmpFresh = "false";
                    }

                    roleChanged = true;
                }

                bw.write(tmpUsername + "\n" + tmpPass + "\n" + tmpRole + "\n" + tmpFresh + "\n");
            }
        }

        Files.deleteIfExists(Paths.get("IO_files/users.txt"));
        (new File("IO_files/newUsers.txt")).renameTo(new File("IO_files/users.txt"));
    }

    public static List<List<String>> getListOfUsers() throws IOException {
        if (Files.notExists(Paths.get("IO_files/users.txt"))) {
            Files.createFile(Paths.get("IO_files/users.txt"));
        }
        List<List<String>> userList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("IO_files/users.txt"))) {
            String tmpUsername, tmpRole;
            while ((tmpUsername = br.readLine()) != null) {
                userList.add(new ArrayList<>());
                userList.get(userList.size() -1).add(tmpUsername);
                br.readLine();
                tmpRole = br.readLine();
                br.readLine();

                userList.get(userList.size() - 1).add(tmpRole.equalsIgnoreCase("Admin") ? "Admin" : "Basic");
            }
        }

        return userList;
    }

    public static void loadObjectsFromFileToLocal(final Path path) throws IOException, ClassNotFoundException {
        try(ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            escapeHole = (EscapeHole) objIn.readObject();
            depthString = (String)objIn.readObject();
            enemyList = (List<Enemy>)objIn.readObject();
            player = (Player)objIn.readObject();
            trader = (Trader<Integer, Item>)objIn.readObject();
            chest = (Chest<Item>)objIn.readObject();
            entitiesInCellsList = (ArrayList<List<List<Object>>>)objIn.readObject();
        }
    }

    public static void saveObjectsToFile(final Path path, final List<List<List<Object>>> entitiesInCellsList,
                                         final EscapeHole escapeHole, String depthString, final List<Enemy> enemies,
                                         final Player player, final Trader<Integer, Item> trader,
                                         final Chest<Item> chest) throws IOException {
        try(ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            objOut.writeObject(escapeHole);
            objOut.writeObject(depthString);
            objOut.writeObject(enemies);
            objOut.writeObject(player);
            objOut.writeObject(trader);
            objOut.writeObject(chest);
            objOut.writeObject(entitiesInCellsList);
        }

        // Can use LoginController.isFreshlyRegistered, but this is because I have to save them both in a file and in a DB
        //  (FileIO or Database, if used before the other, would change it before the other class used it).
        // Otherwise, the code would get unnecessarily more complicated
        if (userIsFreshlyRegistered(LoginController.getUserName())) {
            changeUserRoleNFreshlyReg(LoginController.getUserName(), null, false, true);
            LoginController.isFreshlyRegistered = false;
        }
    }

    public static List<List<String>> loadAllEntChangesFromFile() throws IOException {
        if (Files.notExists(Paths.get("IO_files/entity_changes.dat"))) {
            Files.createFile(Paths.get("IO_files/entity_changes.dat"));
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("IO_files/entity_changes.dat"))) {
            if (ois.available() > 0) {
                return (ArrayList<List<String>>)ois.readObject();
            }
        }
        catch (IOException | ClassNotFoundException ignored){}

        return new ArrayList<>();
    }

    public static String loadLastEntityChange() {
        try {
            if (Files.notExists(Paths.get("IO_files/entity_changes.dat"))) {
                Files.createFile(Paths.get("IO_files/entity_changes.dat"));
            }

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("IO_files/entity_changes.dat"));
            if (ois.available() > 0) {
                List<List<String>> tmpList0 = (ArrayList<List<String>>) ois.readObject();
                ois.close();
                List<String> tmpList1 = tmpList0.get(tmpList0.size() - 1);
                return "Data: " + tmpList1.get(0) + " | Old value: " + tmpList1.get(1) +
                        " | New value: " + tmpList1.get(2) + " | Changed by: " + tmpList1.get(3) +
                        " | Time and date: " + tmpList1.get(4);

            }
        }
        catch(IOException | ClassNotFoundException ignored) {}

        return "";
    }

    public static void saveEntChangesToFile(StringBuilder strBld) throws  IOException {
        List<List<String>> list = loadAllEntChangesFromFile();

        try (ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream("IO_files/entity_changes.dat"))) {
            String[] strArray = strBld.toString().split(" ! ");
            List<String> tmpList = new ArrayList<>();
            for(int i = 0; i < 5; ++i) {
                tmpList.add(strArray[i]);
            }

            list.add(tmpList);
            ous.writeObject(list);
        }
    }

    public static boolean userIsFreshlyRegistered(final String username) throws IOException {
        if (Files.notExists(Paths.get("IO_files/users.txt"))) {
            Files.createFile(Paths.get("IO_files/users.txt"));
        }

        try (BufferedReader br = new BufferedReader(new FileReader("IO_files/users.txt"))) {
            String tmpStr, tmpFresh;
            while ((tmpStr = br.readLine()) != null) {
                br.readLine();
                br.readLine();
                tmpFresh = br.readLine();
                if (tmpStr.equals(username) && tmpFresh.equalsIgnoreCase("true")) {
                    return true;
                }
            }

            return false;
        }
    }

    public static void loadNamesFromFiles(Map<String, List<String>> nameMap) throws IOException {
        Files.walk(Paths.get("IO_files/entity_names"))
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    String tmpStr, typeName;
                    Set<String> tmpSet = new HashSet<>();

                    try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
                        typeName = path.getFileName().toString().substring(0, path.getFileName().toString().length() - 10);
                        if (((tmpStr = br.readLine()) != null)) {
                            tmpSet.add(tmpStr);
                            nameMap.put(typeName, null);
                            while((tmpStr = br.readLine()) != null) {
                                tmpSet.add(tmpStr);
                            }
                            nameMap.put(typeName, new ArrayList<>(tmpSet));
                        }
                    }
                    catch (IOException e) {
                        MainController.logger.error(e.getMessage(), e);
                    }
                });
    }
}
