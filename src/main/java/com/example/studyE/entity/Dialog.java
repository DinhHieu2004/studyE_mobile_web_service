package com.example.studyE.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dialogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dialog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String speaker;

    @Column(name = "content", length = 5000, columnDefinition = "varchar(5000)")
    private String content;

    @Column(name = "audio_url")
    private String audioUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lession_id")
    private Lession lession;
}
