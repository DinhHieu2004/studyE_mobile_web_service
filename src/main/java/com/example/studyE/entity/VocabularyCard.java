package com.example.studyE.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vocabularies_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    private String meaning;

    private String example;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_v_id")
    private TopicVocabulary topic;

    private String phonetic;

    @Column(name = "example_meaning")
    private String exampleMeaning;
}
