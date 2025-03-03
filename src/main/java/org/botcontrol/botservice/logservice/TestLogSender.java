package org.botcontrol.botservice.logservice;

import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import org.botcontrol.Main;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestLogSender {
    public static void main(String[] args) {
        // Bot tokeni va chat ID
        String chatId = Main.chatId;

        // Bugungi sana bo‘yicha log fayl nomini olish
        String logFileName = "java/org/logfiles/game0xx0-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
        File logFile = new File(logFileName);

        File logDir = new File("java/org/logfiles/logs");
        if (logDir.exists() && logDir.isDirectory()) {
            File[] files = logDir.listFiles();
            System.out.println("Logs papkasidagi fayllar:");
            if (files != null) {
                for (File file : files) {
                    System.out.println("➡️ " + file.getName());
                }
            }
        } else {
            System.out.println("❌ Logs papkasi topilmadi!");
        }

        // Fayl mavjudligini tekshirish
        if (logFile.exists()) {
            Main.telegramBot.execute(new SendDocument(chatId, logFile));
            Main.telegramBot.execute(new SendMessage(chatId, "✅ Test uchun log fayli yuborildi!"));

            archiveLogFile(logFile);
            System.out.println("Log fayli yuborildi!");
        } else {
            System.out.println("⚠️ Log fayli topilmadi: " + logFileName);
        }
    }

    private static void archiveLogFile(File logFile) {
        try {
            File archiveDir = new File("java/org/logfiles/archive");
            if (!archiveDir.exists()) {
                archiveDir.mkdirs();
            }

            File archivedFile = new File(archiveDir, logFile.getName());
            Files.move(logFile.toPath(), archivedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            //MultiplayerFriend.telegramBot.execute(new SendMessage(chatId, "🗄 Log fayli arxivlandi: " + archivedFile.getName()));
            System.out.println("🗄 Log fayli arxivlandi: " + archivedFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
           // MultiplayerFriend.telegramBot.execute(new SendMessage(chatId, "❌ Log faylni arxivlashda xatolik!"));
            System.out.println("❌ Log faylni arxivlashda xatolik!");
        }
    }
}
