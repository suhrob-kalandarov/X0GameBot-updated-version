package org.botcontrol.botcommands.botplayer;

import lombok.RequiredArgsConstructor;
import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.model.request.ParseMode;

import org.botcontrol.entities.User;
import org.apache.logging.log4j.Logger;
import org.botcontrol.entities.UserState;
import org.apache.logging.log4j.LogManager;
import org.botcontrol.botcommands.BotCommand;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.btnservice.BotButtonService;

import static org.botcontrol.Main.telegramBot;
import static org.botcontrol.entities.DifficultyLevel.*;
import static org.botcontrol.botservice.msgservice.Constant.USER_STATISTICS_MSG;
import static org.botcontrol.botservice.msgservice.Constant.DIFFICULTY_LEVEL_MSG;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class ChangeDifficultyLevelCmd implements BotCommand {
    private final User user;
    private final String data;

    private static final Logger logger = LogManager.getLogger(ChangeDifficultyLevelCmd.class);

    @Override
    public void process() {
        switch (data) {
            case "level_easy" -> user.setDifficultyLevel(LEVEL_EASY);
            case "level_average" -> user.setDifficultyLevel(LEVEL_MEDIUM);
            case "level_difficult" -> user.setDifficultyLevel(LEVEL_HARD);
            case "level_extreme" -> user.setDifficultyLevel(LEVEL_EXTREME);
        }

        String message = String.format(
                getString(USER_STATISTICS_MSG)
                        .formatted(
                                DB.getUserScores(user.getUserId())
                        )
        );

        SendResponse response = (SendResponse) telegramBot.execute(
                new EditMessageText(
                        user.getUserId(), user.getMessageId(),
                        getString(DIFFICULTY_LEVEL_MSG)
                                .formatted(
                                        getString("level_" + user.getDifficultyLevel())
                                ) + "\n\n" + message + "\n\n@HowdyBots"
                ).parseMode(
                        ParseMode.valueOf("HTML")

                ).replyMarkup(
                        BotButtonService.genAfterGameCabinetButtons()
                )
        );
        user.setMessageId(response.message().messageId());
        user.setUserState(UserState.CHANGE_DIFFICULTY_LEVEL);

        DB.updateDifficultyLevel(user.getUserId(), user.getDifficultyLevel());
        DB.updateMessageId(user.getUserId(), user.getMessageId());
        DB.updateUserState(user.getUserId(), user.getUserState().toString());

        logger.debug("Kabinet menyusi yuborildi");
    }
}