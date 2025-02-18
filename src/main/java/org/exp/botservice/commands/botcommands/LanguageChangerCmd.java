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
import org.exp.entity.tguserentities.State;
import org.exp.entity.tguserentities.TgUser;

import java.util.Locale;

import static org.exp.botservice.servicemessages.Constant.LANG_SUCCESS_MSG;
import static org.exp.botservice.servicemessages.Constant.START_MSG;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;
import static org.exp.botservice.servicemessages.ResourceMessageManager.setLocale;

@RequiredArgsConstructor
public class LanguageChangerCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(LanguageChangerCmd.class);
    private final TgUser tgUser;
    private final String data;

    @Override
    public void process() {
        try {
            String languageCode = data.split("_")[1];
            logger.info("Til o'zgartirilmoqda: {}", languageCode);
            setLocale(new Locale(languageCode));
            tgUser.setLanguage(languageCode);
            DB.updateLanguage(tgUser.getChatId(), languageCode);

            EditMessageText editMessageText = new EditMessageText(
                    tgUser.getChatId(),
                    tgUser.getMessageId(),
                    getString(LANG_SUCCESS_MSG) + getString(START_MSG)
            );
            editMessageText.replyMarkup(BotButtonService.genCabinetButtons());
            SendResponse sendResponse = (SendResponse) Main.telegramBot.execute(editMessageText);
            tgUser.setMessageId(sendResponse.message().messageId());
            DB.updateMessageId(tgUser.getChatId(), tgUser.getMessageId());
            tgUser.setState(State.CABINET);
            logger.debug("Til muvaffaqiyatli o'zgartirildi");
        } catch (Exception e) {
            logger.error("Tilni o'zgartirishda xatolik: {}", e.getMessage(), e);
        }
    }
}