package com.byt.data.scheduling;

import static org.junit.jupiter.api.Assertions.*;

import com.byt.data.user_system.Student;
import com.byt.enums.scheduling.StudyProgramLevel;
import com.byt.workarounds.Success;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

public class StudyProgramTest {
    private final Specialization sampleSpecialization = Specialization.builder()
            .name("SampleSpecialization")
            .description("SampleDescription")
            .subjects(new HashSet<>())
            .studyPrograms(new HashSet<>())
            .students(new HashSet<>())
            .build();

    @Test
    public void testGetterReturnsCorrectContents() {
        StudyProgram studyProgram = StudyProgram.builder()
                .name("SampleName")
                .level(StudyProgramLevel.BACHELOR)
                .specializations(new HashSet<>() {{ add(sampleSpecialization); }})
                .build();

        Set<Specialization> expected = new HashSet<>() {{ add(sampleSpecialization); }};
        Set<Specialization> specializations = studyProgram.getSpecializations();

        assertArrayEquals(expected.toArray(), specializations.toArray());
    }

    @Test
    public void testGetterHasNoEscapingReferences() {
        StudyProgram studyProgram = StudyProgram.builder()
                .name("SampleName")
                .level(StudyProgramLevel.BACHELOR)
                .specializations(new HashSet<>() {{ add(sampleSpecialization); }})
                .build();

        Set<Specialization> expected = new HashSet<>() {{ add(sampleSpecialization); }};

        Set<Specialization> receivedSpecializations = studyProgram.getSpecializations();
        receivedSpecializations.remove(sampleSpecialization);

        assertArrayEquals(expected.toArray(), studyProgram.getSpecializations().toArray());
    }

    @Test
    public void testTriesAddingItselfToOther() {
        StudyProgram studyProgram = StudyProgram.builder()
                .name("SampleName")
                .level(StudyProgramLevel.BACHELOR)
                .specializations(new HashSet<>())
                .build();

        TestSpecialization anotherSpecialization = new TestSpecialization(sampleSpecialization);

        assertThrows(Success.class, () -> studyProgram.addSpecialization(anotherSpecialization));
    }

    @Test
    public void testTriesRemovingItselfFromOther() {
        TestSpecialization anotherSpecialization = new TestSpecialization(sampleSpecialization);

        StudyProgram studyProgram = StudyProgram.builder()
                .name("SampleName")
                .level(StudyProgramLevel.BACHELOR)
                .specializations(new HashSet<>() {{ add(anotherSpecialization); }})
                .build();

        assertThrows(Success.class, () -> studyProgram.removeSpecialization(anotherSpecialization));
    }
}

class TestSpecialization extends Specialization {
    public TestSpecialization(Specialization sample) {
        super(sample.getName(), sample.getDescription(), sample.getSubjects(), sample.getStudyPrograms(), sample.getStudents());
    }

    @Override
    public void addStudyProgram(StudyProgram studyProgram) {
        throw new Success();
    }

    @Override
    public void removeStudyProgram(StudyProgram studyProgram) {
        throw new Success();
    }

    @Override
    public void addStudent(Student student) {
        throw new Success();
    }

    @Override
    public void removeStudent(Student student) {
        throw new Success();
    }

    @Override
    public void addSubject(Subject subject) {
        throw new Success();
    }

    @Override
    public void removeSubject(Subject subject) {
        throw new Success();
    }
}

