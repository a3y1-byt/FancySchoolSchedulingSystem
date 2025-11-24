package com.byt;

import com.byt.persistence.*;
import com.byt.persistence.util.DataSaveKeys;

public class FancySchedulingApplication {
    public static void main(String[] args) {
        SaveLoadService database = generatePersistenceService();
    }

    private static SaveLoadService generatePersistenceService() {
        DataSerializer serializer = new JsonDataSerializer();
        DataRepository repository = new InMemoryDataRepository();

        return new SaveLoadService(serializer, repository);
    }
}
