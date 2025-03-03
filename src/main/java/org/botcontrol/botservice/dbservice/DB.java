package org.botcontrol.botservice.dbservice;

import com.pengrad.telegrambot.model.Update;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;

import java.sql.*;
import java.util.*;

public interface DB {

    Logger logger = LogManager.getLogger(DB.class);
    
    // Method get user or create new user
    static User getOrCreateUser(Update update) {
        User user = null;

        if (update.callbackQuery() != null) {
            user = getUserFromDatabase(update.callbackQuery().from().id());
            Objects.requireNonNull(user).setGameBoard(getGameBoard(user.getUserId()));
            return user;
        }

        Long userId = update.message().from().id();
        
        user = getUserFromDatabase(userId);
        if (user != null) {
            if (!isUserExists("games", userId)) {
                insertGames(userId);
            }
            if (!isUserExists("game_status", userId)) {
                insertDefaultGameStatus(userId);
            }
            return user;
        }

        user = User.builder()
                .userId(userId)
                .fullName(getUserFullName(update))
                .username(update.message().chat().username())
                .difficultyLevel("easy")
                .messageId(null)
                .userState(UserState.START)
                .language("en")
                .build();
        insertUserIntoDatabase(user);
        insertDefaultGameStatus(userId);
        user.setGameBoard(getGameBoard(user.getUserId()));
        insertGames(userId);

        return user;
    }
    

    static String getUserFullName(Update update) {
        String firstName = update.message().chat().firstName();
        String lastName = update.message().chat().lastName();

        if (firstName == null && lastName == null) {
            return "user";
        }

        if (firstName == null) {
            return lastName;
        }

        if (lastName == null) {
            return firstName;
        }

        return firstName + " " + lastName;
    }


