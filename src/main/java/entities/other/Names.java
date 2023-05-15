package entities.other;

import util.database.Database;
import util.FileIO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Names() {
    static public Map<String, List<String>> nameMap = new HashMap<>();

    static public void initLoadFromFiles() throws IOException {
        FileIO.loadNamesFromFiles(nameMap);
    }

    public static void initLoadFromDB() throws SQLException, IOException {
        nameMap = Database.loadNamesFromDB();
    }
}
