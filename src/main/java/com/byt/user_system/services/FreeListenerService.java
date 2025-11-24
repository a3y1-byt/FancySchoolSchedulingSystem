package com.byt.user_system.services;

import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.user_system.data.FreeListener;
import com.byt.user_system.data.Student;
import com.byt.user_system.enums.StudyLanguage;
import com.byt.user_system.enums.StudyStatus;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FreeListenerService {

    private final SaveLoadService service;
    private List<FreeListener> freeListeners;

    private static final Type FREE_LISTENER_LIST_TYPE =
            new TypeToken<List<FreeListener>>() {}.getType();

    public FreeListenerService(SaveLoadService service, List<FreeListener> freeListeners) {
        this.service = service;
        this.freeListeners = freeListeners;
    }

    @SuppressWarnings("unchecked")
    public void init() throws IOException {
        if (!service.canLoad(DataSaveKeys.FREE_LISTENERS)) {
            freeListeners = new ArrayList<>();
            return;
        }

        Object loaded = service.load(DataSaveKeys.FREE_LISTENERS, FREE_LISTENER_LIST_TYPE);

        if (loaded instanceof List<?>) {
            freeListeners = (List<FreeListener>) loaded;
        } else {
            freeListeners = new ArrayList<>();
        }
    }

    public void create(String firstName, String lastName, String familyName,
                       LocalDate dateOfBirth, String phoneNumber, String email,
                       List<StudyLanguage> languagesOfStudies,
                       String notes) throws IOException {
        FreeListener freeListener = new FreeListener(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                new ArrayList<>(languagesOfStudies), notes
        );

        freeListeners.add(freeListener);
        service.save(DataSaveKeys.FREE_LISTENERS, freeListeners);
    }

    public void create(FreeListener freeListener) throws IOException {
        freeListeners.add(freeListener);
        service.save(DataSaveKeys.FREE_LISTENERS, freeListeners);
    }

    public List<FreeListener> getAll() {
        return new ArrayList<>(freeListeners);
    }

    public FreeListener getById(String id) {
        for (FreeListener freeListener : freeListeners) {
            if (Objects.equals(freeListener.getId(), id)) {
                return freeListener;
            }
        }
        return null;
    }

    public void update(FreeListener updated) throws IOException {
        for (int i = 0; i < freeListeners.size(); i++) {
            FreeListener current = freeListeners.get(i);
            if (Objects.equals(current.getId(), updated.getId())) {
                freeListeners.set(i, updated);
                service.save(DataSaveKeys.FREE_LISTENERS, freeListeners);
                return;
            }
        }
    }

    public void deleteById(String id) throws IOException {
        for (int i = 0; i < freeListeners.size(); i++) {
            if (Objects.equals(freeListeners.get(i).getId(), id)) {
                freeListeners.remove(i);
                service.save(DataSaveKeys.FREE_LISTENERS, freeListeners);
                return;
            }
        }
    }
}
