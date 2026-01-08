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

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    Building building;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @Builder.Default
    HashSet<Lesson> lessons = new HashSet<>();


    public void addBuilding(Building building) {
        Validator.validateBuilding(building);
        if(this.building == building) return;

        if(this.building != null) {
            Building oldBuilding = this.building;
            this.building = null;
            oldBuilding.removeClassRoom(this);
        }

        this.building = building;
        building.addClassRoom(this);
    }

    public void removeBuilding(Building building) {
        if(this.building == null || !this.building.equals(building)) return;

        Building oldBuilding = this.building;
        this.building = null;

        oldBuilding.removeClassRoom(this);
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

    public Building getBuilding() {
        return building;
    }


    public static ClassRoom copy(ClassRoom original) {
        return ClassRoom.builder()
                .name(original.getName())
                .floor(original.getFloor())
                .capacity(original.getCapacity())
                .build();
    }
}
