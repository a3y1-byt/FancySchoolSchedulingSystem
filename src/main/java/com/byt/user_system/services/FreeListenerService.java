package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.user_system.data.FreeListener;
import com.byt.user_system.enums.StudyLanguage;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class FreeListenerService {

    // comments explaining how everything works are in Admin Service
    private final SaveLoadService service;
    private List<FreeListener> freeListeners;

    private static final Type FREELISTENER_LIST_TYPE = new TypeToken<List<FreeListener>>() {
    }.getType();

    public FreeListenerService(SaveLoadService service, List<FreeListener> freeListeners) {
        this.service = service;
        this.freeListeners = freeListeners != null ? copyList(freeListeners) : new ArrayList<>();
    }


    public void init() throws IOException {
        List<FreeListener> loaded = loadFromDb(); // raw objects from our 'DB'
        this.freeListeners = copyList(loaded); // safe deep copies
    }

    // _________________________________________________________

    public FreeListener create(String firstName, String lastName, String familyName,
                               LocalDate dateOfBirth, String phoneNumber, String email,
                               List<StudyLanguage> languagesOfStudies,
                               String notes) throws IOException {

        FreeListener freeListener = new FreeListener(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new ArrayList<>(languagesOfStudies), notes
        );

        freeListeners.add(freeListener);
        saveToDb();

        return copy(freeListener);
    }

    public FreeListener create(FreeListener freeListener) throws IOException {
        FreeListener toStore = copy(freeListener);
        freeListeners.add(toStore);
        saveToDb();
        return copy(toStore);
    }

    public List<FreeListener> getAll() {
        return copyList(freeListeners);
    }

    public FreeListener getById(String id) {
        for (FreeListener freeListener : freeListeners) {
            if (Objects.equals(freeListener.getId(), id)) {
                return copy(freeListener);
            }
        }
        return null;
    }

    public void update(FreeListener updated) throws IOException {
        for (int i = 0; i < freeListeners.size(); i++) {
            FreeListener current = freeListeners.get(i);
            if (Objects.equals(current.getId(), updated.getId())) {
                freeListeners.set(i, copy(updated));
                saveToDb();
                return;
            }
        }
    }

    public void deleteById(String id) throws IOException {
        for (int i = 0; i < freeListeners.size(); i++) {
            if (Objects.equals(freeListeners.get(i).getId(), id)) {
                freeListeners.remove(i);
                saveToDb();
                return;
            }
        }
    }
    // _________________________________________________________

    private FreeListener copy(FreeListener adm) {
        if (adm == null) return null;

        FreeListener copy = new FreeListener(
                adm.getFirstName(),
                adm.getLastName(),
                adm.getFamilyName(),
                adm.getDateOfBirth(),
                adm.getPhoneNumber(),
                adm.getEmail(),
                adm.getLanguagesOfStudies(),
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
}
