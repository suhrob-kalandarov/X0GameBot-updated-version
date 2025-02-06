package org.exp.botservice.commands;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.State;
import org.exp.entity.TgUser;

import java.util.Locale;

import static org.exp.botservice.servicemessages.Constant.LANG_SUCCESS_MSG;
import static org.exp.botservice.servicemessages.Constant.START_MSG;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;
import static org.exp.botservice.servicemessages.ResourceMessageManager.setLocale;

@RequiredArgsConstructor
public class LanguageChangerCmd implements BotCommand {
    private final TgUser tgUser;
    private final String data;

    @Override
    public void process() {
        String languageCode = data.split("_")[1]; // Masalan, "lang_en" dan "en" ni olish
        setLocale(new Locale(languageCode)); //Ma'lumot olib beruvchiga tilni saqlash
        tgUser.setLanguage(languageCode); //User tilini saqlash

        EditMessageText editMessageText = new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                getString(LANG_SUCCESS_MSG) + getString(START_MSG)
        );
        editMessageText.replyMarkup(BotButtonService.genCabinetButtons(tgUser));
        SendResponse sendResponse = (SendResponse) Main.telegramBot.execute(editMessageText);
        tgUser.setMessageId(sendResponse.message().messageId());
        tgUser.setState(State.CABINET);
    }
}
