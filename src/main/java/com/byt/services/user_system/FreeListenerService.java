package com.byt.services.user_system;

import com.byt.data.user_system.FreeListener;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.validation.user_system.FreeListenerValidator;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class FreeListenerService implements CRUDService<FreeListener> {

    private final SaveLoadService service;
    private List<FreeListener> freeListeners;

    private static final Type FREELISTENER_LIST_TYPE = new TypeToken<List<FreeListener>>() {}.getType();

    public FreeListenerService(SaveLoadService service, List<FreeListener> freeListeners) {
        this.service = service;
        this.freeListeners = freeListeners != null ? copyList(freeListeners) : new ArrayList<>();
    }

    public FreeListenerService(SaveLoadService service) {
        this(service, null);
    }

    @Override
    public void initialize() throws IOException {
        List<FreeListener> loaded = loadFromDb();
        this.freeListeners = copyList(loaded);
    }

    public FreeListener create(String firstName, String lastName, String familyName,
                               LocalDate dateOfBirth, String phoneNumber, String email,
                               Set<StudyLanguage> languagesOfStudies,
                               String notes) throws IOException {

        FreeListenerValidator.validateFreeListener(
                firstName, lastName, familyName, dateOfBirth, phoneNumber, email, languagesOfStudies, notes
        );

        if (email != null && exists(email)) {
            throw new IllegalStateException("FreeListener exists with this email already");
        }

        FreeListener freeListener = new FreeListener(
                firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new HashSet<>(languagesOfStudies),
                notes
        );

        freeListeners.add(FreeListener.copy(freeListener));
        saveToDb();

        return FreeListener.copy(freeListener);
    }

    @Override
    public void create(FreeListener prototype) throws IllegalArgumentException, IOException {
        FreeListenerValidator.validateClass(prototype);

        String email = prototype.getEmail();
        if (email != null && exists(email)) {
            throw new IllegalArgumentException("FreeListener with email = " + email + " already exists");
        }

        freeListeners.add(FreeListener.copy(prototype));
        saveToDb();
    }

    @Override
    public Optional<FreeListener> get(String email) throws IllegalArgumentException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        for (FreeListener fl : freeListeners) {
            if (Objects.equals(fl.getEmail(), email)) {
                return Optional.of(FreeListener.copy(fl));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<FreeListener> getAll() throws IOException {
        return copyList(freeListeners);
    }

    @Override
    public void update(String email, FreeListener prototype) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        FreeListenerValidator.validateClass(prototype);

        int index = -1;
        for (int i = 0; i < freeListeners.size(); i++) {
            if (Objects.equals(freeListeners.get(i).getEmail(), email)) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new IllegalArgumentException("FreeListener with email = " + email + " not found");
        }

        String newEmail = prototype.getEmail();
        if (newEmail != null && !Objects.equals(newEmail, email) && exists(newEmail)) {
            throw new IllegalArgumentException("FreeListener with email = " + newEmail + " already exists");
        }

        freeListeners.set(index, FreeListener.copy(prototype));
        saveToDb();
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
        throw new IllegalArgumentException("FreeListener with email = " + email + " not found");
    }

    @Override
    public boolean exists(String email) throws IOException {
        if (email == null || email.isBlank()) return false;

        for (FreeListener fl : freeListeners) {
            if (Objects.equals(fl.getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    private List<FreeListener> copyList(List<FreeListener> source) {
        List<FreeListener> result = new ArrayList<>();
        if (source == null) return result;

        for (FreeListener fl : source) {
            result.add(FreeListener.copy(fl));
        }
        return result;
    }

    private List<FreeListener> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.FREE_LISTENERS)) {
            return new ArrayList<>();
        }

        Object loaded = service.load(DataSaveKeys.FREE_LISTENERS, FREELISTENER_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<FreeListener> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof FreeListener fl) {
                    result.add(fl);
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
