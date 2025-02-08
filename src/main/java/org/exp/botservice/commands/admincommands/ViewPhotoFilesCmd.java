package org.exp.botservice.commands.admincommands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class ViewPhotoFilesCmd implements BotCommand {
    private final TgUser tgUser;
    private final Update update;

    @Override
    public void process() {
        listFiles(update, tgUser);
    }

    @SneakyThrows
    public static void listFiles(Update update, TgUser tgUser) {
        // 1. Papka yo'lini aniqlash
        Path dirPath = Paths.get("src/main/java/org/files");

        // 2. Papka mavjudligini tekshirish
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            String errorMessage = "❌ Papka topilmadi yoki papka mavjud emas: " + dirPath.toAbsolutePath();
            Main.telegramBot.execute(new SendMessage(update.message().chat().id(), errorMessage));
            return;
        }

        // 3. Papkadagi fayllarni ro'yxatini olish
        try (Stream<Path> filesStream = Files.list(dirPath)) {
            String fileList = filesStream
                    .filter(Files::isRegularFile) // Faqat fayllarni olish
                    .map(Path::getFileName)       // Fayl nomlarini olish
                    .map(Path::toString)          // Path ni String ga o'tkazish
                    .collect(Collectors.joining("\n")); // Har bir fayl nomini yangi qatorga yozish

            // 4. Foydalanuvchiga xabar yuborish
            if (fileList.isEmpty()) {
                String message = "📂 Papka bo'sh: " + dirPath.toAbsolutePath();
                Main.telegramBot.execute(new SendMessage(update.message().chat().id(), message));
            } else {
                String message = "📂 Papkadagi fayllar:\n" + fileList;
                Main.telegramBot.execute(new SendMessage(tgUser.getChatId(), message));
            }
        }
    }
}
