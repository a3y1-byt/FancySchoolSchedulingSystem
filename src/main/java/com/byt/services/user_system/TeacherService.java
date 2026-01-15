package com.byt.services.user_system;

import com.byt.data.scheduling.Lesson;
import com.byt.data.user_system.Teacher;
import com.byt.persistence.SaveLoadService;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDService;
import com.byt.validation.user_system.TeacherValidator;
import com.google.gson.reflect.TypeToken;
import com.byt.services.reporting.IssueReportService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

public class TeacherService implements CRUDService<Teacher> {

    private final SaveLoadService service;
    private List<Teacher> teachers;
    private final IssueReportService issueReportService;

    private static final Type TEACHER_LIST_TYPE = new TypeToken<List<Teacher>>() {
    }.getType();

    public TeacherService(SaveLoadService service, List<Teacher> teachers,  IssueReportService issueReportService) {
        this.service = service;
        this.teachers = teachers != null ? copyList(teachers) : new ArrayList<>();
        this.issueReportService = issueReportService;
        this.teachers = new ArrayList<>();
    }

    public TeacherService(SaveLoadService service) {
        this(service, null, null);
    }

    @Override
    public void initialize() throws IOException {
        List<Teacher> loaded = loadFromDb();
        this.teachers = new ArrayList<>(loaded);
    }

    public Teacher create(String firstName, String lastName, String familyName,
                          LocalDate dateOfBirth, String phoneNumber, String email,
                          LocalDate hireDate, String title,
                          String position) throws IOException {

        TeacherValidator.validateTeacher(
                firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title, position
        );

        if (email != null && exists(email)) {
            throw new IllegalStateException("Teacher exists with this email already");
        }

        Teacher teacher = new Teacher(
                firstName, lastName, familyName,
                dateOfBirth, phoneNumber, email,
                hireDate, title, position
        );

        teachers.add(teacher);
        saveToDb();

        return Teacher.copy(teacher);
    }

    @Override
    public void create(Teacher prototype) throws IllegalArgumentException, IOException {
        TeacherValidator.validateClass(prototype);

        String email = prototype.getEmail();
        if (email != null && exists(email)) {
            throw new IllegalArgumentException("Teacher with email = " + email + " already exists");
        }

        teachers.add(Teacher.copy(prototype));
        saveToDb();
    }

    @Override
    public Optional<Teacher> get(String email) throws IllegalArgumentException {
        TeacherValidator.validateEmailKey(email);

        for (Teacher teacher : teachers) {
            if (Objects.equals(teacher.getEmail(), email)) {
                return Optional.of(Teacher.copy(teacher));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Teacher> getAll() throws IOException {
        return copyList(teachers);
    }

    @Override
    public void update(String email, Teacher prototype) throws IllegalArgumentException, IOException {
        TeacherValidator.validateEmailKey(email);
        TeacherValidator.validateClass(prototype);

        String oldEmail = email;

        // firstly we find the old stored student
        int index = -1;
        Teacher oldStored = null;

        for (int i = 0; i < teachers.size(); i++) {
            if (Objects.equals(teachers.get(i).getEmail(), email)) {
                index = i;
                oldStored = teachers.get(i);
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Teacher with email=" + email + " not found");
        }

        // then, we validate email change, in order to avoid possible duplicates
        String newEmail = prototype.getEmail();
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("new email must not be null or blank");
        }

        if (!Objects.equals(newEmail, email) && exists(newEmail)) {
            throw new IllegalArgumentException("Teacher with email = " + newEmail + " already exists");
        if (!Objects.equals(newEmail, email)) {
            if (newEmail != null && exists(newEmail)) {
                throw new IllegalArgumentException("Teacher with email=" + newEmail + " already exists");
            }
        }

        // then, we collect references (TEACHER - LESSON)
        Set<Lesson> oldLessons = oldStored.getLessons();


        // theen, we remove connection with old instances from references
        for (Lesson l : oldLessons) {
            l.removeTeacher(oldStored);
        }

        // finally, we are creating a new student instance (just a copy)
        Teacher newStored = Teacher.copy(prototype);

        // AND ----- attaching new instance to the same reference the old one was attached to
        teachers.set(index, newStored);

        for (Lesson l : oldLessons) {
            l.addTeacher(newStored);
        }
        saveToDb();

        if (issueReportService != null && !Objects.equals(oldEmail, newEmail)) {
            issueReportService.updateReporterEmail(oldEmail, newEmail);
        }
    }


    @Override
    public void delete(String email) throws IllegalArgumentException, IOException {
        TeacherValidator.validateEmailKey(email);

        // firstly we find the old stored student
        int index = -1;
        Teacher oldStored = null;

        for (int i = 0; i < teachers.size(); i++) {
            if (Objects.equals(teachers.get(i).getEmail(), email)) {
                index = i;
                oldStored = teachers.get(i);
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Teacher with email=" + email + " not found");
        }

        // then, we collect references (STUDENT - LESSON)
        Set<Lesson> oldLessons = oldStored.getLessons();


        // theen, we remove connection with old instances from references
        for (Lesson l : oldLessons) {
            l.removeTeacher(oldStored);
        }


        teachers.remove(index);

        saveToDb();
    }

    @Override
    public boolean exists(String email) throws IOException {
        if (email == null || email.isBlank()) return false;

        for (Teacher teacher : teachers) {
            if (Objects.equals(teacher.getEmail(), email)) {
                return true;
            }
        }
        return false;
    }

    private List<Teacher> copyList(List<Teacher> source) {
        List<Teacher> result = new ArrayList<>();
        if (source == null) return result;

        for (Teacher t : source) {
            result.add(Teacher.copy(t));
        }
        return result;
    }

    private List<Teacher> loadFromDb() throws IOException {
        if (!service.canLoad(DataSaveKeys.TEACHERS)) {
            return new ArrayList<>();
        }

        Object loaded = service.load(DataSaveKeys.TEACHERS, TEACHER_LIST_TYPE);

        if (loaded instanceof List<?> raw) {
            List<Teacher> result = new ArrayList<>();
            for (Object o : raw) {
                if (o instanceof Teacher teacher) {
                    result.add(teacher);
                }
            }
            return result;
        }

        return new ArrayList<>();
    }

    private void saveToDb() throws IOException {
        service.save(DataSaveKeys.TEACHERS, teachers);
    }
}
