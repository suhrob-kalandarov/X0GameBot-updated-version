package org.botcontrol.entities;

import static org.botcontrol.botservice.msgservice.ResourceMessageManager.getString;

public interface DifficultyLevel {
    String LEVEL_EASY = "easy";
    String LEVEL_MEDIUM = "medium";
    String LEVEL_HARD = "hard";
    String LEVEL_EXTREME = "extreme";

    static String getTrueLevelMsg(String level) {
        return getString("level_" + level);
    }
}