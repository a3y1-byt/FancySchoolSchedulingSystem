package com.byt.data.scheduling;

import com.byt.data.user_system.Student;
import com.byt.enums.scheduling.StudyProgramLevel;
import com.byt.enums.scheduling.SubjectType;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.byt.workarounds.Success;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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

    private Student sampleStudent = new Student("Name", "Surname", null, LocalDate.now(), "+380000999999", "a@a.a", new ArrayList<StudyLanguage>() {{ add(StudyLanguage.ENGLISH); }}, StudyStatus.ACTIVE);
}

class TestSubject extends Subject  {

    public TestSubject(Subject prototype) {
        super(prototype.getName(), prototype.getHours(), prototype.getTypes(), prototype.getLessons(), prototype.getSpecializations());
    }
}

class TestStudyPrograms extends StudyProgram {
    public TestStudyPrograms(StudyProgram prototype) {
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
}