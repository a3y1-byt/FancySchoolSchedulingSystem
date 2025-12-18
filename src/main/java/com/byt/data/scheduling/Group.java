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
    String name;
    StudyLanguage language;
    int maxCapacity;
    int yearOfStudy;
    List<String> notes;
    List<Lesson> lessons;
    List<Student> students;

    public static Group copy(Group group) {
        return Group.builder()
                .name(group.getName())
                .language(group.getLanguage())
                .maxCapacity(group.getMaxCapacity())
                .yearOfStudy(group.getYearOfStudy())
                .notes(group.getNotes())
                .build();
    }
}