    static boolean isUserExists(String tableName, Long userId) {

        try (PreparedStatement preparedStatement = DatabaseManager.getConnection().prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM " + tableName + " WHERE user_id = ?)"
        )
        ) {
            preparedStatement.setLong(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    static void insertGames(long userId) {
        String query = "INSERT INTO games (user_id) VALUES (?) ON CONFLICT (user_id) DO NOTHING";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);

            int rowsInserted = statement.executeUpdate();
            System.out.println(rowsInserted > 0 ? "✅ Game inserted for user: " + userId : "⚠️ Game already exists");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private static void insertUserIntoDatabase(User user) {
        String query = "INSERT INTO users (user_id, full_name, username, language, difficulty_level, message_id, user_state) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (user_id) DO UPDATE SET " +
                "full_name = EXCLUDED.full_name, " +
                "username = EXCLUDED.username, " +
                "language = EXCLUDED.language, " +
                "difficulty_level = EXCLUDED.difficulty_level, " +
                "message_id = null, " +
                "user_state = EXCLUDED.user_state";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)
        ){
            statement.setLong(1, user.getUserId());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getLanguage());
            statement.setString(5, user.getDifficultyLevel());
            statement.setInt(6, 0);
            statement.setString(7, String.valueOf(user.getUserState()));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    static void insertDefaultGameStatus(long userId) {
        String query = "INSERT INTO game_status (user_id, difficulty_level, win_count, draw_count, lose_count) VALUES " +
                "(?, 'easy', 0, 0, 0), " +
                "(?, 'medium', 0, 0, 0), " +
                "(?, 'hard', 0, 0, 0), " +
                "(?, 'extreme', 0, 0, 0)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)
        ){
            statement.setLong(1, userId);
            statement.setLong(2, userId);
            statement.setLong(3, userId);
            statement.setLong(4, userId);

            int rowsInserted = statement.executeUpdate();
            System.out.println(rowsInserted > 0 ? "✅ Game status default values inserted" : "⚠️ No rows inserted");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    static void updateGameBoard(long userId, int[][] board) {
        String query = "UPDATE games SET game_board = ? WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // PostgreSQL 2D massivi formatiga o'tkazish
            Array sqlArray = connection.createArrayOf("INTEGER", flattenArray(board));

            statement.setArray(1, sqlArray);
            statement.setLong(2, userId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ Game board updated" : "⚠️ No rows updated");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private static Integer[] flattenArray(int[][] board) {
        return Arrays.stream(board)
                .flatMapToInt(Arrays::stream)
                .boxed()
                .toArray(Integer[]::new);
    }
    

    // 2D int arrayni PostgreSQL formatiga o'tkazish
    private static String arrayToPostgresString(int[][] board) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < board.length; i++) {
            sb.append("{");
            for (int j = 0; j < board[i].length; j++) {
                sb.append(board[i][j]);
                if (j < board[i].length - 1) sb.append(",");
            }
            sb.append("}");
            if (i < board.length - 1) sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }


    static int[][] getGameBoard(long userId) {
        String query = "SELECT game_board FROM games WHERE user_id = ?";
        int[][] board = new int[3][3]; // Default 3x3 bo'sh taxta

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                String boardString = rs.getString("game_board");
                board = parsePostgresArray(boardString);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return board;
    }


    // PostgreSQL string array formatini Java int[][] ga o‘girish
    private static int[][] parsePostgresArray(String boardString) {
        boardString = boardString.replaceAll("[{}\"]", ""); // Barcha qavs va qo'shtirnoqlarni olib tashlash
        String[] values = boardString.split(","); // Virgullar bo'yicha ajratish

        int size = (int) Math.sqrt(values.length); // O'lchamni hisoblash
        int[][] board = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Integer.parseInt(values[i * size + j].trim()); // Toza int ga aylantirish
            }
        }
        return board;
    }


    static String getUserSign(long userId) {
        String query = "SELECT user_sign FROM games WHERE user_id = ?";
        String userSign = null;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                userSign = rs.getString("user_sign");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userSign;
    }


    static void updateUserSign(long userId, String userSign) {
        String query = "UPDATE games SET user_sign = ? WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userSign);
            statement.setLong(2, userId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ User sign updated");
            } else {
                System.out.println("⚠️ User not found, inserting new game entry...");
                insertGames(userId); // ✅ Agar user topilmasa, yangi o'yin yozuvi qo'shiladi
                updateUserSign(userId, userSign); // ✅ Keyin yana update qilishga harakat qilamiz
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static String getBotSign(long userId) {
        String query = "SELECT bot_sign FROM games WHERE user_id = ?";
        String botSign = null;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                botSign = rs.getString("bot_sign");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return botSign;
    }


    static void updateBotSign(long userId, String botSign) {
        String query = "UPDATE games SET bot_sign = ? WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, botSign);
            statement.setLong(2, userId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ Bot sign updated successfully" : "⚠️ No rows updated");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static User getUserFromDatabase(Long userId) {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)
        ){
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return User.builder()
                        .userId(resultSet.getLong("user_id"))
                        .fullName(resultSet.getString("full_name"))
                        .username(resultSet.getString("username"))
                        .language(resultSet.getString("language"))
                        .difficultyLevel(resultSet.getString("difficulty_level"))
                        .messageId(resultSet.getInt("message_id"))
                        .userState(UserState.valueOf(resultSet.getString("user_state")))
                        .build();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    static void updateLanguage(long userId, String language) {
        String query = "UPDATE users SET language = ? WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, language);
            statement.setLong(2, userId);

            int rowsUpdated = statement.executeUpdate();
            System.out.println(rowsUpdated > 0 ? "✅ Language yangilandi" : "⚠️ Foydalanuvchi topilmadi");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static String getLanguage(long userId) {
        String query = "SELECT language FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("language");
            } else {
                System.out.println("⚠️ Til topilmadi. Foydalanuvchi mavjud emas!");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    static String getUserScores(long chatId) {
        String query = "SELECT difficulty_level, win_count, draw_count, lose_count " +
                "FROM game_status WHERE user_id = ? ORDER BY " +
                "CASE " +
                "WHEN difficulty_level = 'easy' THEN 1 " +
                "WHEN difficulty_level = 'medium' THEN 2 " +
                "WHEN difficulty_level = 'hard' THEN 3 " +
                "WHEN difficulty_level = 'extreme' THEN 4 " +
                "ELSE 5 END";

        StringBuilder result = new StringBuilder("\s\s\s\s\s🏆   ⚖️   😭\n");

        Map<String, String> emojiMap = Map.of(
                "easy", "👶",
                "medium", "😎",
                "hard", "😈",
                "extreme", "🥶"
        );

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, chatId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String level = resultSet.getString("difficulty_level");
                int win = resultSet.getInt("win_count");
                int draw = resultSet.getInt("draw_count");
                int lose = resultSet.getInt("lose_count");

                String emoji = emojiMap.getOrDefault(level, "❓"); // Agar noto‘g‘ri level bo‘lsa
                result.append(String.format("%s:  %-4d %-4d %-4d%n", emoji, win, draw, lose));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error fetching scores!";
        }

        return result.toString();
    }


    static int getUserGameStat(long userId, String difficultyLevel, String msg) {
        // Xavfsiz qidirish uchun msg faqat ushbu ustun nomlaridan biri bo‘lishi kerak
        List<String> validColumns = List.of("win_count", "draw_count", "lose_count");

        if (!validColumns.contains(msg)) {
            throw new IllegalArgumentException("Invalid column name: " + msg);
        }

        String query = String.format("SELECT %s FROM game_status WHERE user_id = ? AND difficulty_level = ?", msg);

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            statement.setString(2, difficultyLevel);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(msg); // Foydalanuvchi natijasi mavjud bo‘lsa, qiymatni qaytarish
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Agar ma'lumot topilmasa, 0 qaytadi
    }


    static void updateGameScore(long userId, String difficultyLevel, String scoreType, int newScore) {
        // 1. scoreType faqat ushbu qiymatlarda bo‘lishi kerak: win_count, draw_count, lose_count
        if (!scoreType.equals("win_count") && !scoreType.equals("draw_count") && !scoreType.equals("lose_count")) {
            System.out.println("⚠️ Xato: Noto‘g‘ri score turi!");
            return;
        }

        // 2. SQL UPDATE query
        String query = "UPDATE game_status SET " + scoreType + " = ? WHERE user_id = ? AND difficulty_level = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)
        ){
            // 3. Parametrlarni to‘ldirish
            statement.setInt(1, newScore);
            statement.setLong(2, userId);
            statement.setString(3, difficultyLevel);

            // 4. Update bajaramiz
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ " + difficultyLevel + " darajadagi " + scoreType + " yangilandi: " + newScore);
            } else {
                System.out.println("⚠️ Foydalanuvchi yoki daraja topilmadi!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static List<User> getAllUsersFromDatabase() {
        List<User> userList = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User user = User.builder()
                        .userId(resultSet.getLong("user_id"))
                        .fullName(resultSet.getString("full_name"))
                        .username(resultSet.getString("username"))
                        .language(resultSet.getString("language"))
                        .difficultyLevel(resultSet.getString("difficulty_level"))
                        .messageId(resultSet.getInt("message_id"))
                        .userState(UserState.valueOf(resultSet.getString("user_state")))
                        .build();
                userList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userList;
    }


    static int getMessageId(long userId) {
        String query = "SELECT message_id FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("message_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    static void updateMessageId(long userId, int messageId) {
        if (userId <= 0 || messageId <= 0) {
            logger.warn("Noto'g'ri userId yoki messageId: userId={}, messageId={}", userId, messageId);
            return;
        }

        String query = "UPDATE users SET message_id = ? WHERE user_id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Foydalanuvchi mavjudligini tekshirish
            String checkUserQuery = "SELECT user_id FROM users WHERE user_id = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkUserQuery)) {
                checkStatement.setLong(1, userId);
                ResultSet resultSet = checkStatement.executeQuery();
                if (!resultSet.next()) {
                    logger.warn("Foydalanuvchi bazada topilmadi: {}", userId);
                    return;
                }
            }

            // message_id ni yangilash
            statement.setInt(1, messageId);
            statement.setLong(2, userId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("✅ message_id yangilandi: userId={}, messageId={}", userId, messageId);
            } else {
                logger.warn("⚠️ Foydalanuvchi topilmadi: userId={}", userId);
            }
        } catch (SQLException e) {
            logger.error("Bazada xatolik: {}", e.getMessage(), e);
        }
    }

    static String getUserState(long userId) {
        String query = "SELECT user_state FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("user_state");
            } else {
                System.out.println("⚠️ user_state topilmadi. Foydalanuvchi mavjud emas!");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void updateUserState(long userId, String newState) {
        String query = "UPDATE users SET user_state = ? WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newState);
            statement.setLong(2, userId);

            if (statement.executeUpdate() > 0) {
                logger.info("✅ user_state yangilandi: {}", newState);
            } else {
                logger.error("⚠️ Foydalanuvchi topilmadi! state: {}", newState);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // users jadvalidan difficulty_level ni olish
    static String getDifficultyLevel(long userId) {
        String query = "SELECT difficulty_level FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("difficulty_level");
            } else {
                System.out.println("⚠️ Foydalanuvchi topilmadi!");
                logger.error("⚠️ Foydalanuvchi topilmadi! user id: {}", userId);
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    // users jadvalidagi difficulty_level ni yangilash
    static void updateDifficultyLevel(long userId, String level) {
        String query = "UPDATE users SET difficulty_level = ? WHERE user_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, level);
            statement.setLong(2, userId);

            if (statement.executeUpdate() > 0) {
                logger.info("✅ Qiyinchilik darajasi yangilandi: {}", level);
            } else {
                logger.error("⚠️ Foydalanuvchi topilmadi! level: {}", level);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
