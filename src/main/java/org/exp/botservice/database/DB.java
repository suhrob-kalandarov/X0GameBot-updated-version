package org.exp.botservice.database;

import org.exp.entity.Admin;
import org.exp.entity.TgUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface DB {
    List<TgUser> TG_USERS_LIST = new ArrayList<>();
    List<Admin> ADMIN_LIST = new ArrayList<>(List.of(Admin.builder().chatId(6513286717L).build()));

    static TgUser getOrCreateUser(Long chatId) {
        Optional<TgUser> optionalTgUser = TG_USERS_LIST.stream()
                .filter(item ->
                        item.getChatId().equals(chatId))
                .findFirst();
        if (optionalTgUser.isPresent()) {
            return optionalTgUser.get();
        }
        else {
            TgUser tgUser = TgUser
                    .builder()
                    .chatId(chatId)
                    .build();
            TG_USERS_LIST.add(tgUser);
            return tgUser;
        }
    }

    static boolean isAdmin(TgUser tgUser) {
        return ADMIN_LIST.stream()
                .anyMatch(
                        admin -> admin.getChatId().equals(tgUser.getChatId())
                );
    }

    static Admin getAdmin(Long chatId) {
        return ADMIN_LIST.stream()
                .filter(admin -> admin.getChatId().equals(chatId))
                .findFirst().get();
    }
}
