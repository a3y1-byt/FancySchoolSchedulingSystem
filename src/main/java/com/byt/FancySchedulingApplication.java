package com.byt;

import com.byt.data.scheduling.*;
import com.byt.persistence.*;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.data.reporting.IssueReport;
import com.byt.services.reporting.IssueReportService;
import com.byt.data.user_system.Admin;
import com.byt.data.user_system.FreeListener;
import com.byt.data.user_system.Student;
import com.byt.data.user_system.Teacher;
import com.byt.enums.user_system.StudyLanguage;
import com.byt.enums.user_system.StudyStatus;
import com.byt.services.scheduling.GroupService;
import com.byt.services.user_system.AdminService;
import com.byt.services.user_system.StudentService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FancySchedulingApplication {
    public static void main(String[] args) {
        try {
            // Obviously, that's an impromptu database, not a real SQL-based solution or whatever
            SaveLoadService database = generatePersistenceService();
            demonstrateServices(database);
        } catch (IOException e) {
            throw new RuntimeException("Oops, the services were so cool that they fell. It's still 3/3, right?");
        }
    }

    private static SaveLoadService generatePersistenceService() throws IOException {
        DataSerializer serializer = new JsonDataSerializer();
        DataRepository repository = new InMemoryDataRepository();

        SaveLoadService database = new SaveLoadService(serializer, repository);

        // This is a small demo of all the entities we store in our "database" atm. Enjoy!
        database.save(DataSaveKeys.STUDENTS, new Student[0]);
        database.save(DataSaveKeys.FREE_LISTENERS, new FreeListener[0]);
        database.save(DataSaveKeys.TEACHERS, new Teacher[0]);
        database.save(DataSaveKeys.ADMINS, new Admin[0]);

        database.save(DataSaveKeys.BUILDINGS, new Building[0]);
        database.save(DataSaveKeys.CLASSROOMS, new ClassRoom[0]);
        database.save(DataSaveKeys.SPECIALIZATIONS, new Specialization[0]);

        database.save(DataSaveKeys.STUDY_PROGRAMS, new StudyProgram[0]);
        database.save(DataSaveKeys.SEMESTERS, new Semester[0]);
        database.save(DataSaveKeys.SUBJECTS, new Subject[0]);
        database.save(DataSaveKeys.LESSONS, new Lesson[0]);
        database.save(DataSaveKeys.GROUPS, new Group[0]);

        database.save(DataSaveKeys.ISSUE_REPORTS, new IssueReport[0]);

        return database;
    }

    private static void demonstrateServices(SaveLoadService database) throws IOException {
        StudentService studentService = new StudentService(database, new ArrayList<>());
        AdminService adminService = new AdminService(database, new ArrayList<>());
        GroupService groupService = new GroupService(database);

        studentService.initialize();
        adminService.initialize();
        groupService.initialize();

        // Objects can be built through the constructors...
        Student studentPrototype = new Student(
                "Jake", "Doe", null, // No family name
                LocalDate.now().minusYears(20),
                "4808857322",
                "jake.doe@ilovebyt.pl",
                List.of(StudyLanguage.ENGLISH),
                StudyStatus.ACTIVE);

        // ...or with builders!
        Group bytGroupPrototype = Group.builder()
                .name("BYT-2025")
                .language(StudyLanguage.ENGLISH)
                .yearOfStudy(2025)
                .maxCapacity(15)
                .students(studentService.getAll()) // BYT is too good, so we'll make ALL the students study it
                .build();

        // Student will be used as a prototype. Under the hood, a separate instance is constructed.
        // Take that, evil escaping references!
        studentService.create(studentPrototype);

        // Same goes here
        groupService.create(bytGroupPrototype);

        // Now, let's try to update the group we just created, because its name doesn't fully represent how cool this subject is
        String oldGroupName = bytGroupPrototype.getName();
        bytGroupPrototype.setName("SUPER-BYT-2025");
        groupService.update(oldGroupName, bytGroupPrototype); // Still, no escaping references

        System.out.println("Group name: " + groupService.get(bytGroupPrototype.getName()).orElseThrow().getName()); // SUPER-BYT-2025

        // OMG, this project is so amazing that it might be illegal!
        // Let's report this issue to the developers (us) so that they make the application worse.
        // Thankfully, we have just the right service for that.
        IssueReportService reportService = new IssueReportService(database);
        reportService.initialize();

        IssueReport superRealReport =
                new IssueReport(
                        "admin@school.com",                 // email instead of "1"
                        "THIS PROJECT IS TOO GOOD!",              // title
                        "Plz fix that",                           // description
                        LocalDateTime.now()                      // createdAt
                );

        reportService.create(superRealReport);
    }
}
