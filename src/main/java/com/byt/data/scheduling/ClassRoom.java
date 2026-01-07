package com.byt.data.scheduling;

import com.byt.validation.scheduling.Validator;
import lombok.*;

import java.util.HashSet;

@Data
@Builder
public class ClassRoom {
    String name;
    int floor;
    int capacity;

    @Setter(AccessLevel.NONE)
    Building building;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    HashSet<Lesson> lessons;


    public void addBuilding(Building building) {
        Validator.validateBuilding(building);

        this.building = building;
        building.addClassRoom(this);
    }

    public void removeBuilding(Building building) {
        if(!this.building.equals(building)) return;

        building.removeClassRoom(this);
        this.building = null;
    }

    public void addLesson(Lesson lesson) {
        Validator.validateLesson(lesson);
        if (lessons.contains(lesson)) return;

        lessons.add(lesson);
        lesson.addClassRoom(this);
    }

    public void removeLesson(Lesson lesson) {
        if (!lessons.contains(lesson)) return;

        lessons.remove(lesson);
        lesson.removeClassRoom(this);
    }

    public HashSet<Lesson> getLessons() {
        return new HashSet<>(lessons);
    }

    public static ClassRoom copy(ClassRoom original) {
        return ClassRoom.builder()
                .name(original.getName())
                .floor(original.getFloor())
                .capacity(original.getCapacity())
                .build();
    }
}
