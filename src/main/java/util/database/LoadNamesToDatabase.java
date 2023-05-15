package util.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileIO;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadNamesToDatabase {
    // Used manually by calling it in MainApplication and removing it after it executes once
    // Goal: to put non player names into the database ONCE

    private static Logger logger = LoggerFactory.getLogger(LoadNamesToDatabase.class);

    public static void updateFromFileToDB() {
        Map<String, List<String>> nameMap = new HashMap<>();

        try {
            FileIO.loadNamesFromFiles(nameMap);
            updateDB(nameMap);
        }
        catch (IOException | SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static void updateDB(Map<String, List<String>> nameMap) throws IOException, SQLException{
        PreparedStatement preparedStatement;
        try (Connection connection = Database.connectToDataBase()) {
            for (String key: nameMap.keySet()) {
                preparedStatement = connection
                        .prepareStatement("INSERT INTO " + key.toUpperCase() + "_NAMES(name) VALUES (?)");
                for(String s: nameMap.get(key)) {
                    preparedStatement.setString(1, s);
                    preparedStatement.executeUpdate();
                }
            }
        }
    }
}
