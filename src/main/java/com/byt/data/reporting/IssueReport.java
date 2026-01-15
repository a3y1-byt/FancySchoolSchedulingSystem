package com.byt.data.reporting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public class IssueReport {

    private String email;
    private String adminEmail;
    private String title;
    private String description;
    private LocalDateTime createdAt;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static IssueReport copy(IssueReport r) {
        if (r == null) return null;
        return new IssueReport(
                r.getEmail(),
                r.getAdminEmail(),
                r.getTitle(),
                r.getDescription(),
                r.getCreatedAt()
        );
    }
}
