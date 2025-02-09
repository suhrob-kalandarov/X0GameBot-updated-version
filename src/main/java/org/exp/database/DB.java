package org.exp.database;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.exp.entity.adminentities.Admin;
import org.exp.entity.tguserentities.TgUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface DB {
    //EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("default");
    Path dirPath = Paths.get("src/main/java/org/files");
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

    static List<String> getFileList() {
        // 1. Papka mavjudligini tekshirish
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            return List.of(); // Bo'sh ro‘yxat qaytarish
        }

        // 2. Fayllarni ro‘yxatga olish
        try (Stream<Path> filesStream = Files.list(dirPath)) {
            return filesStream
                    .filter(Files::isRegularFile) // Faqat oddiy fayllarni olish
                    .map(path -> path.getFileName().toString()) // Fayl nomlarini olish
                    .collect(Collectors.toList()); // List<String> shaklida yig‘ish
        } catch (IOException e) {
            return List.of(); // Xatolik bo‘lsa ham bo‘sh ro‘yxat qaytarish
        }
    }
}

