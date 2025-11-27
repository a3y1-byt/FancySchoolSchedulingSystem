package com.byt.reporting.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.reporting.IssueReport;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class IssueReportService {
    private final SaveLoadService saveLoadService;

    public IssueReportService(SaveLoadService saveLoadService) {
        this.saveLoadService = saveLoadService;
    }

    public List<IssueReport> listReports() {
        try {
            if (!saveLoadService.canLoad(DataSaveKeys.ISSUE_REPORTS)) {
                return new ArrayList<>();
            }

            Type type = new TypeToken<List<IssueReport>>() {}.getType();
            Object loaded = saveLoadService.load(DataSaveKeys.ISSUE_REPORTS, type);
            if (loaded == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>((List<IssueReport>) loaded);
        } catch (IOException e) {
            System.err.println("Failed to load issue reports " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public Optional<IssueReport> getReportById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return listReports().stream()
                .filter(r -> id.equals(r.getId()))
                .findFirst();
    }

    public IssueReport addReport(String title, String description) {
        return addReport(nextId(), title, description, LocalDateTime.now());
    }

    public IssueReport addReport(Long id, String title, String description, LocalDateTime createdAt) {
        validateId(id);
        validateTitle(title);
        validateDescription(description);
        validateCreatedAt(createdAt);

        List<IssueReport> reports = listReports();

        boolean exists = reports.stream().anyMatch(r -> id.equals(r.getId()));
        if (exists) {
            throw new IllegalArgumentException("IssueReport with id " + id + " already exists");
        }

        IssueReport report = new IssueReport(id, title, description, createdAt);
        reports.add(report);
        saveAllReports(reports);
        return report;
    }

    public IssueReport updateReport(Long id, String title, String description) {
        validateId(id);
        validateTitle(title);
        validateDescription(description);

        List<IssueReport> reports = listReports();

        IssueReport existing = reports.stream()
                .filter(r -> id.equals(r.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("IssueReport with id " + id + " not found"));

        existing.setTitle(title);
        existing.setDescription(description);

        saveAllReports(reports);
        return existing;
    }

    public boolean deleteReport(Long id) {
        validateId(id);

        List<IssueReport> reports = listReports();
        boolean removed = reports.removeIf(r -> id.equals(r.getId()));

        if (removed) {
            saveAllReports(reports);
        }
        return removed;
    }

    private void saveAllReports(List<IssueReport> reports) {
        try {
            saveLoadService.save(DataSaveKeys.ISSUE_REPORTS, reports);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save issue reports", e);
        }
    }

    private Long nextId() {
        return listReports().stream()
                .map(IssueReport::getId)
                .filter(x -> x != null)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1L;
    }

    private static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("id must be positive");
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
    }

    private static void validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("createdAt must not be null");
        }
    }
}
