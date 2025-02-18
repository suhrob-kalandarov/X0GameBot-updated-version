package org.exp.database;
import java.sql.*;

public interface DataBase {

    String DB_URL = "jdbc:postgresql://localhost:5432/game0x";
    String DB_USER = "postgres";
    String DB_PASSWORD = "root123";

    static void main(String[] args) throws SQLException {
        getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    static Connection getConnection(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
