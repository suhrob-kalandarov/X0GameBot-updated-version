package org.botcontrol.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiGame {
    private int gameId;

    private Long creatorId;

    private MultiplayerUser playerX;
    private MultiplayerUser playerO;

    private Long turn;

    private String inlineMessageId;

    private int[][] gameBoard;

    public void initializeBoard() {
        gameBoard = new int[3][3];
    }
}
