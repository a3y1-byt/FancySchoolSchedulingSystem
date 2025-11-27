package com.byt.reporting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueReport {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
}
