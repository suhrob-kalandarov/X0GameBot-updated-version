package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.servicemessages.Constant.WARNING_MSG;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class WarningHandlerCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(WarningHandlerCmd.class);
    private final TgUser tgUser;

    @Override
    public void process() {
        logger.warn("Ogohlantirish xabari yuborilmoqda (User: {})", tgUser.getChatId());
        try {
            telegramBot.execute(new DeleteMessage(
                    tgUser.getChatId(),
                    tgUser.getMessageId())
            );

            tgUser.setMessageId(telegramBot.execute(
                    new SendMessage(
                            tgUser.getChatId(),
                            getString(WARNING_MSG)
                    )
            ).message().messageId());
            logger.debug("Ogohlantirish xabari yuborildi");
        } catch (Exception e) {
            logger.error("Ogohlantirish xabarini yuborishda xatolik: {}", e.getMessage(), e);
        }
    }
}