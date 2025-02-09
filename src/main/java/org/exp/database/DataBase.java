package org.exp.database;

import com.google.gson.Gson;
import java.sql.*;

public interface DataBase {

    String DB_URL = "jdbc:postgresql://localhost:5432/game0x";
    String DB_USER = "postgres";
    String DB_PASSWORD = "root123";
    Gson GSON = new Gson();

    static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
