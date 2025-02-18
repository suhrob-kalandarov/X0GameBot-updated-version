package org.exp.botservice.commands.admincommands.filecmds;

import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.commands.admincommands.AdminCmd;
import org.exp.entity.adminentities.Admin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.exp.Main.telegramBot;

@RequiredArgsConstructor
public class DownloadPhotoCmd implements BotCommand {
    private final Admin admin;
    private final Update update;

    @Override
    public void process() {
        // 1. Faylni yuklab olish
        PhotoSize[] photos = update.message().photo();
        GetFileResponse getFileResponse = Main.telegramBot.execute(
                new GetFile(photos[photos.length - 1].fileId())
        );

        if (getFileResponse == null || !getFileResponse.isOk()) {
            Main.telegramBot.execute(
                    new SendMessage(admin.getChatId(), "File ma'lumotlarini olishda xatolik!")
            );
            telegramBot.execute(new DeleteMessage(
                    admin.getChatId(),
                    admin.getMessageId())
            );
            new AdminCmd(admin).process();

        } else {
            getPhoto(admin, getFileResponse);
        }
    }

    @SneakyThrows
    static void getPhoto(Admin admin, GetFileResponse getFileResponse) {
        // 2. Papka mavjudligini tekshirish va yaratish
        Path dirPath = Path.of("src/main/java/org/files");
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 3. Fayl nomini yaratish va saqlash
        String fileName = UUID.randomUUID() + ".jpg";
        Path filePath = dirPath.resolve(fileName);

        Files.write(
                filePath,
                Main.telegramBot.getFileContent(
                        getFileResponse.file()
                )
        );

        // 4. Foydalanuvchiga yuklanganligi haqida xabar yuborish
        String message = "✅ Rasm muvaffaqiyatli saqlandi!\n" +
                "📂 Fayl nomi: " + fileName + "\n" +
                "📁 Saqlangan joy: " + filePath.toAbsolutePath();

        Main.telegramBot.execute(
                new SendMessage(
                        admin.getChatId(),
                        message
                )
        );
    }
}
