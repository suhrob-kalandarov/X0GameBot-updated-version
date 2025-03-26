package org.botcontrol.commands.playercmds;

import lombok.RequiredArgsConstructor;
import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.request.EditMessageText;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.Main;
import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.msgservice.Constant;
import org.botcontrol.botservice.btnservice.BotButtonService;

import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class DifficultyLevelMainMenuCmd implements BotCommand {
    private final User user;

    private static final Logger logger = LogManager.getLogger(LanguageMainMenuCmd.class);

    @Override
    public void process() {
        SendResponse response = (SendResponse) Main.telegramBot.execute(
                new EditMessageText(
                        user.getUserId(), user.getMessageId(),
                        getString(Constant.CHOOSE_DIFFICULTY_LEVEL)
                                .formatted(
                                        getString("level_" + user.getDifficultyLevel())
                                )
                ).replyMarkup(
                        BotButtonService.genDifficultyLevelButtons()
                )
        );

        user.setMessageId(response.message().messageId());
        //user.setUserState(UserState.LEVEL_DIFFICULTY_CABINET);

        DB.updateMessageId(user.getUserId(), user.getMessageId());
       //DB.updateUserState(user.getUserId(), user.getUserState().toString());
    }
}