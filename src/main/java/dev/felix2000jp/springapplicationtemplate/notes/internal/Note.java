package dev.felix2000jp.springapplicationtemplate.notes.internal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Entity(name = "note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Size(min = 3, max = 150)
    @NotBlank
    @Column(name = "title")
    private String title;

    @Size(min = 3, max = 5000)
    @NotBlank
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "appuser_id")
    private UUID appuserId;

    public Note() {
    }

    Note(String title, String content, UUID appuserId) {
        this.title = title;
        this.content = content;
        this.appuserId = appuserId;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public UUID getAppuserId() {
        return appuserId;
    }

    void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
