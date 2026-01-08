package com.byt.data.scheduling;

import com.byt.enums.scheduling.StudyProgramLevel;
import com.byt.validation.scheduling.Validator;
import com.byt.validation.user_system.UserValidator;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class StudyProgram {
    private String name;
    private StudyProgramLevel level;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private Set<Specialization> specializations = new HashSet<>();

    public static StudyProgram copy(StudyProgram program) {
        return StudyProgram.builder()
                .name(program.getName())
                .level(program.getLevel())
                .build();
    }

    public Set<Specialization> getSpecializations() {
        return new HashSet<>(specializations);
    }

    public void addSpecialization(Specialization specialization) {
        Validator.validateSpecialization(specialization);

        if (specializations.contains(specialization))
            return;

        specializations.add(specialization);
        specialization.addStudyProgram(this);
    }

    public void removeSpecialization(Specialization specialization) {
        Validator.validateSpecialization(specialization);

        if (!specializations.contains(specialization))
            return;

        specializations.remove(specialization);
        specialization.removeStudyProgram(this);
    }
}
