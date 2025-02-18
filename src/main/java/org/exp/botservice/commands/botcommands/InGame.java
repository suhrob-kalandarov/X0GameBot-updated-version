package org.exp.botservice.commands.botcommands;

import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.exp.botservice.commands.BotCommand;
import org.exp.entity.tguserentities.TgUser;

@RequiredArgsConstructor
public class InGame implements BotCommand {
    private static final Logger logger = LogManager.getLogger(InGame.class);
    private final TgUser tgUser;
    private final Update update;
    private final String data;

    @Override
    public void process() {
        logger.debug("O'yin harakati qayta ishlanmoqda (Data: {})", data);
        try {
            int row = Integer.parseInt(data.split("_")[1]);
            int col = Integer.parseInt(data.split("_")[2]);
            logger.trace("Qabul qilingan katak: [{}, {}]", row, col);
            new PlayGameCmd(tgUser, update, data).handleMove(row, col);
        } catch (Exception e) {
            logger.error("Harakatni qayta ishlashda xatolik: {}", e.getMessage(), e);
        }
    }
}