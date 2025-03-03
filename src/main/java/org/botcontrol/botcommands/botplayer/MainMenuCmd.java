package org.botcontrol.botcommands.botplayer;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.botcontrol.botcommands.BotCommand;
import org.botcontrol.botservice.btnservice.BotButtonService;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.msgservice.Constant;
import org.botcontrol.botservice.msgservice.ResourceMessageManager;
import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;

import static org.botcontrol.Main.telegramBot;

@RequiredArgsConstructor
public class MainMenuCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(MainMenuCmd.class);
    private final User user;

    @Override
    public void process() {
        logger.info("Kabinet menyusi ko'rsatilmoqda (User: {})", user.getUserId());
        try {
            user.setMessageId(telegramBot.execute(
                    new SendMessage(
                            user.getUserId(),
                            ResourceMessageManager.getString(Constant.START_MSG)
                    ).replyMarkup(BotButtonService.genCabinetButtons())
                            .parseMode(ParseMode.HTML)
            ).message().messageId());

            DB.updateMessageId(user.getUserId(), user.getMessageId());

            user.setUserState(UserState.CABINET);

            logger.debug("Kabinet menyusi yuborildi");
        } catch (Exception e) {
            logger.error("Kabinet menyusini yuborishda xatolik: {}", e.getMessage(), e);
        }
    }
}
