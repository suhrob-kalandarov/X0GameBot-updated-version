package org.exp.botservice.commands.admincommands.filecmds;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.database.DB;
import org.exp.entity.adminentities.Admin;

import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import org.exp.entity.tguserentities.TgUser;

import java.io.File;

@RequiredArgsConstructor
public class PhotoSenderCmd implements BotCommand {
    private final Admin admin;
    private final Update update;

    @Override
    public void process() {

        admin.setPhotoCaption(update.message().text().substring("caption_".length()));
        sendPhotoToAllUsers(update, admin);
    }

    @SneakyThrows
    public static void sendPhotoToAllUsers(Update update, Admin admin) {
        // 1. Rasm nomini shakllantirish
        String photoPath = "src/main/java/org/files/" + admin.getPhoto();

        // 2. Caption (izoh) olish
        String caption = admin.getPhotoCaption() != null ? admin.getPhotoCaption() : "";

        // 3. Rasm faylini tekshirish
        File photoFile = new File(photoPath);
        if (!photoFile.exists()) {
            String errorMessage = "❌ Rasm fayli topilmadi: " + photoPath;
            Main.telegramBot.execute(new SendMessage(admin.getChatId(), errorMessage));
            return;
        }

        // 4. Barcha foydalanuvchilarga rasmni yuborish
        for (TgUser user : DB.TG_USERS_LIST) {
            long chatId = user.getChatId(); // Foydalanuvchining chatId sini olish

            SendPhoto sendPhoto = new SendPhoto(chatId, photoFile).caption(caption);

            // Rasmni yuborish va natijani tekshirish
            SendResponse response = Main.telegramBot.execute(sendPhoto);
            if (!response.isOk()) {
                System.err.println("⚠️ Xatolik: " + response.description() + " | Foydalanuvchi: " + user.getUsername());
            }
        }

        // 5. Adminga tasdiq xabari yuborish
        String successMessage = "✅ Rasm barcha foydalanuvchilarga yuborildi!";
        Main.telegramBot.execute(new SendMessage(admin.getChatId(), successMessage));
    }
}