package com.example.studyE.entity;

package com.example.studyE.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "part_progress")
public class PartProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    Integer partId;

    String partName;

    Integer totalSentences;

    Integer completedSentences;

    Double completionPercentage;

    @Column(name = "last_updated")
    Instant lastUpdated;
}