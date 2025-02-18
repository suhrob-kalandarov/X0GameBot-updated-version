package org.exp.entity.tguserentities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TgUser {
    private Long chatId;
    private String username;
    private Integer messageId;
    private State state;

    private String language;

    private int userScore = 0;
    private int drawScore = 0;
    private int botScore = 0;

    private String playerSymbol;
    private String botSymbol;

    private int[][] gameBoard;

    public void initializeBoard() {
        this.gameBoard = new int[3][3];
    }
}
