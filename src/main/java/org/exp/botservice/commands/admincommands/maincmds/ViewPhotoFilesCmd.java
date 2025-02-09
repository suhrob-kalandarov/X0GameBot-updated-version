package org.exp.botservice.commands.admincommands.maincmds;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.adminentities.AdminState;

import static org.exp.database.DB.dirPath;
import static org.exp.database.DB.getFileList;

@RequiredArgsConstructor
public class ViewPhotoFilesCmd implements BotCommand {
    private final Admin admin;

    @Override
    public void process() {
        List<String> photoFiles = getFileList();
        SendResponse response = (SendResponse) Main.telegramBot.execute(
                new EditMessageText(
                        admin.getChatId(),
                        admin.getMessageId(),
                        listFiles(photoFiles)
                ).replyMarkup(
                        BotButtonService.generateImgNameBtns(photoFiles)
                )
        );
        admin.setMessageId(response.message().messageId());
        admin.setAdminState(AdminState.VIEWING_FILE_LIST);
    }

    @SneakyThrows
    public static String listFiles(List<String> photoFiles) {
        if (photoFiles.isEmpty()) return "📂 Papka bo'sh.";

        String fileList = photoFiles.stream()
                .map(ViewPhotoFilesCmd::formatFileAttributes) // Yordamchi metodni chaqirish
                .collect(Collectors.joining("\n\n"));
        return "📂 Papkadagi rasmlar:\n\n" + fileList;
    }

    @SneakyThrows
    private static String formatFileAttributes(String fileName) {
        Path filePath = dirPath.resolve(fileName); // org.exp.database.DB.dirPath;
        try {
            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            long fileSizeKB = attr.size() / 1024; // Fayl hajmi KB da
            String lastModified = attr.lastModifiedTime().toString().substring(0, 19); // YYYY-MM-DD T HH:MM:SS

            // Sana va vaqtni ajratish
            String date = lastModified.substring(0, 10);  // YYYY-MM-DD
            String time = lastModified.substring(11, 19); // HH:MM:SS

            return "🖼 " + fileName + "\n💿S: " + fileSizeKB + " KB, 🗓MD: " + date + ", ⏳T: " + time;
        } catch (IOException e) {
            return "🖼 " + fileName + "\n(Ma'lumotlarni olishda xatolik)";
        }
    }
}
