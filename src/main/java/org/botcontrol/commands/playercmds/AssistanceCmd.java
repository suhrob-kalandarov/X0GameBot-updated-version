package org.botcontrol.commands.playercmds;

import lombok.RequiredArgsConstructor;
import com.pengrad.telegrambot.response.SendResponse;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.Main;
import org.botcontrol.entities.User;
import org.botcontrol.entities.UserState;
import org.botcontrol.botservice.dbservice.DB;
import org.botcontrol.botservice.btnservice.BotButtonService;

import static org.botcontrol.botservice.msgservice.Constant.START_MSG;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class AssistanceCmd implements BotCommand {
    private final User user;

    private static final Logger logger = LogManager.getLogger(AssistanceCmd.class);

    @Override
    public void process() {
        logger.info("Yordam so'rovi qabul qilindi (User: {})", user.getUserId());
        try {
            SendResponse response = (SendResponse) Main.telegramBot.execute(
                    new EditMessageText(user.getUserId(), user.getMessageId(), getString(START_MSG))
                            .replyMarkup(BotButtonService.genCabinetButtons())
                            .parseMode(ParseMode.HTML)
            );

            user.setMessageId(response.message().messageId());
            //user.setUserState(UserState.INFO_GAME);

            DB.updateMessageId(user.getUserId(), user.getMessageId());
            //DB.updateUserState(user.getMessageId(), user.getUserState().toString());

            logger.debug("Yordam menyusi yuborildi");

        } catch (Exception e) {
            logger.error("Yordam menyusini ko'rsatishda xatolik: {}", e.getMessage(), e);
        }
    }
}
