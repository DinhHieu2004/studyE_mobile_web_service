package com.example.studyE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "topic_vocabulary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopicVocabulary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "image")
    private String imagePath;
}
