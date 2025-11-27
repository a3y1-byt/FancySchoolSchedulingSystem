package com.byt.persistence;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonDataSerializerTest {
    private final JsonDataSerializer serializer = new JsonDataSerializer();

    private final Student testStudent = new Student(1, "Jake");
    private final Student[] testStudentArray = new Student[] {
            new Student(1, "Jake"),
            new Student(2, "Michael"),
            new Student(3, "Jean")
    };
    private final Classroom testClassroom = new Classroom(1, testStudentArray);

    @Test
    public void testSingleObjectSerialization() {
        Student student = testStudent;
        String expected = "{\"id\":1,\"name\":\"Jake\"}";

        assertEquals(expected, serializer.serialize(student));
    }

    @Test
    public void testSingleObjectDeserialization() {
        String serializedStudent = "{\"id\":1,\"name\":\"Jake\"}";
        Student expected = testStudent;

        assertEquals(expected, serializer.deserialize(serializedStudent, Student.class));
    }

    @Test
    public void testSingleObjectWithNestedArraySerialization() {
        Classroom classroom = testClassroom;
        String expected = "{\"number\":1,\"students\":[{\"id\":1,\"name\":\"Jake\"},{\"id\":2,\"name\":\"Michael\"},{\"id\":3,\"name\":\"Jean\"}]}";

        assertEquals(expected, serializer.serialize(testClassroom));
    }

    @Test
    public void testSingleObjectWithNestedArrayDeserialization() {
        String serializedClassroom = "{\"number\":1,\"students\":[{\"id\":1,\"name\":\"Jake\"},{\"id\":2,\"name\":\"Michael\"},{\"id\":3,\"name\":\"Jean\"}]}";
        Classroom expected = testClassroom;

        assertEquals(expected, serializer.deserialize(serializedClassroom, Classroom.class));
    }

    @Test
    public void testArraySerialization() {
        Student[] studentArray = testStudentArray;
        String expected = "[{\"id\":1,\"name\":\"Jake\"},{\"id\":2,\"name\":\"Michael\"},{\"id\":3,\"name\":\"Jean\"}]";

        assertEquals(expected, serializer.serialize(studentArray));
    }

    @Test
    public void testArrayDeserialization() {
        String serializedStudentArray = "[{\"id\":1,\"name\":\"Jake\"},{\"id\":2,\"name\":\"Michael\"},{\"id\":3,\"name\":\"Jean\"}]";
        Student[] expected = testStudentArray;

        assertArrayEquals(expected, (Student[])serializer.deserialize(serializedStudentArray, Student[].class));
    }

    @Nested
    public class AdaptersTest {
        private final LocalDateTime testDateTime = LocalDateTime.of(2002, 4, 10, 12, 32, 10, 10012);
        private final LocalDate testDate = LocalDate.of(1987, 3, 12);
        private final LocalTime testTime = LocalTime.of(20, 13, 1, 102);

        @Test
        public void testNoLossesHappenOnTwoSidedLocalDateTimeSerialization() {
            LocalDateTime dateTime = testDateTime;

            String serializedDateTime = serializer.serialize(dateTime);
            LocalDateTime deserializedDateTime = (LocalDateTime) serializer.deserialize(serializedDateTime, LocalDateTime.class);

            assertEquals(dateTime, deserializedDateTime);
        }

        @Test
        public void testNoLossesHappenOnTwoSidedLocalDateSerialization() {
            LocalDate date = testDate;

            String serializedDate = serializer.serialize(date);
            LocalDate deserializedDate = (LocalDate) serializer.deserialize(serializedDate, LocalDate.class);

            assertEquals(date, deserializedDate);
        }

        @Test
        public void testNoLossesHappenOnTwoSidedLocalTimeSerialization() {
            LocalTime time = testTime;

            String serializedTime = serializer.serialize(time);
            LocalTime deserializedTime = (LocalTime) serializer.deserialize(serializedTime, LocalTime.class);

            assertEquals(time, deserializedTime);
        }
    }

    private record Classroom(int number, Student[] students) {
        @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;

                if (!(o instanceof Classroom classroom))
                    return false;

                return number == classroom.number && Objects.deepEquals(students, classroom.students);
            }

            @Override
            public int hashCode() {
                return Objects.hash(number, Arrays.hashCode(students));
            }
        }

    private record Student(int id, String name) {

        @Override
            public boolean equals(Object o) {
                if (this == o)
                    return true;

                if (!(o instanceof Student student))
                    return false;

                return id == student.id && name.equals(student.name);
            }

    }
}
