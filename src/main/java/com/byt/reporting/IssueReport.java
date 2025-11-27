package com.byt.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueReport {

    private String id;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    public IssueReport(String title, String description, LocalDateTime createdAt) {
        this.id = java.util.UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
    }
}
