package org.botcontrol.commands.updatecmds;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.*;
import org.botcontrol.commands.playercmds.*;
import org.botcontrol.botservice.msgservice.ResourceMessageManager;
import org.botcontrol.entities.User;

import java.util.Locale;

import static org.botcontrol.Main.*;
import static org.botcontrol.botservice.dbservice.DB.addUser;
import static org.botcontrol.botservice.dbservice.DB.setFullName;
import static org.botcontrol.botservice.msgservice.Constant.*;
import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

@RequiredArgsConstructor
public class MessageCmd implements UpdateCommand {
    private final Update update;
    private final User user;

    private static final Logger logger = LogManager.getLogger(MessageCmd.class);
    //private final Message message = update.message();
    private BotCommand command;

    @Override
    public void handle() {
        Message message = update.message();

        logger.debug("Xabar user: {}", user);
        String text = message.text();

        if (text != null){
            logger.info("Foydalanuvchi xabari: {}", text);

            if (text.equals(START)) {
                logger.info("/start komandasi qabul qilindi");

                if (user.getMessageId() != null) {
                    new WarningHandlerCmd(user).process();
                }

                Long userId = message.from().id();
                String firstName = message.from().firstName();
                String lastName = message.from().lastName();

                User.builder()
                        .userId(userId)
                        .fullName(setFullName(firstName, lastName))
                        .build();
                addUser(userId, firstName, lastName);

                if (ResourceMessageManager.bundle == null) {
                    logger.error("ResourceBundle yuklanmagan! Qayta yuklashga harakat qilamiz.");
                    ResourceMessageManager.loadBundle(
                            "messages",
                            new Locale(user.getLanguage())
                    );
                }

            } else {
                new WarningHandlerCmd(user).process();
            }

        } else {
            telegramBot.execute(new DeleteMessage(
                    user.getUserId(),
                    user.getMessageId())
            );
            int newMessageId = telegramBot.execute(
                    new SendMessage(chatId, getString(WARNING_MSG))
            ).message().messageId();
            user.setMessageId(newMessageId);
        }
        new MainMenuCmd(user).process();
    }
}
