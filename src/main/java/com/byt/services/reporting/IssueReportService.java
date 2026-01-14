package com.byt.services.reporting;

import com.byt.data.reporting.IssueReport;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.validation.reporting.IssueReportValidator;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class IssueReportService implements CRUDService<IssueReport> {

    private final SaveLoadService service;
    private List<IssueReport> reports;

    private static final Type ISSUE_REPORT_LIST_TYPE = new TypeToken<List<IssueReport>>() {}.getType();

    private record CompositeKey(String email, String title) {}

    public IssueReportService(SaveLoadService service) {
        this.service = service;
        this.reports = new ArrayList<>();
    }

    public IssueReportService(SaveLoadService service, List<IssueReport> reports) {
        this.service = service;
        this.reports = copyList(reports);
    }

    @Override
    public void initialize() throws IOException {
        List<IssueReport> loaded = loadFromDb();
        this.reports = copyList(loaded);
    }

    // ----- Attribute association "reverse navigation"
    public List<IssueReport> getAllByEmail(String email) {
        IssueReportValidator.validateEmail(email);
        String n = normalizeEmail(email);

        List<IssueReport> result = new ArrayList<>();
        for (IssueReport r : reports) {
            if (r.getEmail() != null && normalizeEmail(r.getEmail()).equals(n)) {
                result.add(IssueReport.copy(r));
            }
        }
        return result;
    }

    public void updateReporterEmail(String oldEmail, String newEmail) throws IOException {
        IssueReportValidator.validateEmail(oldEmail);
        IssueReportValidator.validateEmail(newEmail);

        String oldN = normalizeEmail(oldEmail);
        String newN = normalizeEmail(newEmail);

        if (oldN.equals(newN)) return;

        // conflict check: newEmail|title must not already exist
        for (IssueReport r : reports) {
            if (r.getEmail() == null || r.getTitle() == null) continue;

            if (normalizeEmail(r.getEmail()).equals(oldN)) {
                String conflictId = compositeId(newEmail, r.getTitle());
                if (exists(conflictId)) {
                    throw new IllegalStateException(
                            "Email change causes IssueReport key conflict for title: " + r.getTitle()
                    );
                }
            }
        }

        for (IssueReport r : reports) {
            if (r.getEmail() != null && normalizeEmail(r.getEmail()).equals(oldN)) {
                r.setEmail(newEmail);
            }
        }

        saveToDb();
    }

    // ----- CRUDService

    @Override
    public void create(IssueReport prototype) throws IllegalArgumentException, IOException {
        IssueReportValidator.validatePrototype(prototype);

        if (prototype.getCreatedAt() == null) {
            prototype.setCreatedAt(LocalDateTime.now());
        }

        String id = compositeId(prototype.getEmail(), prototype.getTitle());
        if (exists(id)) {
            throw new IllegalArgumentException("IssueReport already exists with key " + id);
        }

        reports.add(IssueReport.copy(prototype));
        saveToDb();
    }

    @Override
    public Optional<IssueReport> get(String id) throws IllegalArgumentException {
        // IMPORTANT: for generic CRUD tests, get("TestObject") should return empty, not throw
        CompositeKey key;
        try {
            key = parseCompositeId(id);
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }

        for (IssueReport r : reports) {
            if (sameKey(r, key)) {
                return Optional.of(IssueReport.copy(r));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<IssueReport> getAll() throws IOException {
        return copyList(reports);
    }

    @Override
    public void update(String id, IssueReport prototype) throws IllegalArgumentException, IOException {
        IssueReportValidator.validateId(id);
        if (prototype == null) {
            throw new IllegalArgumentException("IssueReport must not be null");
        }
        if (prototype.getDescription() == null || prototype.getDescription().isBlank()) {
            throw new IllegalArgumentException("description must not be blank");
        }

        CompositeKey key = parseCompositeId(id);

        for (int i = 0; i < reports.size(); i++) {
            IssueReport current = reports.get(i);
            if (!sameKey(current, key)) continue;

            // email + title must stay the same because they form the key
            IssueReport updated = new IssueReport(
                    current.getEmail(),
                    current.getTitle(),
                    prototype.getDescription(),
                    current.getCreatedAt()
            );

            // allow updating createdAt only if provided (optional)
            if (prototype.getCreatedAt() != null) {
                updated.setCreatedAt(prototype.getCreatedAt());
            }

            reports.set(i, IssueReport.copy(updated));
            saveToDb();
            return;
        }

        throw new IllegalArgumentException("IssueReport with id " + id + " not found");
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        IssueReportValidator.validateId(id);
        CompositeKey key = parseCompositeId(id);

        for (int i = 0; i < reports.size(); i++) {
            if (sameKey(reports.get(i), key)) {
                reports.remove(i);
                saveToDb();
                return;
            }
        }

        throw new IllegalArgumentException("IssueReport with id " + id + " not found");
    }

    @Override
    public boolean exists(String id) throws IOException {
        CompositeKey key;
        try {
            key = parseCompositeId(id);
        } catch (IllegalArgumentException ex) {
            return false;
        }

        for (IssueReport r : reports) {
            if (sameKey(r, key)) return true;
        }
        return false;
    }

    // ----- Key helpers

    public static String compositeId(String email, String title) {
        return normalizeEmail(email) + "|" + normalizeTitle(title);
    }

    private static CompositeKey parseCompositeId(String id) {
        IssueReportValidator.validateId(id);

        int idx = id.indexOf('|');
        String email = id.substring(0, idx);
        String title = id.substring(idx + 1);

        return new CompositeKey(normalizeEmail(email), normalizeTitle(title));
    }

    private static boolean sameKey(IssueReport r, CompositeKey key) {
        if (r == null || r.getEmail() == null || r.getTitle() == null) return false;

        String reportEmail = normalizeEmail(r.getEmail());
        String reportTitle = normalizeTitle(r.getTitle());

        return reportEmail.equals(key.email) && reportTitle.equals(key.title);
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private static String normalizeTitle(String title) {
        return title.trim();
    }

    private List<IssueReport> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.ISSUE_REPORTS)) {
            return new ArrayList<>();
        }

        Object loaded = service.load(DataSaveKeys.ISSUE_REPORTS, ISSUE_REPORT_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<IssueReport> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof IssueReport r) {
                    result.add(r);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    private void saveToDb() throws IOException {
        service.save(DataSaveKeys.ISSUE_REPORTS, reports);
    }

    private static List<IssueReport> copyList(List<IssueReport> src) {
        List<IssueReport> out = new ArrayList<>();
        if (src == null) return out;

        for (IssueReport r : src) {
            out.add(IssueReport.copy(r));
        }
        return out;
    }
}
