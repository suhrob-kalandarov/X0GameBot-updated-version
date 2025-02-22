package org.exp.botservice.playwithfriendervice;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;

public class Main {
    private static final String BOT_TOKEN = "7937505866:AAHMrYPiJhYxmdvYXEhSaAgtG9yQOUYtDVM"; // Bot tokenini kiriting
    private static final TelegramBot bot = new TelegramBot(BOT_TOKEN);

    private static final Map<Long, String> players = new HashMap<>(); // O'yinchilar ro'yxati
    private static final Map<Long, String[][]> gameBoards = new HashMap<>(); // O'yin doskalari
    private static final Map<Long, Timer> gameTimers = new HashMap<>(); // O'yin vaqtini boshqarish

    public static void main(String[] args) {
        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    if (update.message() != null && update.message().text() != null) {
                        handleMessage(update.message());

                    } else if (update.callbackQuery() != null) {
                        //handleInlineQuery(update.inlineQuery());
                        handleCallbackQuery(update.callbackQuery());

                    } else if (update.inlineQuery() != null) {

                        InlineQuery inlineQuery = update.inlineQuery();

                        Long userId = inlineQuery.from().id();
                        // Yangi o'yin doskasi yaratish
                        String[][] newBoard = new String[3][3];
                        gameBoards.put(userId, newBoard);

                        // O'yinchi uchun boshlang'ich belgi (X) ni qo'shish
                        players.put(userId, "❌"); // <-- Asosiy o'zgarish

                        handleInlineQuery(update.inlineQuery());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
        System.out.println("✅ Bot ishga tushdi...");
    }

    /*
    private static TgUser getOrCreateTgUser(Long chatId) {
        Optional<TgUser> first = DB.TG_USER_LIST.stream().filter(tgUser -> tgUser.getChatId().equals(chatId)).findFirst();
        if (first.isPresent()) {
            return first.get();
        } else {
            TgUser tgUser = TgUser.builder().chatId(chatId).build();
            DB.TG_USER_LIST.add(tgUser);
            return tgUser;
        }
    }*/

    private static void handleMessage(Message message) {
        Long chatId = message.chat().id();
        String text = message.text();

        if (text.equals("/start")) {
            sendMainMenu(chatId);
        }
    }

    private static void sendMainMenu(Long chatId) {
        bot.execute(
                new SendMessage(
                        chatId,
                        "Tic-Tac-Toe o'yiniga xush kelibsiz! Do‘stingiz bilan o‘ynash uchun tugmani bosing."
                )
                        .replyMarkup(
                                new InlineKeyboardMarkup(new InlineKeyboardButton("🎮 Play with Friend").switchInlineQuery(""))
                        )
        );
    }

    private static void handleInlineQuery(InlineQuery inlineQuery) {
        String queryId = inlineQuery.id();

        // InlineKeyboardMarkup (3x3 doska)
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton("⬜").callbackData("move_0_0"),
                        new InlineKeyboardButton("⬜").callbackData("move_0_1"),
                        new InlineKeyboardButton("⬜").callbackData("move_0_2")
                },
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton("⬜").callbackData("move_1_0"),
                        new InlineKeyboardButton("⬜").callbackData("move_1_1"),
                        new InlineKeyboardButton("⬜").callbackData("move_1_2")
                },
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton("⬜").callbackData("move_2_0"),
                        new InlineKeyboardButton("⬜").callbackData("move_2_1"),
                        new InlineKeyboardButton("⬜").callbackData("move_2_2")
                }
        );


        // Inline natijani yuborish
        InputTextMessageContent messageContent = new InputTextMessageContent("🚀 O'yin boshlandi!")
                .parseMode(ParseMode.Markdown);

        InlineQueryResultArticle gameResult = new InlineQueryResultArticle("tic_tac_toe", "O'yin Doskasi", messageContent)
                .replyMarkup(keyboard);

        bot.execute(new AnswerInlineQuery(queryId, gameResult));
    }

    private static void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        Long userId = callbackQuery.from().id();

        // Inline xabar ID sini olish
        String inlineMessageId = callbackQuery.inlineMessageId();

        if (inlineMessageId != null) {
            // Inline xabar uchun ishlov berish
            if (data.startsWith("move_")) {
                makeMoveInline(userId, inlineMessageId, data);
            }
        } else {
            // Oddiy xabar uchun ishlov berish
            Long chatId = callbackQuery.message().chat().id();
            if (data.equals("restart")) {
                restartGame(chatId);
            } else if (data.startsWith("move_")) {
                makeMove(chatId, userId, data);
            }
        }
    }

    private static void makeMoveInline(Long userId, String inlineMessageId, String data) {
        // Agar doska mavjud bo'lmasa, yangi doska yaratish
        String[][] board = gameBoards.get(userId);
        if (board == null) {
            board = new String[3][3];
            gameBoards.put(userId, board);
        }

        // Agar o'yinchi mavjud bo'lmasa, boshlang'ich belgini (X) qo'ying
        if (players.get(userId) == null) {
            players.put(userId, "❌");
        }

        int row = Integer.parseInt(data.split("_")[1]);
        int col = Integer.parseInt(data.split("_")[2]);

        if (board[row][col] == null) {
            board[row][col] = players.get(userId);
            players.put(userId, players.get(userId).equals("❌") ? "⭕" : "❌");
            updateInlineGameBoard(userId, inlineMessageId, board);
        }
    }

    private static void updateInlineGameBoard(Long userId, String inlineMessageId, String[][] board) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton(board[0][0] == null ? "⬜" : board[0][0]).callbackData("move_0_0"),
                        new InlineKeyboardButton(board[0][1] == null ? "⬜" : board[0][1]).callbackData("move_0_1"),
                        new InlineKeyboardButton(board[0][2] == null ? "⬜" : board[0][2]).callbackData("move_0_2")
                },
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton(board[1][0] == null ? "⬜" : board[1][0]).callbackData("move_1_0"),
                        new InlineKeyboardButton(board[1][1] == null ? "⬜" : board[1][1]).callbackData("move_1_1"),
                        new InlineKeyboardButton(board[1][2] == null ? "⬜" : board[1][2]).callbackData("move_1_2")
                },
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton(board[2][0] == null ? "⬜" : board[2][0]).callbackData("move_2_0"),
                        new InlineKeyboardButton(board[2][1] == null ? "⬜" : board[2][1]).callbackData("move_2_1"),
                        new InlineKeyboardButton(board[2][2] == null ? "⬜" : board[2][2]).callbackData("move_2_2")
                }
        );

        // Inline xabarni yangilash
        bot.execute(new EditMessageText(inlineMessageId, "Navbat: " + players.get(userId))
                .replyMarkup(keyboard));
    }

    private static void makeMove(Long chatId, Long userId, String data) {
        String[][] board = gameBoards.get(chatId);
        int row = Integer.parseInt(data.split("_")[1]);
        int col = Integer.parseInt(data.split("_")[2]);

        if (board[row][col] == null) {
            board[row][col] = players.get(chatId);
            players.put(chatId, players.get(chatId).equals("❌") ? "⭕" : "❌"); // Navbatni almashtirish
            sendGameBoard(chatId);
        } else {
            bot.execute(new SendMessage(chatId, "Bu joy band! Boshqa joy tanlang."));
        }
    }

    private static void sendGameBoard(Long chatId) {
        String[][] board = gameBoards.get(chatId);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup(
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton(board[0][0] == null ? "⬜" : board[0][0]).callbackData("move_0_0"),
                        new InlineKeyboardButton(board[0][1] == null ? "⬜" : board[0][1]).callbackData("move_0_1"),
                        new InlineKeyboardButton(board[0][2] == null ? "⬜" : board[0][2]).callbackData("move_0_2")
                },
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton(board[1][0] == null ? "⬜" : board[1][0]).callbackData("move_1_0"),
                        new InlineKeyboardButton(board[1][1] == null ? "⬜" : board[1][1]).callbackData("move_1_1"),
                        new InlineKeyboardButton(board[1][2] == null ? "⬜" : board[1][2]).callbackData("move_1_2")
                },
                new InlineKeyboardButton[] {
                        new InlineKeyboardButton(board[2][0] == null ? "⬜" : board[2][0]).callbackData("move_2_0"),
                        new InlineKeyboardButton(board[2][1] == null ? "⬜" : board[2][1]).callbackData("move_2_1"),
                        new InlineKeyboardButton(board[2][2] == null ? "⬜" : board[2][2]).callbackData("move_2_2")
                }
        );
        bot.execute(new SendMessage(chatId, "Navbat: " + players.get(chatId)).replyMarkup(keyboard));

       /* if (checkWin(board, chatId)) {
            refreshGameBoard();
        }*/
    }

    private static void restartGame(Long chatId) {
        gameBoards.put(chatId, new String[3][3]);
        players.put(chatId, "❌");
        sendGameBoard(chatId);
    }

    private static void refreshGameBoard(String inlineMessageId, String resultMsg){
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        keyboard.addRow(
                new InlineKeyboardButton("🔄").callbackData("restart"),
                new InlineKeyboardButton("🤖").callbackData("@GameOXXO_bot")
        );
        bot.execute(
                new EditMessageText(inlineMessageId, "🎉 Result:\n" + resultMsg)
        );
    }

    private static void checkWin(String[][] board, String inlineMessageId) {
        String winner = null;

        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2])) {
                winner = board[i][0];
            }
            if (board[0][i] != null && board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i])) {
                winner = board[0][i];
            }
        }

        if (board[0][0] != null && board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2])) {
            winner = board[0][0];
        }
        if (board[0][2] != null && board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0])) {
            winner = board[0][2];
        }

        if (winner != null) {
            //bot.execute(new EditMessageText(inlineMessageId, "🎉 G'olib: " + winner));
            gameBoards.remove(inlineMessageId);
        }
    }
}
