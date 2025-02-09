package org.exp.botservice.commands.botcommands;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.servicemessages.Constant;
import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.entity.tguserentities.State;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.botservice.service.BotButtonService.chooseSymbolButtons;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SelectionSymbolCmd implements BotCommand {
    private final TgUser tgUser;
    private final Update update;

    @Override
    public void process() {
        EditMessageText editMessageText = new EditMessageText(
                tgUser.getChatId(),
                tgUser.getMessageId(),
                ResourceMessageManager.getString(Constant.CHOOSE_SYMBOL_MSG)
        );
        editMessageText.replyMarkup(chooseSymbolButtons());
        tgUser.setState(State.SYMBOL_CHOOSING);
        SendResponse response = (SendResponse) telegramBot.execute(editMessageText);
        tgUser.setMessageId(response.message().messageId());
    }
}