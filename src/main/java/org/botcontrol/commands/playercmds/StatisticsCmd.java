package org.botcontrol.commands.playercmds;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.botcontrol.botservice.btnservice.BotButtonService;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;

import static org.botcontrol.Main.telegramBot;
import static org.botcontrol.botservice.msgservice.Constant.DIFFICULTY_LEVEL_MSG;
import static org.botcontrol.botservice.msgservice.Constant.USER_STATISTICS_MSG;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class StatisticsCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(StatisticsCmd.class);
    private final User user;

    @Override
    public void process() {
        logger.info("Statistika ko'rsatilmoqda (User: {})", user.getUserId());
        try {
            String message = String.format(
                    getString(USER_STATISTICS_MSG)
                            .formatted(
                                    DB.getUserScores(user.getUserId())
                            )
            );

            EditMessageText editMessageText = new EditMessageText(
                    user.getUserId(), user.getMessageId(),
                    getString(DIFFICULTY_LEVEL_MSG).formatted(getString("level_" + user.getDifficultyLevel()))
                            + "\n\n" + message + "\n\n@HowdyBots"
            );
            editMessageText.parseMode(ParseMode.valueOf("HTML"));
            editMessageText.replyMarkup(BotButtonService.genAfterGameCabinetButtons());
            SendResponse sendResponse = (SendResponse) telegramBot.execute(editMessageText);

            user.setMessageId(sendResponse.message().messageId());
            //user.setUserState(UserState.CABINET);

            DB.updateMessageId(user.getUserId(), user.getMessageId());
            //DB.updateUserState(user.getUserId(), user.getUserState().toString());

            logger.debug("Statistika muvaffaqiyatli yuborildi");
        } catch (Exception e) {
            logger.error("Statistikani ko'rsatishda xatolik: {}", e.getMessage(), e);
        }
    }
}
