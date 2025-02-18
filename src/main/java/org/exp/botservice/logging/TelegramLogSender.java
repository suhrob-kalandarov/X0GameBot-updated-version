package org.exp.botservice.logging;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TelegramLogSender {
    private final TelegramBot bot;
    private final String chatId;

    public TelegramLogSender(String botToken, String chatId) {
        this.bot = new TelegramBot(botToken);
        this.chatId = chatId;
    }

    public void sendDailyLog() {
        try {
            // Bugungi sana bo‘yicha log fayl nomini olish
            String logFileName = "logs/game0xx0-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
            File logFile = new File(logFileName);

            // Fayl mavjudligini tekshirish
            if (logFile.exists()) {
                long fileSizeInMB = logFile.length() / (1024 * 1024); // Fayl hajmi MB da
                if (fileSizeInMB > 50) {
                    bot.execute(new SendMessage(chatId, "⚠️ Log fayli hajmi 50 MB dan katta, yuborilmadi!"));
                    return;
                }

                // Telegramga yuborish
                bot.execute(new SendDocument(chatId, logFile));
                bot.execute(new SendMessage(chatId, "✅ Kunlik log fayli yuborildi: " + logFileName));

                // Faylni arxivlash va eski faylni o‘chirish
                archiveLogFile(logFile);
            } else {
                bot.execute(new SendMessage(chatId, "⚠️ Bugungi log fayli topilmadi!"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                bot.execute(new SendMessage(chatId, "❌ Log fayl yuborishda xatolik: " + e.getMessage()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void archiveLogFile(File logFile) {
        try {
            File archiveDir = new File("logs/archive");
            if (!archiveDir.exists()) {
                archiveDir.mkdirs();
            }

            File archivedFile = new File(archiveDir, logFile.getName());
            Files.move(logFile.toPath(), archivedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            bot.execute(new SendMessage(chatId, "🗄 Log fayli arxivlandi: " + archivedFile.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            bot.execute(new SendMessage(chatId, "❌ Log faylni arxivlashda xatolik!"));
        }
    }
}
