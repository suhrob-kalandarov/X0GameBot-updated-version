package org.botcontrol.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MultiplayerUser {
    @Id
    private Long userId;
    private String fullName;
    private String language;
    private List<MultiGame> games;
}
