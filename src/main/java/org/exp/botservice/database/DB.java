package org.exp.botservice.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;

import org.exp.entity.TgUser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface DB {

    List<TgUser> TG_USERS_LIST = new ArrayList<>();

    static TgUser getOrCreateUser(Long chatId) {
        Optional<TgUser> optionalTgUser = TG_USERS_LIST.stream()
                .filter(item ->
                        item.getChatId().equals(chatId))
                .findFirst();
        if (optionalTgUser.isPresent()) {
            saveTgUser();
            return optionalTgUser.get();
        }
        else {
            TgUser tgUser = TgUser
                    .builder()
                    .chatId(chatId)
                    .build();
            TG_USERS_LIST.add(tgUser);
            saveTgUser();
            return tgUser;
        }
    }


    @SneakyThrows
    static void saveTgUser() {
        Path path = Path.of("src/main/java/org/exp/botservice/database/TgUser.json");
        String prettyJson = new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(TG_USERS_LIST);
        Files.writeString(
                path,
                prettyJson,
                StandardOpenOption.TRUNCATE_EXISTING
        );
        System.out.println("TgUser file updated!");
    }

    @SneakyThrows
    static void restoringTgUserList(){
        Path path = Path.of("src/main/java/org/exp/botservice/database/TgUser.json");
        List<TgUser> tgUserList = new Gson().fromJson(
                Files.readString(path),
                new TypeToken<List<TgUser>>(){}.getType()
        );
        if (tgUserList != null) TG_USERS_LIST.addAll(tgUserList);
    }
}
