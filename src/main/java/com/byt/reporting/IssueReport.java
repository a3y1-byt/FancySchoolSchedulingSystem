package com.byt.reporting;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueReport {

    private Long id;
    private String title;
    private String description;

    private LocalDateTime createdAt;

}
