package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.database.DB;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.botservice.servicemessages.Constant.START_MSG;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class AssistanceCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(AssistanceCmd.class);
    private final TgUser tgUser;

    @Override
    public void process() {
        logger.info("Yordim so'rovi qabul qilindi (User: {})", tgUser.getChatId());
        try {
            SendResponse response = (SendResponse) Main.telegramBot.execute(
                    new EditMessageText(
                            tgUser.getChatId(),
                            tgUser.getMessageId(),
                            getString(START_MSG)

                    ).replyMarkup(BotButtonService.genCabinetButtons()));
            tgUser.setMessageId(response.message().messageId());
            DB.updateMessageId(tgUser.getChatId(), tgUser.getMessageId());
            logger.debug("Yordim menyusi yuborildi");
        } catch (Exception e) {
            logger.error("Yordim menyusini ko'rsatishda xatolik: {}", e.getMessage(), e);
        }
    }
}