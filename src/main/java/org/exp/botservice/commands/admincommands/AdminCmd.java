package org.exp.botservice.commands.admincommands;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.exp.Main;
import org.exp.botservice.commands.BotCommand;
import org.exp.botservice.service.BotButtonService;
import org.exp.entity.State;
import org.exp.entity.TgUser;

@RequiredArgsConstructor
public class AdminCmd implements BotCommand {
    private final TgUser tgUser;

    @Override
    public void process() {
        EditMessageText editMessageText = new EditMessageText(
                tgUser.getChatId(), tgUser.getMessageId(),
                "ADMIN PANEL\n#ChatId " + tgUser.getChatId()
                        + "\nusername: @" + tgUser.getUsername()
        );
        editMessageText.replyMarkup(BotButtonService.genAdminBtns());
        SendResponse response = (SendResponse) Main.telegramBot.execute(editMessageText);
        //tgUser.setMessageId(response.message().messageId());
        tgUser.setState(State.ADMIN_CABINET);
    }
}