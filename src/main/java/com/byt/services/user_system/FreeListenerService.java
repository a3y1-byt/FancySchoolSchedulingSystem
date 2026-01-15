package com.byt.services.user_system;

import com.byt.data.scheduling.Group;
import com.byt.validation.user_system.FreeListenerValidator;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.data.user_system.FreeListener;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.validation.user_system.TeacherValidator;
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
        this.freeListeners = freeListeners != null ? new ArrayList<>(freeListeners) : new ArrayList<>();

    }

    public FreeListenerService(SaveLoadService service) {
        this(service, null);
    }

    @Override
    public void initialize() throws IOException {
        List<FreeListener> loaded = loadFromDb(); // raw objects from our 'DB'
        this.freeListeners = new ArrayList<>(loaded);
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

        freeListeners.add(freeListener);
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

        // firstly we find the old stored student
        int index = -1;
        FreeListener oldStored = null;

        for (int i = 0; i < freeListeners.size(); i++) {
            if (Objects.equals(freeListeners.get(i).getEmail(), email)) {
                index = i;
                oldStored = freeListeners.get(i);
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("freeListeners with email=" + email + " not found");
        }

        // then, we validate email change, in order to avoid possible duplicates
        String newEmail = prototype.getEmail();
        if (!Objects.equals(newEmail, email)) {
            if (newEmail != null && exists(newEmail)) {
                throw new IllegalArgumentException("freeListeners with email=" + newEmail + " already exists");
            }
        }

        // then, we collect references (FREELISTENERS - GROUP)
        Set<Group> oldGroups = oldStored.getGroups();


        // theen, we remove connection with old instances from references
        for (Group l : oldGroups) {
            l.removeFreeListener(oldStored);
        }

        // finally, we are creating a new student instance (just a copy)
        FreeListener newStored = FreeListener.copy(prototype);

        // AND ----- attaching new instance to the same reference the old one was attached to
        freeListeners.set(index, newStored);

        for (Group l : oldGroups) {
            l.addFreeListener(newStored);
        }
        saveToDb();
    }

    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email must not be null or blank");
        }

        // firstly we find the old stored student
        int index = -1;
        FreeListener oldStored = null;

        for (int i = 0; i < freeListeners.size(); i++) {
            if (Objects.equals(freeListeners.get(i).getEmail(), email)) {
                index = i;
                oldStored = freeListeners.get(i);
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("freeListeners with email=" + email + " not found");
        }

        // then, we collect references (FREELISTENERS - GROUP)
        Set<Group> oldGroups = oldStored.getGroups();


        // theen, we remove connection with old instances from references
        for (Group l : oldGroups) {
            l.removeFreeListener(oldStored);
        }

        freeListeners.remove(index);

        saveToDb();
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
