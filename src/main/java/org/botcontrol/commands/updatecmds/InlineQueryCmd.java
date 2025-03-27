package org.botcontrol.commands.updatecmds;

import com.pengrad.telegrambot.model.InlineQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import lombok.RequiredArgsConstructor;
import org.botcontrol.Main;
import org.botcontrol.entities.User;

import static org.botcontrol.botservice.gameservice.MultiplayerLogic.createNewGame;

@RequiredArgsConstructor
public class InlineQueryCmd implements UpdateCommand {
    private final InlineQuery inlineQuery;
    //private final User user;

    @Override
    public void handle() {
        try {

            Long creatorId = inlineQuery.from().id();

            int gameId = createNewGame(creatorId);


            // Inline natijada bitta yangi o'yin yaratiladi:
            InlineKeyboardMarkup joinMarkup = new InlineKeyboardMarkup(
                    new InlineKeyboardButton("Join as ❌").callbackData("SELECT_X_" + gameId)
                    //, new InlineKeyboardButton("Join as ⭕").callbackData("SELECT_O_" + gameId)
            );

            // Inline natija: oddiy maqola ko‘rinishida tugmalar bilan
            InlineQueryResult[] results = new InlineQueryResult[]{
                    new InlineQueryResultArticle("join_game", "Start Game", "Please, join game!")
                            .inputMessageContent(new InputTextMessageContent("Opponent is waiting..."))
                            .replyMarkup(joinMarkup)
            };

            Main.telegramBot.execute(new AnswerInlineQuery(inlineQuery.id(), results));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
