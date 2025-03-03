package org.botcontrol.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    private String fullName;
    private String username;

    private Integer messageId;
    private String language;
    private UserState userState;
    private String difficultyLevel;

    private String playerSymbol;
    private String botSymbol;

    private int[][] gameBoard;

    public void initializeBoard() {
        this.gameBoard = new int[3][3];
    }
}