package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.user_system.data.FreeListener;
import com.byt.user_system.enums.StudyLanguage;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.byt.user_system.validation.UserValidator;
import com.byt.user_system.validation.ValidationException;

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

        validateClassData(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies, notes);

        FreeListener freeListener = new FreeListener(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new ArrayList<>(languagesOfStudies), notes
        );

        freeListeners.add(freeListener);
        saveToDb();

        return copy(freeListener);
    }

    @Override
    public void create(FreeListener prototype) throws IllegalArgumentException, IOException {
        validateClass(prototype);

        FreeListener toStore = copy(prototype);
        freeListeners.add(toStore);
        saveToDb();
    }

    @Override
    public Optional<FreeListener> get(String id) throws IllegalArgumentException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        for (FreeListener freeListener : freeListeners) {
            if (Objects.equals(freeListener.getId(), id)) {
                return Optional.of(copy(freeListener));
            }
        }

        return Optional.empty();
    }

    @Override
    public List<FreeListener> getAll() throws IOException{
        return copyList(freeListeners);
    }

    @Override
    public void update(String id, FreeListener prototype) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        validateClass(prototype);

        for (int i = 0; i < freeListeners.size(); i++) {
            FreeListener current = freeListeners.get(i);
            if (Objects.equals(current.getId(), id)) {
                FreeListener updatedCopy = copy(prototype);
                updatedCopy.setId(id);
                freeListeners.set(i, updatedCopy);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("FreeListener with id=" + id + " not found");
    }

    @Override
    public void delete(String id) throws IllegalArgumentException, IOException {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be null or blank");
        }

        for (int i = 0; i < freeListeners.size(); i++) {
            if (Objects.equals(freeListeners.get(i).getId(), id)) {
                freeListeners.remove(i);
                saveToDb();
                return;
            }
        }
        throw new IllegalArgumentException("FreeListener with id=" + id + " not found");
    }

    @Override
    public boolean exists(String id) throws IOException{
        if (id == null || id.isBlank()) {
            return false;
        }
        for (FreeListener freeListener : freeListeners) {
            if (Objects.equals(freeListener.getId(), id)) {
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
        copy.setId(adm.getId());
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


    // VALIDATION METHODS
    private void validateClassData(
            String firstName,
            String lastName,
            String familyName,
            LocalDate dateOfBirth,
            String phoneNumber,
            String email,
            List<StudyLanguage> languagesOfStudies,
            String notes
    ) {
        // general USER class validation
        UserValidator.validateUserFields(
                firstName,
                lastName,
                familyName,
                dateOfBirth,
                phoneNumber,
                email
        );

        //  only FreeListener validation
        if (languagesOfStudies == null || languagesOfStudies.isEmpty()) {
            throw new ValidationException("FreeListener must have at least one study language");
        }

        // notes are nullable but I think it is ok to set them at max  = 1000
        int max_notes = 1000;
        if (notes != null && notes.length() > max_notes) {
            throw new ValidationException("Notes are too long");
        }
    }

    private void validateClass(FreeListener prototype) {
        if (prototype == null) {
            throw new ValidationException("FreeListener prototype must not be null");
        }

        validateClassData(
                prototype.getFirstName(),
                prototype.getLastName(),
                prototype.getFamilyName(),
                prototype.getDateOfBirth(),
                prototype.getPhoneNumber(),
                prototype.getEmail(),
                prototype.getLanguagesOfStudies(),
                prototype.getNotes()
        );
    }

}
