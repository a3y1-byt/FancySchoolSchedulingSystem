package com.byt.data.reporting;

import lombok.AllArgsConstructor;
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
}
