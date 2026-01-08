package com.byt.data.user_system;

import com.byt.enums.user_system.StudyLanguage;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class Attendee extends User {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Set<StudyLanguage> languagesOfStudies = new HashSet<>();

    protected Attendee(String firstName, String lastName,
                       String familyName, LocalDate dateOfBirth,
                       String phoneNumber, String email,
                       Set<StudyLanguage> languagesOfStudies) {

        super(firstName, lastName, familyName, dateOfBirth, phoneNumber, email);

        // copy the collection, do NOT keep reference
        this.languagesOfStudies = (languagesOfStudies == null)
                ? new HashSet<>()
                : new HashSet<>(languagesOfStudies);
    }

    public Set<StudyLanguage> getLanguagesOfStudies() {
        return new HashSet<>(languagesOfStudies);
    }

    public void addLanguageOfStudy(StudyLanguage language) {
        Objects.requireNonNull(language, "Study language must not be null");
        languagesOfStudies.add(language);
    }

    public void removeLanguageOfStudy(StudyLanguage language) {
        Objects.requireNonNull(language, "Study language must not be null");
        languagesOfStudies.remove(language);
    }
}
