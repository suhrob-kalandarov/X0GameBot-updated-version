package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.database.DB;
import org.exp.entity.tguserentities.State;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.servicemessages.Constant.USER_STATISTICS_MSG;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class StatisticsCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(StatisticsCmd.class);
    private final TgUser tgUser;

    @Override
    public void process() {
        logger.info("Statistika ko'rsatilmoqda (User: {})", tgUser.getChatId());
        try {
            String message = String.format(
                    getString(USER_STATISTICS_MSG),
                    tgUser.getUserScore(),
                    tgUser.getDrawScore(),
                    tgUser.getBotScore()
            );
            EditMessageText editMessageText = new EditMessageText(
                    tgUser.getChatId(),
                    tgUser.getMessageId(),
                    message + "\n\n@HowdyBots"
            );
            editMessageText.parseMode(ParseMode.valueOf("HTML"));
            editMessageText.replyMarkup(BotButtonService.genAfterGameCabinetButtons());
            SendResponse sendResponse = (SendResponse) telegramBot.execute(editMessageText);
            tgUser.setMessageId(sendResponse.message().messageId());
            DB.updateMessageId(tgUser.getChatId(), tgUser.getMessageId());
            tgUser.setState(State.CABINET);
            logger.debug("Statistika muvaffaqiyatli yuborildi");
        } catch (Exception e) {
            logger.error("Statistikani ko'rsatishda xatolik: {}", e.getMessage(), e);
        }
    }
}