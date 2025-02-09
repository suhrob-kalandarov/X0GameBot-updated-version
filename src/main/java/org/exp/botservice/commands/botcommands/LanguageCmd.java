package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.tguserentities.State;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.botservice.servicemessages.Constant.CHOOSE_LANG;
import static org.exp.botservice.servicemessages.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class LanguageCmd implements BotCommand {
    private final TgUser tgUser;

    @Override
    public void process() {
        EditMessageText editMessageText = new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                getString(CHOOSE_LANG)
        );
        editMessageText.replyMarkup(BotButtonService.genLanguageButtons());
        SendResponse sendResponse = (SendResponse) Main.telegramBot.execute(editMessageText);
        tgUser.setMessageId(sendResponse.message().messageId());
        tgUser.setState(State.LANG_MENU);
    }
}
