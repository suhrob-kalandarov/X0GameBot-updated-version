package org.exp.botservice.commands.botcommands;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.exp.botservice.commands.BotCommand;
import org.exp.entity.tguserentities.TgUser;

@RequiredArgsConstructor
public class BackButtonCmd implements BotCommand {
    private static final Logger logger = LogManager.getLogger(BackButtonCmd.class);
    private final TgUser tgUser;
    private final String data;

    @Override
    public void process() {
        logger.debug("Orqaga tugmasi bosildi (Data: {})", data);
        try {
            if (data.equals("back_to_cabinet")) {
                new StatisticsCmd(tgUser).process();
            }
        } catch (Exception e) {
            logger.error("Orqaga tugmasini qayta ishlashda xatolik: {}", e.getMessage(), e);
        }
    }
}