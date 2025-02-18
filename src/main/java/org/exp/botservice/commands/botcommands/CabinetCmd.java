package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.botservice.servicemessages.Constant;
import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.database.DB;
import org.exp.entity.tguserentities.State;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;

@RequiredArgsConstructor
public class CabinetCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(CabinetCmd.class);
    private final TgUser tgUser;

    @Override
    public void process() {
        logger.info("Kabinet menyusi ko'rsatilmoqda (User: {})", tgUser.getChatId());
        try {
            tgUser.setMessageId(telegramBot.execute(
                    new SendMessage(
                            tgUser.getChatId(),
                            ResourceMessageManager.getString(Constant.START_MSG)
                    ).replyMarkup(BotButtonService.genCabinetButtons())
            ).message().messageId());
            DB.updateMessageId(tgUser.getChatId(), tgUser.getMessageId());
            tgUser.setState(State.CABINET);
            logger.debug("Kabinet menyusi yuborildi");
        } catch (Exception e) {
            logger.error("Kabinet menyusini yuborishda xatolik: {}", e.getMessage(), e);
        }
    }
}