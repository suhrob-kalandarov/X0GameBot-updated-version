package org.botcontrol.botcommands.botplayer;

import lombok.RequiredArgsConstructor;
import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.Main;
import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;
import org.botcontrol.botcommands.BotCommand;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.btnservice.BotButtonService;

import java.util.Locale;

import static org.botcontrol.botservice.msgservice.Constant.*;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.setLocale;

@RequiredArgsConstructor
public class ChangeLanguageCmd implements BotCommand {
    private final User user;
    private final String data;

    private static final Logger logger = LogManager.getLogger(ChangeLanguageCmd.class);

    @Override
    public void process() {
        try {
            String languageCode = data.split("_")[1];
            logger.info("Til o'zgartirilmoqda: {}", languageCode);
            setLocale(new Locale(languageCode));

            SendResponse sendResponse = (SendResponse) Main.telegramBot.execute(
                    new EditMessageText(
                            user.getUserId(),
                            user.getMessageId(),
                            getString(START_MSG)
                    ).parseMode(
                            ParseMode.valueOf("HTML")

                    ).replyMarkup(
                            BotButtonService.genCabinetButtons()
                    )
            );

            user.setMessageId(sendResponse.message().messageId());
            user.setUserState(UserState.CABINET);
            user.setLanguage(languageCode);

            DB.updateLanguage(user.getUserId(), languageCode);
            DB.updateMessageId(user.getUserId(), user.getMessageId());
            DB.updateUserState(user.getMessageId(), user.getUserState().toString());

            logger.debug("Til muvaffaqiyatli o'zgartirildi");

        } catch (Exception e) {
            logger.error("Tilni o'zgartirishda xatolik: {}", e.getMessage(), e);
        }
    }
}
