package com.byt.data.user_system;

import com.byt.data.scheduling.Group;
import com.byt.enums.user_system.StudyLanguage;
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
public class FreeListener extends Attendee {
    public static final int MAX_NOTES_LENGTH = 1000;
    private String notes;

    public FreeListener(String firstName, String lastName, String familyName,
                        LocalDate dateOfBirth, String phoneNumber, String email,
                        List<StudyLanguage> languagesOfStudies,
                        String notes) {

        super(firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                languagesOfStudies);

        this.notes = notes;
    }

    public static FreeListener copy(FreeListener fl) {
        if (fl == null) return null;

        List<StudyLanguage> langsCopy = (fl.getLanguagesOfStudies() != null)
                ? new ArrayList<>(fl.getLanguagesOfStudies())
                : new ArrayList<>();

        FreeListener copy = new FreeListener(
                fl.getFirstName(),
                fl.getLastName(),
                fl.getFamilyName(),
                fl.getDateOfBirth(),
                fl.getPhoneNumber(),
                fl.getEmail(),
                langsCopy,
                fl.getNotes()
        );

        copy.groups = (fl.groups != null) ? new HashSet<>(fl.groups) : new HashSet<>();

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

        if (groups.contains(group))
            return;

        groups.add(group);
        group.addFreeListener(this);
    }

    public void removeGroup(Group group) {
        Validator.validateGroup(group);

        if (!groups.contains(group)) {
            return;
        }

        groups.remove(group);
        group.removeFreeListener(this);
    }
}
