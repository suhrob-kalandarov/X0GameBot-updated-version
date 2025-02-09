package org.exp.botservice.service;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.exp.entity.tguserentities.TgUser;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/game0x";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root123";
    private static final Gson GSON = new Gson();

    @SneakyThrows
    public static TgUser getOrUpdateUser(Long chatId, String username) {
        String sql = """
            INSERT INTO tg_user (chat_id, username)
            VALUES (?, ?)
            ON CONFLICT (chat_id) DO UPDATE SET
                username = EXCLUDED.username
            RETURNING *;
            """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, chatId);
            pstmt.setString(2, username);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return TgUser.builder()
                            .chatId(rs.getLong("chat_id"))
                            .username(rs.getString("username"))
                            .userScore(rs.getInt("user_score"))
                            .drawScore(rs.getInt("draw_score"))
                            .botScore(rs.getInt("bot_score"))
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return TgUser.builder().chatId(chatId).username(username).build();
    }

    public static void updateAllScores(Long chatId, int userScore, int drawScore, int botScore) {
        String sql = """
        UPDATE tg_user 
        SET user_score = ?, 
            draw_score = ?, 
            bot_score = ? 
        WHERE chat_id = ?;
        """;

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userScore);
            pstmt.setInt(2, drawScore);
            pstmt.setInt(3, botScore);
            pstmt.setLong(4, chatId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static void exportToJson() {
        String filePath = "org/exp/database/TgUsers.json";
        String sql = "SELECT * FROM tg_user";
        List<Map<String, Object>> result = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                result.add(row);
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                GSON.toJson(result, writer);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
