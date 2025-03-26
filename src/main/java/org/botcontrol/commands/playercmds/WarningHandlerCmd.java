package org.botcontrol.commands.playercmds;

import com.pengrad.telegrambot.request.EditMessageText;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.botcontrol.entities.User;

import static org.botcontrol.Main.telegramBot;

@RequiredArgsConstructor
public class WarningHandlerCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(WarningHandlerCmd.class);
    private final User user;

    @Override
    public void process() {
        logger.warn("Ogohlantirish xabari yuborilmoqda (User: {})", user.getUserId());
        try {
            telegramBot.execute(
                    new EditMessageText(user.getUserId(), user.getMessageId(), "↩")
            );
            logger.debug("Ogohlantirish xabari yuborildi");
        } catch (Exception e) {
            logger.error("Ogohlantirish xabarini yuborishda xatolik: {}", e.getMessage(), e);
        }
    }
}