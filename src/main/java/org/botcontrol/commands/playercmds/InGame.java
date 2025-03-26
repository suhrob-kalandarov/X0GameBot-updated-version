package org.botcontrol.commands.playercmds;

import lombok.RequiredArgsConstructor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.botcontrol.entities.User;

import static org.botcontrol.botservice.msgservice.Constant.CELL;

@RequiredArgsConstructor
public class InGame implements BotCommand {
    private final User user;
    private final String data;

    private static final Logger logger = LogManager.getLogger(InGame.class);

    @Override
    public void process() {
        logger.debug("O'yin harakati qayta ishlanmoqda (Data: {})", data);
        try {
            if (data == null || !data.startsWith(CELL)) {
                logger.error("Noto'g'ri data format: {}", data);
                System.out.println("Noto'g'ri data format: " +  data);
                return;
            }

            int row = Integer.parseInt(data.split("_")[1]);
            int col = Integer.parseInt(data.split("_")[2]);

            logger.trace("Qabul qilingan katak: [{}, {}]", row, col);

            System.out.println("Qabul qilingan katak: "+ row + "_" + col);

            // Create a PlayGameCmd object and initialize the board
            new GameCmd(user).handleMove(row, col); // Handle the action

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}