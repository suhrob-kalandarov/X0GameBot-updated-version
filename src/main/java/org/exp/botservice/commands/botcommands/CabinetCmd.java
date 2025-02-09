package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.botservice.servicemessages.Constant;
import org.exp.botservice.servicemessages.ResourceMessageManager;
import org.exp.entity.tguserentities.State;
import org.exp.entity.tguserentities.TgUser;

import static org.exp.Main.telegramBot;
import static org.exp.database.DB.isAdmin;

@RequiredArgsConstructor
public class CabinetCmd implements BotCommand {
    private final TgUser tgUser;

    @Override
    public void process() {
        if (isAdmin(tgUser)) {
            tgUser.setMessageId(telegramBot.execute(
                    new SendMessage(
                            tgUser.getChatId(),
                            "You're admin! I added admin button!\n\n"
                                    + ResourceMessageManager.getString(Constant.START_MSG)
                    ).replyMarkup(BotButtonService.genAdminMenuBtn())
            ).message().messageId());

        } else {
            tgUser.setMessageId(telegramBot.execute(
                    new SendMessage(
                            tgUser.getChatId(),
                            ResourceMessageManager.getString(Constant.START_MSG)
                    ).replyMarkup(BotButtonService.genCabinetButtons())
            ).message().messageId());

            tgUser.setState(State.CABINET);
        }
    }
}
