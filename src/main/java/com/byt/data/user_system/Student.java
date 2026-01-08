package com.byt.data.user_system;

import com.byt.data.scheduling.Group;
import com.byt.data.scheduling.Specialization;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.byt.validation.scheduling.Validator;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Student extends Attendee {

    private StudyStatus studiesStatus;

    public Student(String firstName, String lastName, String familyName,
                   LocalDate dateOfBirth, String phoneNumber, String email,
                   List<StudyLanguage> languagesOfStudies,
                   StudyStatus studiesStatus) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies);

        this.studiesStatus = studiesStatus;
    }

    public static Student copy(Student s) {
        if (s == null) return null;

        List<StudyLanguage> langsCopy = (s.getLanguagesOfStudies() != null)
                ? new ArrayList<>(s.getLanguagesOfStudies())
                : new ArrayList<>();

        return new Student(
                s.getFirstName(),
                s.getLastName(),
                s.getFamilyName(),
                s.getDateOfBirth(),
                s.getPhoneNumber(),
                s.getEmail(),
                langsCopy,
                s.getStudiesStatus()
        );
    }


    // STUDENT -------- SPECIALIZATION
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<Specialization> specializations = new HashSet<>();

    public Set<Specialization> getSpecializations() {
        return new HashSet<>(specializations);
    }

    public void addSpecialization(Specialization specialization) {
        Validator.validateSpecialization(specialization);

        if (specializations.contains(specialization))
            return;

        specializations.add(specialization);
        specialization.addStudent(this);
    }

    public void removeSpecialization(Specialization specialization) {
        Validator.validateSpecialization(specialization);

        if (!specializations.contains(specialization)) {
            return;
        }

        specializations.remove(specialization);
        specialization.removeStudent(this);
    }


    // STUDENT -------- GROUP
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<Group> groups = new HashSet<>();

    public Set<Group> getGroups() {
        return new HashSet<>(groups);
    }

    public void addGroup(Group group) {
        Validator.validateGroup(group);

        if (groups.contains(group))
            return;

        groups.add(group);
        group.addStudent(this);
    }

    public void removeGroup(Group group) {
        Validator.validateGroup(group);

        if (!groups.contains(group)) {
            return;
        }

        groups.remove(group);
        group.removeStudent(this);
    }

}
