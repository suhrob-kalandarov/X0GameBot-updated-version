package org.exp.database;

import org.exp.entity.adminentities.Admin;
import org.exp.entity.tguserentities.TgUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DB {
    //EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("default");
    Path dirPath = Paths.get("src/main/java/org/files");
    List<TgUser> TG_USERS_LIST = new ArrayList<>();
    List<Admin> ADMIN_LIST = new ArrayList<>(List.of(Admin.builder().chatId(6513286717L).build()));

    static TgUser getOrCreateUser(Long chatId) {
        // 1. List ichida bor-yo‘qligini tekshiramiz
        Optional<TgUser> optionalTgUser = TG_USERS_LIST.stream()
                .filter(item -> item.getChatId().equals(chatId))
                .findFirst();

        if (optionalTgUser.isPresent()) {
            return optionalTgUser.get(); // Agar listda bo‘lsa, qaytaramiz
        }

        // 2. Agar listda bo‘lmasa, DB dan tekshiramiz
        TgUser dbUser = getUserFromDatabase(chatId);
        if (dbUser != null) {
            TG_USERS_LIST.add(dbUser); // DB dagi userni listga qo‘shamiz
            return dbUser; // Uni qaytaramiz
        }

        // 3. Agar DB da ham bo‘lmasa, yangi user yaratamiz va DB ga qo‘shamiz
        TgUser newUser = TgUser.builder()
                .chatId(chatId)
                .username(null)
                .userScore(0)
                .drawScore(0)
                .botScore(0)
                .language("en") // Default til
                .build();

        TG_USERS_LIST.add(newUser); // Listga qo‘shamiz
        insertUserIntoDatabase(newUser); // DB ga qo‘shamiz

        return newUser; // Yangi userni qaytaramiz
    }


/*    static TgUser getOrCreateUser(Long chatId) {
        Optional<TgUser> optionalTgUser = TG_USERS_LIST.stream()
                .filter(item ->
                        item.getChatId().equals(chatId))
                .findFirst();
        if (optionalTgUser.isPresent()) {
            return optionalTgUser.get();
        }
        else {
            TgUser tgUser = TgUser
                    .builder()
                    .chatId(chatId)
                    .build();
            TG_USERS_LIST.add(tgUser);
            TgUser user = TgUser.builder()
                    .chatId(chatId)
                    .username(null)
                    .userScore(0)
                    .drawScore(0)
                    .botScore(0)
                    .language("en") // Default til
                    .build();

            insertUserIntoDatabase(user);
            return tgUser;
        }
    }*/

/*    static TgUser getOrCreateUser(Long chatId, String username) {
        TgUser user = getUserFromDatabase(chatId);
        if (user != null) {
            return user;
        }

        // Yangi foydalanuvchini yaratish va bazaga qo‘shish
        user = TgUser.builder()
                .chatId(chatId)
                .username(username)
                .userScore(0)
                .drawScore(0)
                .botScore(0)
                .language("en") // Default til
                .build();

        insertUserIntoDatabase(user);
        return user;
    }*/

    private static void insertUserIntoDatabase(TgUser user) {
        String query = "INSERT INTO game0xx0 (chat_id, username, user_score, draw_score, bot_score, language) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, user.getChatId());
            statement.setString(2, user.getUsername());
            statement.setInt(3, user.getUserScore());
            statement.setInt(4, user.getDrawScore());
            statement.setInt(5, user.getBotScore());
            statement.setString(6, user.getLanguage());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void updateUsername(long chatId, String newUsername) {
        String query = "UPDATE game0xx0 SET username = ? WHERE chat_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newUsername);
            statement.setLong(2, chatId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Username muvaffaqiyatli yangilandi!");
            } else {
                System.out.println("⚠️ Username yangilanmadi. Chat ID topilmadi!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static String getUsername(long chatId) {
        String query = "SELECT username FROM game0xx0 WHERE chat_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("username");
            } else {
                System.out.println("⚠️ Username topilmadi. Chat ID mavjud emas!");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    static String getLanguage(long chatId) {
        String query = "SELECT language FROM game0xx0 WHERE chat_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("language");
            } else {
                System.out.println("⚠️ Til topilmadi. Chat ID mavjud emas!");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private static TgUser getUserFromDatabase(Long chatId) {
        String query = "SELECT * FROM game0xx0 WHERE chat_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)
        ) {
            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return TgUser.builder()
                        .chatId(resultSet.getLong("chat_id"))
                        .username(resultSet.getString("username"))
                        .userScore(resultSet.getInt("user_score"))
                        .drawScore(resultSet.getInt("draw_score"))
                        .botScore(resultSet.getInt("bot_score"))
                        .language(resultSet.getString("language"))
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    static Map<String, Integer> getAllScores(long chatId) {
        String query = "SELECT user_score, draw_score, bot_score FROM game0xx0 WHERE chat_id = ?";
        Map<String, Integer> scores = new HashMap<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                scores.put("user_score", resultSet.getInt("user_score"));
                scores.put("draw_score", resultSet.getInt("draw_score"));
                scores.put("bot_score", resultSet.getInt("bot_score"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    static int getUserScore(long chatId) {
        String query = "SELECT user_score FROM game0xx0 WHERE chat_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("user_score");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Agar topilmasa -1 qaytariladi
    }

    static int getDrawScore(long chatId) {
        String query = "SELECT draw_score FROM game0xx0 WHERE chat_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("draw_score");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Agar topilmasa -1 qaytariladi
    }


    static int getBotScore(long chatId) {
        String query = "SELECT bot_score FROM game0xx0 WHERE chat_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("bot_score");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Agar topilmasa -1 qaytariladi
    }


    static int getMessageId(long chatId) {
        String query = "SELECT message_id FROM game0xx0 WHERE chat_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("message_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Agar topilmasa -1 qaytariladi
    }




    static void updateMessageId(long chatId, int messageId) {
        String query = "UPDATE game0xx0 SET message_id = ? WHERE chat_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, messageId);
            statement.setLong(2, chatId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ message_id yangilandi" : "⚠️ Foydalanuvchi topilmadi");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void updateLanguage(long chatId, String language) {
        String query = "UPDATE game0xx0 SET language = ? WHERE chat_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, language);
            statement.setLong(2, chatId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ language yangilandi" : "⚠️ Foydalanuvchi topilmadi");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static void updateScores(long chatId, int userScore, int drawScore, int botScore) {
        String query = "UPDATE game0xx0 SET user_score = ?, draw_score = ?, bot_score = ? WHERE chat_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userScore);
            statement.setInt(2, drawScore);
            statement.setInt(3, botScore);
            statement.setLong(4, chatId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ score yangilandi" : "⚠️ Foydalanuvchi topilmadi");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void updateUserScore(long chatId, int userScore) {
        String query = "UPDATE game0xx0 SET user_score = ? WHERE chat_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userScore);
            statement.setLong(2, chatId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ user_score yangilandi" : "⚠️ Foydalanuvchi topilmadi");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void updateDrawScore(long chatId, int drawScore) {
        String query = "UPDATE game0xx0 SET draw_score = ? WHERE chat_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, drawScore);
            statement.setLong(2, chatId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ draw_score yangilandi" : "⚠️ Foydalanuvchi topilmadi");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void updateBotScore(long chatId, int botScore) {
        String query = "UPDATE game0xx0 SET bot_score = ? WHERE chat_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, botScore);
            statement.setLong(2, chatId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ bot_score yangilandi" : "⚠️ Foydalanuvchi topilmadi");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    static boolean isAdmin(TgUser tgUser) {
        return ADMIN_LIST.stream()
                .anyMatch(
                        admin -> admin.getChatId().equals(tgUser.getChatId())
                );
    }

    static Admin getAdmin(Long chatId) {
        return ADMIN_LIST.stream()
                .filter(admin -> admin.getChatId().equals(chatId))
                .findFirst().get();
    }

    static List<String> getFileList() {
        // 1. Papka mavjudligini tekshirish
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            return List.of(); // Bo'sh ro‘yxat qaytarish
        }

        // 2. Fayllarni ro‘yxatga olish
        try (Stream<Path> filesStream = Files.list(dirPath)) {
            return filesStream
                    .filter(Files::isRegularFile) // Faqat oddiy fayllarni olish
                    .map(path -> path.getFileName().toString()) // Fayl nomlarini olish
                    .collect(Collectors.toList()); // List<String> shaklida yig‘ish
        } catch (IOException e) {
            return List.of(); // Xatolik bo‘lsa ham bo‘sh ro‘yxat qaytarish
        }
    }
}

