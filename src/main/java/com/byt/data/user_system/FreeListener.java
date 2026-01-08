package com.byt.data.user_system;

import com.byt.data.scheduling.Group;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.validation.scheduling.Validator;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"groups"})
@Getter(AccessLevel.NONE)
@Setter(AccessLevel.NONE)
public class FreeListener extends Attendee {

    public static final int MAX_NOTES_LENGTH = 1000;

    private String notes;

    public FreeListener(String firstName, String lastName, String familyName,
                        LocalDate dateOfBirth, String phoneNumber, String email,
                        Set<StudyLanguage> languagesOfStudies,
                        String notes) {

        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email, languagesOfStudies);
        this.notes = notes;
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public static FreeListener copy(FreeListener fl) {
        if (fl == null) return null;

        FreeListener copy = new FreeListener(
                fl.getFirstName(),
                fl.getLastName(),
                fl.getFamilyName(),
                fl.getDateOfBirth(),
                fl.getPhoneNumber(),
                fl.getEmail(),
                new HashSet<>(fl.getLanguagesOfStudies()),
                fl.getNotes()
        );

        copy.groups = new HashSet<>(fl.groups);
        return copy;
    }

    // FREELISTENER -------- GROUP
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<Group> groups = new HashSet<>();

    public Set<Group> getGroups() {
        return new HashSet<>(groups);
    }

    public void addGroup(Group group) {
        Validator.validateGroup(group);
        if (groups.contains(group)) return;

        groups.add(group);
        group.addFreeListener(this);
    }

    public void removeGroup(Group group) {
        Validator.validateGroup(group);
        if (!groups.contains(group)) return;

        groups.remove(group);
        group.removeFreeListener(this);
    }
}
