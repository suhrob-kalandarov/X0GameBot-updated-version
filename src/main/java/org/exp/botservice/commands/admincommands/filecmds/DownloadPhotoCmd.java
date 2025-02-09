package org.exp.botservice.commands.admincommands.filecmds;

import com.pengrad.telegrambot.model.File;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.tguserentities.TgUser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@RequiredArgsConstructor
public class DownloadPhotoCmd implements BotCommand {
    private final TgUser tgUser;
    private final Update update;

    @Override
    public void process() {
        getPhoto(update);
    }

    @SneakyThrows
    static void getPhoto(Update update) {
        // 1. Rasm mavjudligini tekshirish
        if (update.message().photo() == null || update.message().photo().length == 0) {
            throw new IllegalArgumentException("Xabarda rasm mavjud emas");
        }

        // 2. Eng yuqori sifatli rasmni tanlash (oxirgi element)
        PhotoSize[] photos = update.message().photo();
        PhotoSize largestPhoto = photos[photos.length - 1];

        // 3. File obyektini olish
        GetFile getFileRequest = new GetFile(largestPhoto.fileId());
        GetFileResponse getFileResponse = Main.telegramBot.execute(getFileRequest);

        if (getFileResponse == null || !getFileResponse.isOk()) {
            throw new RuntimeException("File ma'lumotlarini olishda xatolik");
        }

        File file = getFileResponse.file();

        // 4. Rasm kontentini yuklab olish
        byte[] fileContent = Main.telegramBot.getFileContent(file);

        // 5. Papka mavjudligini tekshirish va yaratish
        Path dirPath = Path.of("src/main/java/org/files");
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 6. Fayl nomini yaratish va saqlash
        String fileName = UUID.randomUUID() + ".jpg";
        Path filePath = dirPath.resolve(fileName);

        Files.write(filePath, fileContent);

        // 7. Foydalanuvchiga xabar yuborish
        String message = "✅ Rasm muvaffaqiyatli saqlandi!\n" +
                "📂 Fayl nomi: " + fileName + "\n" +
                "📁 Saqlangan joy: " + filePath.toAbsolutePath();

        Main.telegramBot.execute(new SendMessage(update.message().chat().id(), message));
    }
}
