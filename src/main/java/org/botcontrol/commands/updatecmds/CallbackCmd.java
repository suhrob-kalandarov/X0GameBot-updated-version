package org.botcontrol.commands.updatecmds;

import com.pengrad.telegrambot.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.*;
import org.botcontrol.commands.playercmds.BotCommand;
import org.botcontrol.commands.playercmds.*;
import org.botcontrol.entities.User;

import static java.util.Objects.requireNonNull;
import static org.botcontrol.botservice.msgservice.Constant.*;

@RequiredArgsConstructor
public class CallbackCmd implements UpdateCommand {
    private final Update update;
    private final User user;

    private BotCommand command;
    private final CallbackQuery callbackQuery = update.callbackQuery();
    private static final Logger logger = LogManager.getLogger(CallbackCmd.class);

    @Override
    public void handle() {

        logger.debug("CallbackQuery data: {}", callbackQuery.data());

        String data = callbackQuery.data();

        if (data.startsWith(CELL)) {
            command = new InGame(user, data);

        } else if (data.startsWith(LANG)) {
            command = new ChangeLanguageCmd(user, data);

        } else if (data.equals(PLAY_WITH_BOT_BTN)) {
            command = new SelectionSymbolCmd(user);

        } else if (data.equals(PLAY_WITH_FRIEND_BTN)) {
            //command = new InviteFriendCmd(user);


        } else if (data.startsWith(CHOSEN_SYMBOL)) {
            command = new PlayGameCmd(user, data);

        } else if (data.equals(LANGUAGE_MSG)) {
            command = new LanguageMainMenuCmd(user);

        } else if (data.equals(STATISTICS_BTN)) {
            command = new StatisticsCmd(user);

        } else if (data.startsWith(BACK)) {
            command = new BackButtonCmd(user, data);

        } else if (data.equals(SUPPORT_BTN)) {
            command = new AssistanceCmd(user);

        } else if (data.equals(DIFFICULTY_LEVEL_BTN)) {
            command = new DifficultyLevelMainMenuCmd(user);

        } else if (data.startsWith(LEVEL)) {
            command = new ChangeDifficultyLevelCmd(user,data);
        }
        requireNonNull(command).process();
    }
}
