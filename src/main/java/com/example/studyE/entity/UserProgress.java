package com.example.studyE.entity;




import jakarta.persistence.*;
        import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
@Getter
@Setter
@NoArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean completed;

    private int score;

    @Column(name = "last_studied")
    private LocalDateTime lastStudied;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lession_id")
    private Lession lession;
}


