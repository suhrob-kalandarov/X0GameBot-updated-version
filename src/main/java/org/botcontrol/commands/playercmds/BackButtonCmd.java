package org.botcontrol.commands.playercmds;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.entities.User;

@RequiredArgsConstructor
public class BackButtonCmd implements BotCommand {
    private final User tgUser;
    private final String data;

    private static final Logger logger = LogManager.getLogger(BackButtonCmd.class);

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