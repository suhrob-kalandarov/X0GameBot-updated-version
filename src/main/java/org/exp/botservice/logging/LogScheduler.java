package org.exp.botservice.logging;

import java.time.*;
import java.util.concurrent.*;

public class LogScheduler {
    public static void logSender(String chatId, String token) {
        TelegramLogSender logSender = new TelegramLogSender(token, chatId);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // TEST UCHUN: Bugun 00:00 da log yuborish (faqat bir marta)
        long testDelay = getInitialDelayForHour(0);
        scheduler.schedule(logSender::sendDailyLog, testDelay, TimeUnit.MINUTES);

        // HAQIQIY REJALASHTIRISH: Har kuni 09:00 da log yuborish
        long initialDelay = getInitialDelayForHour(9);
        scheduler.scheduleAtFixedRate(logSender::sendDailyLog, initialDelay, 24, TimeUnit.HOURS);
    }

    // Berilgan soatga qancha vaqt qolgani hisoblash
    private static long getInitialDelayForHour(int targetHour) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.toLocalDate().atTime(targetHour, 0);

        if (now.isAfter(targetTime)) {
            // Agar vaqt allaqachon o‘tgan bo‘lsa, ertangi kun uchun hisoblaymiz
            targetTime = targetTime.plusDays(1);
        }

        return Duration.between(now, targetTime).toMinutes();
    }
}
