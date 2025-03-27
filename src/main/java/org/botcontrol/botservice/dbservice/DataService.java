package org.botcontrol.botservice.dbservice;

import org.botcontrol.entities.MultiGame;
import org.botcontrol.entities.MultiplayerUser;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public interface DataService {
    List<MultiplayerUser> users = new ArrayList<>();
    Map<Integer, MultiGame> games = new HashMap<>();

    static void log(String message) {
        System.out.println("[LOG] " + new Date() + " - " + message);
    }

    static void addUser(Long userId, String firstName, String lastName) {
        MultiplayerUser user = MultiplayerUser.builder()
                .userId(userId)
                .fullName(setFullName(firstName, lastName))
                .build();
        users.add(user);
        log("Yangi foydalanuvchi qo'shildi: " + user);
    }

    static String setFullName(String firstName, String lastName) {
        AtomicReference<StringBuilder> fullNameBuilder = new AtomicReference<>(new StringBuilder());

        // Ism va familiyani qo'shish
        if (firstName != null) {
            fullNameBuilder.get().append(firstName);
        }
        if (lastName != null) {
            if (!fullNameBuilder.get().isEmpty()) {
                fullNameBuilder.get().append(" ");
            }
            fullNameBuilder.get().append(lastName);
        }
        return fullNameBuilder.toString();
    }
}
