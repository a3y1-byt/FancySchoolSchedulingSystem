package com.byt.persistence.util;

public enum DataSaveKeys {
        // Users
        STUDENTS("Users/Students"),
        FREE_LISTENERS("Users/FreeListeners"),
        TEACHERS("Users/Teachers"),
        ADMINS("Users/Admins"),

        // Facilities
        BUILDINGS("Facilities/Buildings"),
        CLASSROOMS("Facilities/ClassRooms"),
        SPECIALIZATIONS("Facilities/Specializations"),

        // Scheduling
        STUDY_PROGRAMS("Scheduling/StudyPrograms"),
        SEMESTERS("Scheduling/Semesters"),
        SUBJECTS("Scheduling/Subjects"),
        LESSONS("Scheduling/Lessons"),
        GROUPS("Scheduling/Groups"),

        // Reports
        ISSUE_REPORTS("Reports/IssueReports");

    public final String repositoryKey;

    DataSaveKeys(String repositoryKey) {
        this.repositoryKey = repositoryKey;
    }

    @Override
    public String toString() {
        return repositoryKey;
    }
}
