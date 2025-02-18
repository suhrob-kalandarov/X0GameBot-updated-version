package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.servicemessages.Constant;
import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.database.DB;
import org.exp.entity.tguserentities.State;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.service.BotButtonService.chooseSymbolButtons;

@RequiredArgsConstructor
public class SelectionSymbolCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(SelectionSymbolCmd.class);
    private final TgUser tgUser;
    private final Update update;

    @Override
    public void process() {
        logger.info("Belgi tanlash menyusi ko'rsatilmoqda (User: {})", tgUser.getChatId());
        try {
            EditMessageText editMessageText = new EditMessageText(
                    tgUser.getChatId(),
                    tgUser.getMessageId(),
                    ResourceMessageManager.getString(Constant.CHOOSE_SYMBOL_MSG)
            );
            editMessageText.replyMarkup(chooseSymbolButtons());
            tgUser.setState(State.SYMBOL_CHOOSING);
            SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
            tgUser.setMessageId(response.message().messageId());
            DB.updateMessageId(tgUser.getChatId(), tgUser.getMessageId());
            logger.debug("Belgi tanlash menyusi yuborildi");
        } catch (Exception e) {
            logger.error("Belgi tanlash menyusini yuborishda xatolik: {}", e.getMessage(), e);
        }
    }
}