package com.byt.validation.reporting;

import com.byt.data.reporting.IssueReport;

public class IssueReportValidator {

    private IssueReportValidator() {}

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }
    }

    public static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be null or blank");
        }
    }

    // Composite key format: "email|title"
    public static void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        int idx = id.indexOf('|');
        if (idx <= 0 || idx >= id.length() - 1) {
            throw new IllegalArgumentException("id must have format email|title");
        }

        if (id.indexOf('|', idx + 1) != -1) {
            throw new IllegalArgumentException("id must contain exactly one '|'");
        }

        String emailPart = id.substring(0, idx).trim();
        String titlePart = id.substring(idx + 1).trim();

        validateEmail(emailPart);
        validateTitle(titlePart);
    }

    public static void validatePrototype(IssueReport prototype) {
        if (prototype == null) {
            throw new IllegalArgumentException("IssueReport must not be null");
        }

        validateEmail(prototype.getEmail());
        validateTitle(prototype.getTitle());

        if (prototype.getDescription() == null || prototype.getDescription().isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }
}
