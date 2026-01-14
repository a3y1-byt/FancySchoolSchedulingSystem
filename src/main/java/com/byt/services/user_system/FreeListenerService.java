package com.byt.services.user_system;
import com.byt.validation.user_system.FreeListenerValidator;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.data.user_system.FreeListener;
import com.byt.enums.user_system.StudyLanguage;
import com.google.gson.reflect.TypeToken;
import com.byt.services.reporting.IssueReportService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

public class FreeListenerService implements CRUDService<FreeListener> {

    // comments explaining how everything works are in FreeListener Service
    private final SaveLoadService service;
    private final IssueReportService issueReportService;
    private List<FreeListener> freeListeners;

    private static final Type FREELISTENER_LIST_TYPE = new TypeToken<List<FreeListener>>() {
    }.getType();

    public FreeListenerService(SaveLoadService service, List<FreeListener> freeListeners, IssueReportService issueReportService) {
        this.service = service;
        this.freeListeners = freeListeners != null ? new ArrayList<>(freeListeners.stream().map(FreeListener::copy).toList()) : new ArrayList<>();
        this.issueReportService = issueReportService;
    }

    public FreeListenerService(SaveLoadService service) {
        this(service, null, null);
    }

    @Override
    public void initialize() throws IOException {
        List<FreeListener> loaded = loadFromDb(); // raw objects from our 'DB'
        this.freeListeners = new ArrayList<>(loaded.stream().map(FreeListener::copy).toList()); // safe deep copies
    }

    // _________________________________________________________

    public FreeListener create(String firstName, String lastName, String familyName,
                               LocalDate dateOfBirth, String phoneNumber, String email,
                               Set<StudyLanguage> languagesOfStudies,
                               String notes) throws IOException {

        FreeListenerValidator.validateFreeListener(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies, notes);

        FreeListener freeListener = new FreeListener(
                firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new HashSet<>(languagesOfStudies),
                notes
        );

        if (freeListener.getEmail() != null && exists(freeListener.getEmail())) {
            throw new IllegalStateException("freeListener exists with this email already");
        }

        freeListeners.add(FreeListener.copy(freeListener));
        saveToDb();
        return FreeListener.copy(freeListener);
    }

    @Override
    public void create(FreeListener prototype) throws IllegalArgumentException, IOException {

        FreeListenerValidator.validateClass(prototype);

        if (prototype.getEmail() != null && exists(prototype.getEmail())) {
            throw new IllegalArgumentException("freeListener with email = " + prototype.getEmail() + " already exists");
        }

        FreeListener toStore = FreeListener.copy(prototype);
        freeListeners.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<FreeListener> get(String email) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        for (FreeListener freeListener : freeListeners) {
            if (Objects.equals(freeListener.getEmail(), email)) {
                return Optional.of(FreeListener.copy(freeListener));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<FreeListener> getAll() throws IOException {
        return new ArrayList<>(freeListeners.stream().map(FreeListener::copy).toList());
    }

    @Override
    public void update(String email, FreeListener prototype) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        FreeListenerValidator.validateClass(prototype);

        String oldEmail = email;

        int index = -1;
        for (int i = 0; i < freeListeners.size(); i++) {
            if (Objects.equals(freeListeners.get(i).getEmail(), email)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("FreeListener with email=" + email + " not found");
        }

        String newEmail = prototype.getEmail();
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("new email must not be null or blank");
        }

        if (!Objects.equals(newEmail, email)) {
            if (exists(newEmail)) {
                throw new IllegalArgumentException("FreeListener with email=" + newEmail + " already exists");
            }

            freeListeners.remove(index);
            freeListeners.add(FreeListener.copy(prototype));
        } else {
            freeListeners.set(index, FreeListener.copy(prototype));
        }

        saveToDb();

        if (issueReportService != null && !Objects.equals(oldEmail, newEmail)) {
            issueReportService.updateReporterEmail(oldEmail, newEmail);
        }
    }


    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        for (int i = 0; i < freeListeners.size(); i++) {
            if (Objects.equals(freeListeners.get(i).getEmail(), email)) {
                freeListeners.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("FreeListener with email=" + email + " not found");
    }

    @Override
    public boolean exists(String email) throws IOException {
        if (email == null || email.isBlank()) {
            return false;
        }
        for (FreeListener freeListener : freeListeners) {
            if (Objects.equals(freeListener.getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    // _________________________________________________________
    private List<FreeListener> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.FREE_LISTENERS)) {
            return new ArrayList<>();
        }

        Object loaded = service.load(DataSaveKeys.FREE_LISTENERS, FREELISTENER_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<FreeListener> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof FreeListener freeListener) {
                    result.add(freeListener);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    private void saveToDb() throws IOException {
        service.save(DataSaveKeys.FREE_LISTENERS, freeListeners);
    }

}
