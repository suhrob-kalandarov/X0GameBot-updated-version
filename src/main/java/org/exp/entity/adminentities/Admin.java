package org.exp.entity.adminentities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private Long chatId;
    private String username;
    private Integer messageId;

    private AdminState adminState;

    private String message;

    private String photo;
    private String photoCaption;
}