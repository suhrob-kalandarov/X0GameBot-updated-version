package org.exp.entity;

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
    private Integer messageId;

    private AdminState state;

    private String message;

    private String photo;
    private String photoCaption;
}