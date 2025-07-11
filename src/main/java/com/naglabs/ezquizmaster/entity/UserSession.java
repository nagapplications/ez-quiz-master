package com.naglabs.ezquizmaster.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "quiz_sessions")
@Data
public class UserSession {

    @Id
    @Column
    private String sessionId = UUID.randomUUID().toString();

    private String userEmail;

    private int currentQuestionIndex = 0;

    private int score = 0;

    private int remainingLifelines = 3;

    private boolean usedAlternate = false;
    private boolean usedFiftyFifty = false;
    private boolean usedSecondChance = false;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String primaryQuestionsJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String alternateQuestionsJson;
}

