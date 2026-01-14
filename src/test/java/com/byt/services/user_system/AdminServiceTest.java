package com.byt.services.user_system;

import com.byt.data.user_system.Admin;
import com.byt.exception.ValidationException;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest extends CRUDServiceTest<Admin> {

    public AdminServiceTest() {
        super(DataSaveKeys.ADMINS, saveLoadService -> new AdminService(saveLoadService));
    }

    @Override
    protected String getSampleObjectId() {
        return "yumi@gmail.com";
    }

    @Override
    protected Admin getSampleObject() {
        LocalDate dob = LocalDate.of(1997, 1, 1);
        LocalDate hireDate = LocalDate.of(2024, 1, 1);
        LocalDateTime lastLogin = LocalDateTime.of(2025, 1, 1, 12, 0);

        return new Admin(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi@gmail.com",
                hireDate,
                lastLogin,
                null
        );
    }

    @Override
    protected void alterEntity(Admin entity) {
        entity.setFirstName(entity.getFirstName() + "_changed");
    }

    // ------------------- TESTS FOR FIELDS FROM USER -------------------

    @Test
    public void updateAdminWithValidData() throws IOException {
        AdminService service = (AdminService) serviceWithData;

        String oldEmail = getSampleObjectId();
        Optional<Admin> beforeOpt = service.get(oldEmail);
        assertTrue(beforeOpt.isPresent());

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        String newEmail = "yumiii@gmail.com";

        Admin prototype = new Admin(
                "Yumpa",
                "Hnatiukk",
                "Piess",
                dob,
                "3809691046",
                newEmail,
                hireDate,
                lastLogin,
                null
        );

        service.update(oldEmail, prototype);

        Optional<Admin> afterOpt = service.get(newEmail);
        assertTrue(afterOpt.isPresent());
        Admin updated = afterOpt.get();

        assertEquals("Yumpa", updated.getFirstName());
        assertEquals("Hnatiukk", updated.getLastName());
        assertEquals("Piess", updated.getFamilyName());
        assertEquals(dob, updated.getDateOfBirth());
        assertEquals("3809691046", updated.getPhoneNumber());
        assertEquals(newEmail, updated.getEmail());
        assertEquals(hireDate, updated.getHireDate());
        assertEquals(lastLogin, updated.getLastLoginTime());
    }

    @Test
    public void createAdminWithNonLatinFirstName() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Юмі",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+ua@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithNullEmail() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        null,
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithInvalidEmailFormat() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi_at_gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithPhoneContainingLetters() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "48505ab505",
                        "yumi+1@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithTooShortPhone() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(25);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "4850",
                        "yumi+2@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithFutureDateOfBirth() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.plusDays(1);
        LocalDate hireDate = today;
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+3@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithTooYoungDateOfBirth() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(10);
        LocalDate hireDate = today;
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+4@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // ------------------- TESTS FOR ADMIN ASSOCIATION (SELF-REFERENCE) -------------------

    @Test
    public void assignSupervisorCreatesReverseConnection() throws IOException {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        service.create("Super", "A", "Boss", dob, "10203040", "super@gmail.com", hireDate, lastLogin, null);
        service.create("Sub", "A", "Worker", dob, "10203041", "sub@gmail.com", hireDate, lastLogin, null);

        service.assignSupervisor("sub@gmail.com", "super@gmail.com");

        Admin subStored = service.get("sub@gmail.com").orElseThrow();
        assertNotNull(subStored.getSuperAdmin());
        assertEquals("super@gmail.com", subStored.getSuperAdmin().getEmail());

        Admin superStored = service.get("super@gmail.com").orElseThrow();
        assertTrue(superStored.getSupervisedAdmins().stream().anyMatch(a -> a.getEmail().equals("sub@gmail.com")));
    }

    @Test
    public void deleteSupervisorViaDeleteThrowsWhenHasSubordinates() throws IOException {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        service.create("Super", "A", "Boss", dob, "10203040", "super@gmail.com", hireDate, lastLogin, null);
        service.create("Sub", "A", "Worker", dob, "10203041", "sub@gmail.com", hireDate, lastLogin, "super@gmail.com");

        assertThrows(IllegalStateException.class, () -> service.delete("super@gmail.com"));
    }

    @Test
    public void deleteSuperAdminReassignsSubordinates() throws IOException {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        service.create("Old", "Boss", "One", dob, "10203040", "oldsuper@gmail.com", hireDate, lastLogin, null);
        service.create("New", "Boss", "Two", dob, "10203042", "newsuper@gmail.com", hireDate, lastLogin, null);
        service.create("Sub", "A", "Worker", dob, "10203041", "sub@gmail.com", hireDate, lastLogin, "oldsuper@gmail.com");

        service.deleteSuperAdmin("oldsuper@gmail.com", "newsuper@gmail.com");

        assertTrue(service.get("oldsuper@gmail.com").isEmpty());

        Admin sub = service.get("sub@gmail.com").orElseThrow();
        assertNotNull(sub.getSuperAdmin());
        assertEquals("newsuper@gmail.com", sub.getSuperAdmin().getEmail());
    }

    // ------------------- OTHER ADMIN FIELD TESTS -------------------

    @Test
    public void createAdminWithValidData() throws IOException {
        AdminService service = (AdminService) emptyService;

        int before = service.getAll().size();

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        Admin created = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "valid@gmail.com",
                hireDate,
                lastLogin,
                null
        );

        assertNotNull(created);
        assertNotNull(created.getEmail());

        List<Admin> after = service.getAll();
        assertEquals(before + 1, after.size());
    }

    @Test
    public void createAdminWithNullHireDate() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+hireNull@gmail.com",
                        null,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithFutureHireDate() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.plusDays(1);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+hireFuture@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminTooYoungAtHireDate() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(17);
        LocalDate hireDate = today;
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+tooYoungHire@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithHireDateBeforeBirth() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = dob.minusDays(1);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+hireBeforeBirth@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithFutureLastLogin() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.minusYears(5);
        LocalDateTime lastLogin = LocalDateTime.now().plusMinutes(5);

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+futureLogin@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createAdminWithLastLoginBeforeHireDate() {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.minusYears(1);
        LocalDateTime lastLogin = hireDate.minusDays(1).atStartOfDay();

        assertThrows(
                ValidationException.class,
                () -> service.create(
                        "Yumi",
                        "Hnatiuk",
                        "Pies",
                        dob,
                        "10203040",
                        "yumi+loginBeforeHire@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    @Test
    public void createWithNullPrototype() {
        AdminService service = (AdminService) emptyService;
        assertThrows(ValidationException.class, () -> service.create((Admin) null));
    }

    @Test
    public void createWithPrototypeDoesNotLeakReferences() throws IOException {
        AdminService service = (AdminService) emptyService;

        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(30);
        LocalDate hireDate = today.minusYears(5);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        Admin prototype = new Admin(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "prototype@gmail.com",
                hireDate,
                lastLogin,
                null
        );

        service.create(prototype);

        prototype.setFirstName("CHANGED");

        List<Admin> all = service.getAll();
        assertEquals(1, all.size());

        Admin stored = all.getFirst();
        assertNotEquals("CHANGED", stored.getFirstName());
        assertEquals("Yumi", stored.getFirstName());
    }
}
