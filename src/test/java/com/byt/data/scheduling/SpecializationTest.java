package com.byt.data.scheduling;

import static org.junit.jupiter.api.Assertions.*;

import com.byt.data.user_system.Student;
import com.byt.enums.scheduling.StudyProgramLevel;
import com.byt.enums.scheduling.SubjectType;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.byt.workarounds.Success;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpecializationTest {
    private Subject sampleSubject = Subject.builder()
            .name("SampleName")
            .hours(100)
            .types(new ArrayList<>() {{ add(SubjectType.NORMAL_SUBJECT); }})
            .lessons(new HashSet<>())
            .specializations(new HashSet<>())
            .build();

    private StudyProgram sampleStudyProgram = StudyProgram.builder()
            .name("SampleName")
            .level(StudyProgramLevel.BACHELOR)
            .specializations(new HashSet<>())
            .build();

    private Student sampleStudent = new Student("Name", "Surname", null, LocalDate.now().minusYears(20), "380000999999", "a@a.com", new HashSet<StudyLanguage>() {{ add(StudyLanguage.ENGLISH); }}, StudyStatus.ACTIVE);

    private TestSubject testSubject = new TestSubject(sampleSubject);
    private TestStudyProgram testStudyProgram = new TestStudyProgram(sampleStudyProgram);
    private TestStudent testStudent = new TestStudent(sampleStudent);

    private Specialization emptySpecialization = new Specialization("SampleName", "SampleDescription",
            new HashSet<Subject>(),
            new HashSet<StudyProgram>(),
            new HashSet<Student>()
    );

    private Specialization filledSpecialization = new Specialization("SampleName", "SampleDescription",
            new HashSet<Subject>() {{ add(testSubject); }},
            new HashSet<StudyProgram>() {{ add(testStudyProgram); }},
            new HashSet<Student>() {{ add(testStudent); }}
    );

    @Test
    public void testTriesRemovingItselfFromOther() {
        assertAll(
                () -> assertThrows(Success.class, () -> filledSpecialization.removeSubject(testSubject)),
                () -> assertThrows(Success.class, () -> filledSpecialization.removeStudyProgram(testStudyProgram)),
                () -> assertThrows(Success.class, () -> filledSpecialization.removeStudent(testStudent))
        );
    }

    @Test
    public void testTriesAddingItselfToOthers() {
        assertAll(
                () -> assertThrows(Success.class, () -> emptySpecialization.addSubject(testSubject)),
                () -> assertThrows(Success.class, () -> emptySpecialization.addStudyProgram(testStudyProgram)),
                () -> assertThrows(Success.class, () -> emptySpecialization.addStudent(testStudent))
        );
    }

    @Test
    public void testDoesNotAddTheSameEntityTwice() {
        assertAll(
                () -> assertDoesNotThrow(() -> filledSpecialization.addSubject(testSubject)),
                () -> assertDoesNotThrow(() -> filledSpecialization.addStudyProgram(testStudyProgram)),
                () -> assertDoesNotThrow(() -> filledSpecialization.addStudent(testStudent))
        );
    }

    @Test
    public void testGettersDoNotHaveEscapingReferences() {
        assertAll(
                () -> assertFalse(filledSpecialization.getSubjects() == filledSpecialization.getSubjects()),
                () -> assertFalse(filledSpecialization.getStudyPrograms() == filledSpecialization.getStudyPrograms()),
                () -> assertFalse(filledSpecialization.getStudents() == filledSpecialization.getStudents())
        );
    }
}

class TestSubject extends Subject  {

    public TestSubject(Subject prototype) {
        super(prototype.getName(), prototype.getHours(), prototype.getTypes(), prototype.getLessons(), prototype.getSpecializations());
    }

    @Override
    public void addSpecialization(Specialization specialization) {
        throw new Success();
    }

    @Override
    public void removeSpecialization(Specialization specialization) {
        throw new Success();
    }
}

class TestStudyProgram extends StudyProgram {
    public TestStudyProgram(StudyProgram prototype) {
        super(prototype.getName(), prototype.getLevel(), prototype.getSpecializations());
    }

    @Override
    public void addSpecialization(Specialization specialization) {
        throw new Success();
    }

    @Override
    public void removeSpecialization(Specialization specialization) {
        throw new Success();
    }
}

class TestStudent extends Student {
    public TestStudent(Student prototype) {
        super(prototype.getFirstName(), prototype.getLastName(), prototype.getFamilyName(), prototype.getDateOfBirth(),
                prototype.getPhoneNumber(), prototype.getEmail(), prototype.getLanguagesOfStudies(), prototype.getStudiesStatus());
    }

    @Override
    public void addSpecialization(Specialization specialization) {
        throw new Success();
    }

    @Override
    public void removeSpecialization(Specialization specialization) {
        throw new Success();
    }
}