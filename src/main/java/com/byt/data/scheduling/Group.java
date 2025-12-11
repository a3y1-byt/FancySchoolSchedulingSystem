package com.byt.data.scheduling;

import com.byt.data.user_system.Student;
import com.byt.enums.user_system.StudyLanguage;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class Group {
    public static final int MAX_CAPACITY = 20;
    public static final int MIN_CAPACITY = 1;
    String name;
    StudyLanguage language;
    int maxCapacity;
    int minCapacity;
    int yearOfStudy;
    List<String> notes;
    List<Lesson> lessons;
    List<Student> students;

    public static Group copy(Group group) {
        return Group.builder()
                .name(group.getName())
                .language(group.getLanguage())
                .maxCapacity(group.getMaxCapacity())
                .minCapacity(group.getMinCapacity())
                .yearOfStudy(group.getYearOfStudy())
                .notes(group.getNotes())
                .build();
    }
}
