package com.byt.reporting;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.reporting.IssueReport;
import com.byt.services.CRUDService;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class IssueReportService implements CRUDService<IssueReport> {

    private static final Type ISSUE_REPORT_LIST_TYPE = new TypeToken<List<IssueReport>>() {}.getType();

    private final SaveLoadService saveLoadService;
    private List<IssueReport> reports;

    public IssueReportService(SaveLoadService saveLoadService) {
        this(saveLoadService, null);
    }

    public IssueReportService(SaveLoadService saveLoadService, List<IssueReport> initial) {
        this.saveLoadService = Objects.requireNonNull(saveLoadService, "saveLoadService must not be null");
        this.reports = initial != null ? copyList(initial) : new ArrayList<>();
    }

    @Override
    public void initialize() throws IOException {
        List<IssueReport> loaded = loadFromDb();
        this.reports = copyList(loaded);
    }

    // Convenience method for controllers
    public IssueReport create(String title, String description) throws IOException {
        return create(title, description, LocalDateTime.now());
    }

    public IssueReport create(String title, String description, LocalDateTime createdAt) throws IOException {
        validateTitle(title);
        validateDescription(description);
        validateCreatedAt(createdAt);

        IssueReport report = new IssueReport(null, title, description, createdAt);
        create(report);
        return copy(report);
    }

    @Override
    public void create(IssueReport prototype) throws IllegalArgumentException, IOException {
        validatePrototype(prototype);

        IssueReport toStore = copy(prototype);

        if (toStore.getId() == null || toStore.getId().isBlank()) {
            toStore.setId(java.util.UUID.randomUUID().toString());
        }

        if (exists(toStore.getId())) {
            throw new IllegalArgumentException("IssueReport with id=" + toStore.getId() + " already exists");
        }

        reports.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<IssueReport> get(String id) throws IllegalArgumentException {
        validateId(id);

        for (IssueReport r : reports) {
            if (Objects.equals(r.getId(), id)) {
                return Optional.of(copy(r));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<IssueReport> getAll() {
        return copyList(reports);
    }

    @Override
    public void update(String id, IssueReport prototype) throws IllegalArgumentException, IOException {
        validateId(id);
        validatePrototype(prototype);

        for (int i = 0; i < reports.size(); i++) {
            IssueReport current = reports.get(i);
            if (Objects.equals(current.getId(), id)) {
                IssueReport updatedCopy = copy(prototype);
                updatedCopy.setId(id);
                reports.set(i, updatedCopy);
                saveToDb();
                return;
            }
        }

        throw new IllegalArgumentException("IssueReport with id=" + id + " not found");
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        validateId(id);

        for (int i = 0; i < reports.size(); i++) {
            if (Objects.equals(reports.get(i).getId(), id)) {
                reports.remove(i);
                saveToDb();
                return;
            }
        }

        throw new IllegalArgumentException("IssueReport with id=" + id + " not found");
    }

    @Override
    public boolean exists(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        for (IssueReport r : reports) {
            if (Objects.equals(r.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    private List<IssueReport> loadFromDb() throws IOException {
        if (!saveLoadService.canLoad(DataSaveKeys.ISSUE_REPORTS)) {
            return new ArrayList<>();
        }

        Object loaded = saveLoadService.load(DataSaveKeys.ISSUE_REPORTS, ISSUE_REPORT_LIST_TYPE);

        if (loaded == null) {
            throw new IOException("Loaded issue reports are null. Stored data might be corrupted");
        }

        if (!(loaded instanceof List<?> raw)) {
            throw new IOException("Loaded issue reports have unexpected type " + loaded.getClass().getName());
        }

        List<IssueReport> result = new ArrayList<>();
        for (Object o : raw) {
            if (!(o instanceof IssueReport report)) {
                throw new IOException("Loaded issue reports contain non IssueReport element");
            }
            validatePrototype(report);
            result.add(report);
        }

        return result;
    }

    private void saveToDb() throws IOException {
        saveLoadService.save(DataSaveKeys.ISSUE_REPORTS, reports);
    }

    private static IssueReport copy(IssueReport r) {
        if (r == null) return null;
        return new IssueReport(r.getId(), r.getTitle(), r.getDescription(), r.getCreatedAt());
    }

    private static List<IssueReport> copyList(List<IssueReport> list) {
        List<IssueReport> out = new ArrayList<>();
        if (list == null) return out;
        for (IssueReport r : list) {
            out.add(copy(r));
        }
        return out;
    }

    private static void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
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

    private static void validatePrototype(IssueReport prototype) {
        if (prototype == null) {
            throw new IllegalArgumentException("IssueReport must not be null");
        }
        validateTitle(prototype.getTitle());
        validateDescription(prototype.getDescription());
        validateCreatedAt(prototype.getCreatedAt());
    }
}
