package com.byt.services.reporting;

import com.byt.data.reporting.IssueReport;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
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

    private static final Type ISSUE_REPORT_LIST_TYPE =
            new TypeToken<List<IssueReport>>() {}.getType();

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
        this.reports = copyList(loadFromDb());
    }

    @Deprecated
    public void init() throws IOException {
        initialize();
    }

    /**
     * Public helper to build composite id that callers can use
     * id will look like "email|title"
     */
    public static String compositeId(String email, String title) {
        validateEmail(email);
        validateTitle(title);
        return normalizeEmail(email) + "|" + normalizeTitle(title);
    }

    @Override
    public void create(IssueReport prototype) throws IllegalArgumentException, IOException {
        validatePrototype(prototype);

        IssueReport toStore = copy(prototype);

        if (toStore.getCreatedAt() == null) {
            toStore.setCreatedAt(LocalDateTime.now());
        }

        if (exists(compositeId(toStore.getEmail(), toStore.getTitle()))) {
            throw new IllegalArgumentException(
                    "IssueReport for email " + toStore.getEmail()
                            + " and title " + toStore.getTitle()
                            + " already exists"
            );
        }

        reports.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<IssueReport> get(String id) throws IllegalArgumentException {
        // keep old behavior: null or blank id is an error
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        CompositeKey key;
        try {
            // parse email|title
            key = parseCompositeId(id);
        } catch (IllegalArgumentException e) {
            // if format is wrong (no '|', etc) we treat it as "not found"
            // this is needed so CRUDServiceTest.testReturnsNullOnGetByNonExistentId passes
            return Optional.empty();
        }

        for (IssueReport r : reports) {
            if (sameKey(r, key)) {
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
        CompositeKey key = parseCompositeId(id);
        validatePrototype(prototype);

        for (int i = 0; i < reports.size(); i++) {
            IssueReport current = reports.get(i);
            if (sameKey(current, key)) {
                IssueReport updatedCopy = copy(prototype);

                // if you do not want to allow changing email or title
                updatedCopy.setEmail(current.getEmail());
                updatedCopy.setTitle(current.getTitle());

                reports.set(i, updatedCopy);
                saveToDb();
                return;
            }
        }

        throw new IllegalArgumentException(
                "IssueReport for email " + key.email + " and title " + key.title + " not found"
        );
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        CompositeKey key = parseCompositeId(id);

        for (int i = 0; i < reports.size(); i++) {
            if (sameKey(reports.get(i), key)) {
                reports.remove(i);
                saveToDb();
                return;
            }
        }

        throw new IllegalArgumentException(
                "IssueReport for email " + key.email + " and title " + key.title + " not found"
        );
    }

    @Override
    public boolean exists(String id) {
        CompositeKey key;
        try {
            key = parseCompositeId(id);
        } catch (IllegalArgumentException e) {
            return false;
        }

        for (IssueReport r : reports) {
            if (sameKey(r, key)) {
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
        return new IssueReport(
                r.getEmail(),
                r.getTitle(),
                r.getDescription(),
                r.getCreatedAt(),
                r.getId()    // deprecated field kept as is
        );
    }

    private static List<IssueReport> copyList(List<IssueReport> list) {
        List<IssueReport> out = new ArrayList<>();
        if (list == null) return out;
        for (IssueReport r : list) {
            out.add(copy(r));
        }
        return out;
    }

    // composite key support

    private static class CompositeKey {
        final String email;
        final String title;

        CompositeKey(String email, String title) {
            this.email = email;
            this.title = title;
        }
    }

    private static CompositeKey parseCompositeId(String id) {
        validateId(id);

        int idx = id.indexOf('|');
        if (idx <= 0 || idx >= id.length() - 1) {
            throw new IllegalArgumentException("id must be in format email|title");
        }

        String emailPart = id.substring(0, idx);
        String titlePart = id.substring(idx + 1);

        validateEmail(emailPart);
        validateTitle(titlePart);

        return new CompositeKey(
                normalizeEmail(emailPart),
                normalizeTitle(titlePart)
        );
    }

    private static boolean sameKey(IssueReport r, CompositeKey key) {
        if (r.getEmail() == null || r.getTitle() == null) {
            return false;
        }

        String reportEmail = normalizeEmail(r.getEmail());
        String reportTitle = normalizeTitle(r.getTitle());

        return reportEmail.equals(key.email) && reportTitle.equals(key.title);
    }

    // validation and normalization

    private static void validateId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be null or blank");
        }
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private static String normalizeTitle(String title) {
        return title.trim();
    }

    private static void validatePrototype(IssueReport prototype) {
        if (prototype == null) {
            throw new IllegalArgumentException("IssueReport must not be null");
        }

        validateEmail(prototype.getEmail());
        validateTitle(prototype.getTitle());

        if (prototype.getDescription() == null || prototype.getDescription().isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }
        // createdAt can be null in prototype
        // create method will fill it with current time
    }
}
