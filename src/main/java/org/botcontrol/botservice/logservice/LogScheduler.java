package org.botcontrol.botservice.logservice;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class LogScheduler {
    public static void logSender(String chatId, String token) {
        TelegramLogSender logSender = new TelegramLogSender(token, chatId);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // TEST UCHUN: Bugun 00:00 da log yuborish (faqat bir marta)
        //long testDelay = getInitialDelayForHour(23);
       // scheduler.schedule(logSender::sendDailyLog, testDelay, TimeUnit.MINUTES);

        // HAQIQIY REJALASHTIRISH: Har kuni 09:00 da log yuborish
        long initialDelay = getInitialDelayForHour();
        scheduler.scheduleAtFixedRate(logSender::sendDailyLog, initialDelay, 24, TimeUnit.HOURS);
    }

    // Berilgan soatga qancha vaqt qolgani hisoblash
    private static long getInitialDelayForHour() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetTime = now.toLocalDate().atTime(20, 0);

        if (now.isAfter(targetTime)) {
            // Agar vaqt allaqachon o‘tgan bo‘lsa, ertangi kun uchun hisoblaymiz
            targetTime = targetTime.plusDays(1);
        }

        return Duration.between(now, targetTime).toMinutes();
    }
}
