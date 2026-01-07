package com.byt.services.user_system;
import com.byt.validation.user_system.FreeListenerValidator;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.data.user_system.FreeListener;
import com.byt.enums.user_system.StudyLanguage;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

public class FreeListenerService implements CRUDService<FreeListener> {

    // comments explaining how everything works are in FreeListener Service
    private final SaveLoadService service;
    private List<FreeListener> freeListeners;

    private static final Type FREELISTENER_LIST_TYPE = new TypeToken<List<FreeListener>>() {
    }.getType();

    public FreeListenerService(SaveLoadService service, List<FreeListener> freeListeners) {
        this.service = service;
        this.freeListeners = freeListeners != null ? copyList(freeListeners) : new ArrayList<>();
    }

    public FreeListenerService(SaveLoadService service) {
        this(service, null);
    }

    @Override
    public void initialize() throws IOException {
        List<FreeListener> loaded = loadFromDb(); // raw objects from our 'DB'
        this.freeListeners = copyList(loaded); // safe deep copies
    }

    // _________________________________________________________

    public FreeListener create(String firstName, String lastName, String familyName,
                               LocalDate dateOfBirth, String phoneNumber, String email,
                               List<StudyLanguage> languagesOfStudies,
                               String notes) throws IOException {

        FreeListenerValidator.validateFreeListener(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies, notes);

        FreeListener freeListener = new FreeListener(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new ArrayList<>(languagesOfStudies), notes
        );
        if (freeListener.getEmail() != null && exists(freeListener.getEmail())) {
            throw new IllegalStateException("freeListener exists with this email already");
        }

        freeListeners.add(freeListener);
        saveToDb();

        return copy(freeListener);
    }

    @Override
    public void create(FreeListener prototype) throws IllegalArgumentException, IOException {

        FreeListenerValidator.validateClass(prototype);

        if (prototype.getEmail() != null && exists(prototype.getEmail())) {
            throw new IllegalArgumentException("freeListener with email = " + prototype.getEmail() + " already exists");
        }

        FreeListener toStore = copy(prototype);
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
                return Optional.of(copy(freeListener));
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
            throw new IllegalArgumentException("FreeListener with email=" + email + " not found");
        }

        String newEmail = prototype.getEmail();

        if (!Objects.equals(newEmail, email)) {
            if (newEmail != null && exists(newEmail)) {
                throw new IllegalArgumentException("FreeListener with email=" + newEmail + " already exists");
            }
            freeListeners.remove(index);

            FreeListener toStore = copy(prototype);
            freeListeners.add(toStore);
        } else {
            FreeListener updatedCopy = copy(prototype);
            freeListeners.set(index, updatedCopy);
        }

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

    private FreeListener copy(FreeListener adm) {
        if (adm == null) return null;

        List<StudyLanguage> langs = adm.getLanguagesOfStudies();
        List<StudyLanguage> langsCopy = langs != null
                ? new ArrayList<>(langs)
                : new ArrayList<>();

        FreeListener copy = new FreeListener(
                adm.getFirstName(),
                adm.getLastName(),
                adm.getFamilyName(),
                adm.getDateOfBirth(),
                adm.getPhoneNumber(),
                adm.getEmail(),
                langsCopy,
                adm.getNotes()
        );
        copy.setEmail(adm.getEmail());
        return copy;
    }

    private List<FreeListener> copyList(List<FreeListener> source) {
        List<FreeListener> result = new ArrayList<>();
        if (source == null) return result;
        for (FreeListener a : source) {
            result.add(copy(a));
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
