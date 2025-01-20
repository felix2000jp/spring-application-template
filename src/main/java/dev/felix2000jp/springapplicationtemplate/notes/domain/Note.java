package dev.felix2000jp.springapplicationtemplate.notes.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Entity
public class Note {

    @Id
    @NotNull
    private UUID id;

    @Column(name = "appuser_id")
    @NotNull
    private UUID appuserId;

    @Column(name = "title")
    @NotBlank
    private String title;

    @Column(name = "content")
    @NotBlank
    private String content;

    public Note() {
    }

    public Note(UUID appuserId, String title, String content) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.content = content;
        this.appuserId = appuserId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getAppuserId() {
        return appuserId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
