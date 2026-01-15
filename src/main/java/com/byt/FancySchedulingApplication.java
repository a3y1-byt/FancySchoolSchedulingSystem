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
import com.byt.services.scheduling.*;
import com.byt.services.user_system.AdminService;
import com.byt.services.user_system.FreeListenerService;
import com.byt.services.user_system.StudentService;
import com.byt.services.user_system.TeacherService;
import com.byt.services.reporting.IssueReportService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FancySchedulingApplication {
    public static void main(String[] args) {
        try {
            // Obviously, that's an impromptu database, not a real SQL-based solution or whatever
            SaveLoadService database = generatePersistenceService();
            demonstrateApp(database);
        } catch (IOException e) {
            throw new RuntimeException("Oops, the services were so cool that they fell. It's still 3/3, right?");
        }
    }

    private static SaveLoadService generatePersistenceService() throws IOException {
        DataSerializer serializer = new JsonDataSerializer();
        DataRepository repository = new InMemoryDataRepository();
        SaveLoadService database = new SaveLoadService(serializer, repository);

        database.save(DataSaveKeys.STUDENTS, new ArrayList<Student>());
        database.save(DataSaveKeys.FREE_LISTENERS, new ArrayList<FreeListener>());
        database.save(DataSaveKeys.TEACHERS, new ArrayList<Teacher>());
        database.save(DataSaveKeys.ADMINS, new ArrayList<Admin>());

        database.save(DataSaveKeys.BUILDINGS, new ArrayList<Building>());
        database.save(DataSaveKeys.CLASSROOMS, new ArrayList<ClassRoom>());
        database.save(DataSaveKeys.SPECIALIZATIONS, new ArrayList<Specialization>());
        database.save(DataSaveKeys.STUDY_PROGRAMS, new ArrayList<StudyProgram>());
        database.save(DataSaveKeys.SEMESTERS, new ArrayList<Semester>());
        database.save(DataSaveKeys.SUBJECTS, new ArrayList<Subject>());
        database.save(DataSaveKeys.LESSONS, new ArrayList<Lesson>());
        database.save(DataSaveKeys.GROUPS, new ArrayList<Group>());

        database.save(DataSaveKeys.ISSUE_REPORTS, new ArrayList<IssueReport>());

        return database;
    }

    private static void demonstrateApp(SaveLoadService database) throws IOException {
        // User
        AdminService adminService = new AdminService(database);
        TeacherService teacherService = new TeacherService(database);
        StudentService studentService = new StudentService(database);
        FreeListenerService freeListenerService = new FreeListenerService(database);

        adminService.initialize();
        teacherService.initialize();
        studentService.initialize();
        freeListenerService.initialize();

        // Scheduling
        GroupService groupService = new GroupService(database);
        LessonService lessonService = new LessonService(database);
        ClassRoomService classRoomService = new ClassRoomService(database);
        BuildingService buildingService = new BuildingService(database);
        SubjectService subjectService = new SubjectService(database);
        SemesterService semesterService = new SemesterService(database);
        StudyProgramService studyProgramService = new StudyProgramService(database);

        groupService.initialize();
        lessonService.initialize();
        classRoomService.initialize();
        buildingService.initialize();
        subjectService.initialize();
        semesterService.initialize();
        studyProgramService.initialize();

        // Service stuff
        IssueReportService issueReportService = new IssueReportService(database);

        issueReportService.initialize();

        // -- GO! --
        System.out.println("DEMO STARTS HERE!");

        Student studentProto = new Student(
                "Alex", "Alexov", null,
                LocalDate.now().minusYears(20),
                "3706421244", "alex.alexov@alexia.com",
                new HashSet<>() {{ add(StudyLanguage.ENGLISH); }},
                StudyStatus.ACTIVE
        );

        studentService.create(studentProto);

        System.out.println("========================");
        System.out.println("== REFLEX ASSOCIATION ==");

        Admin superAdminProto = new Admin(
                "Admin", "Adminski", null,
                LocalDate.now().minusYears(22),
                "4898542321", "superadmin@WeLoveBYT.com",
                LocalDate.now().minusMonths(1),
                LocalDateTime.now(),
                null
        );

        Admin normalAdminProto = new Admin(
                "Admin", "Normalny", null,
                LocalDate.now().minusYears(20),
                "712344244", "turboflex.admin@WeLoveBYT.com",
                LocalDate.now().minusDays(10),
                LocalDateTime.now().minusHours(1),
                superAdminProto
        );

        adminService.create(superAdminProto);
        adminService.create(normalAdminProto);

        System.out.println("Two admins were initialized:");
        System.out.println("Super Admin: " + adminService.get(superAdminProto.getEmail()).get().getEmail());
        System.out.println("Normal Admin: " + adminService.get(normalAdminProto.getEmail()).get().getEmail());
        System.out.println();
        System.out.println("Current supervisor of normal admin: " + adminService.get(normalAdminProto.getEmail()).get().getEmail());
        System.out.println("Removing supervised admin from the superadmin...");

        Admin supervisingAdmin = adminService.get(superAdminProto.getEmail()).get();
        supervisingAdmin.removeSupervisedAdmin(adminService.get(normalAdminProto.getEmail()).get());
        adminService.update(supervisingAdmin.getEmail(), supervisingAdmin);

        System.out.println("Current supervisor of normal admin: " + adminService.get(normalAdminProto.getEmail()).get().getEmail());

        System.out.println("========================");

        Building buildingProto = Building.builder()
                .name("BYlTing")
                .description("It's a pun with 'building' and 'BYT', got it? I'm SO funny")
                .address("ul. BYT, b. 34/3")
                .build();

        ClassRoom classRoomProto = ClassRoom.builder()
                .name("Abdulla Mohamed's Personal Throne Hall")
                .building(buildingProto)
                .floor(1)
                .capacity(35)
                .build();

        buildingProto.addClassRoom(classRoomProto);

        buildingService.create(buildingProto);
        classRoomService.create(classRoomProto);

        System.out.printf("A building '%s' was initialized with classroom '%s'\n",
                buildingService.get(buildingProto.getName()).get().getName(),
                classRoomService.get(classRoomProto.getName()).get().getName()
        );

        Lesson lessonProto = Lesson.builder()
                .name("BYT Lecture with non-mandatory attendance that EVERYBODY visits")
                .language(StudyLanguage.ENGLISH)
                .build();

        Group groupProto = Group.builder()
                .name("BYT-2025")
                .language(StudyLanguage.ENGLISH)
                .maxCapacity(35)
                .yearOfStudy(2025)
                .notes(new ArrayList<String>() {{ add("The best subject ever OMG"); }})
                .lessons(new HashSet<Lesson>())
                .freeListeners(new HashSet<FreeListener>())
                .build();

        System.out.println("=======================");
        System.out.println("===== COMPOSITION =====");

        System.out.println("Do you remember the building we just created?");
        System.out.println("I wonder what will happen with the classroom inside it if we'll delete the building");
        System.out.println("Current TOTAL amount of classrooms: " + classRoomService.getAll().size());
        System.out.println("Deleting the '" + buildingProto.getName() + "' building...");
        buildingService.delete(buildingProto.getName());
        System.out.println("Current TOTAL amount of classrooms: " + classRoomService.getAll().size());
    }

    @Deprecated
    private static void demonstrateServices(SaveLoadService database) throws IOException {
        IssueReportService reportService = new IssueReportService(database);
        reportService.initialize();

        StudentService studentService = new StudentService(database, null, reportService);
        TeacherService teacherService = new TeacherService(database, null);
        FreeListenerService freeListenerService = new FreeListenerService(database, null);
        AdminService adminService = new AdminService(database);
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
                Set.of(StudyLanguage.ENGLISH),
                StudyStatus.ACTIVE);

        // ...or with builders!
        Group bytGroupPrototype = Group.builder()
                .name("BYT-2025")
                .language(StudyLanguage.ENGLISH)
                .yearOfStudy(2025)
                .maxCapacity(15)
                .students((Set<Student>) studentService.getAll()) // BYT is too good, so we'll make ALL the students study it
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
