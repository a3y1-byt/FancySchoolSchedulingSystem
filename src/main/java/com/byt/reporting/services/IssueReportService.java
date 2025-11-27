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

    private static final Type REPORT_LIST_TYPE = new TypeToken<List<IssueReport>>() {}.getType();

    private final SaveLoadService saveLoadService;

    public IssueReportService(SaveLoadService saveLoadService) {
        if (saveLoadService == null) {
            throw new IllegalArgumentException("saveLoadService must not be null");
        }
        this.saveLoadService = saveLoadService;
    }

    public List<IssueReport> listReports() throws IOException {
        if (!saveLoadService.canLoad(DataSaveKeys.ISSUE_REPORTS)) {
            return new ArrayList<>();
        }

        Object loaded = saveLoadService.load(DataSaveKeys.ISSUE_REPORTS, REPORT_LIST_TYPE);

        if (loaded == null) {
            throw new IOException("Loaded issue reports are null. Stored data might be corrupted");
        }

        if (!(loaded instanceof List<?> rawList)) {
            throw new IOException("Loaded issue reports have unexpected type " + loaded.getClass().getName());
        }

        List<IssueReport> result = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof IssueReport report)) {
                throw new IOException("Loaded list contains non IssueReport element " +
                        (item == null ? "null" : item.getClass().getName()));
            }
            result.add(report);
        }

        return result;
    }

    public Optional<IssueReport> getReportById(Long id) throws IOException {
        if (id == null) return Optional.empty();
        return listReports().stream().filter(r -> id.equals(r.getId())).findFirst();
    }

    public IssueReport addReport(String title, String description) throws IOException {
        return addReport(nextId(), title, description, LocalDateTime.now());
    }

    public IssueReport addReport(Long id, String title, String description, LocalDateTime createdAt) throws IOException {
        validateId(id);
        validateTitle(title);
        validateDescription(description);
        validateCreatedAt(createdAt);

        List<IssueReport> reports = listReports();
        boolean exists = reports.stream().anyMatch(r -> id.equals(r.getId()));
        if (exists) {
            throw new IllegalArgumentException("IssueReport with id " + id + " already exists");
        }

        IssueReport report = IssueReport.builder()
                .id(id)
                .title(title)
                .description(description)
                .createdAt(createdAt)
                .build();

        reports.add(report);
        saveAllReports(reports);
        return report;
    }

    public IssueReport updateReport(Long id, String title, String description) throws IOException {
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

    public boolean deleteReport(Long id) throws IOException {
        validateId(id);

        List<IssueReport> reports = listReports();
        boolean removed = reports.removeIf(r -> id.equals(r.getId()));

        if (removed) {
            saveAllReports(reports);
        }
        return removed;
    }

    private void saveAllReports(List<IssueReport> reports) throws IOException {
        saveLoadService.save(DataSaveKeys.ISSUE_REPORTS, reports);
    }

    private Long nextId() throws IOException {
        return listReports().stream()
                .map(IssueReport::getId)
                .filter(x -> x != null)
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1L;
    }

    private static void validateId(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("id must be positive");
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("title must not be blank");
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) throw new IllegalArgumentException("description must not be blank");
    }

    private static void validateCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) throw new IllegalArgumentException("createdAt must not be null");
    }
}
