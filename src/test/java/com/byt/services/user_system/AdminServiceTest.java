package com.byt.services.user_system;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import com.byt.data.user_system.Admin;
import com.byt.exception.ValidationException;
import com.byt.exception.ExceptionCode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTest extends CRUDServiceTest<Admin> {

    private static final String SAMPLE_EMAIL = "yumi@gmail.com";

    public AdminServiceTest() {
        super(DataSaveKeys.ADMINS, saveLoadService -> new AdminService(saveLoadService));
    }

    @Override
    protected String getSampleObjectId() {
        return SAMPLE_EMAIL;
    }

    @Override
    protected Admin getSampleObject() {
        LocalDate dob = LocalDate.of(1997, 1, 1);
        LocalDate hireDate = LocalDate.of(2024, 1, 1);
        LocalDateTime lastLogin = LocalDateTime.of(2025, 1, 1, 12, 0);

        Admin admin = new Admin(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                SAMPLE_EMAIL,
                hireDate,
                lastLogin,
                null
        );
        return admin;
    }


    @Override
    protected void alterEntity(Admin entity) {
        entity.setFirstName(entity.getFirstName() + "_changed");
    }

    // ------------------- TESTS FOR FIELDS FROM USER -------------------

    // update test
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

        assertTrue(service.get(oldEmail).isEmpty());
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

    // firstName contains ukrainian letters
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // email = null
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

    // email wrong format
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

    // phone contains letters
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // phone too short
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // date of birth in future
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // too young
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // ------------------- TESTS FOR ADMIN FIELDS -------------------

    // make Super Admin
    @Test
    public void makeSuperAdmin() throws IOException {
        AdminService service = (AdminService) emptyService;


        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        Admin superA = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi1@gmail.com",
                hireDate,
                lastLogin,
                null
        );

        Admin subA = service.create(
                "AYumi",
                "AHnatiuk",
                "APies",
                dob,
                "10203040",
                "yumi2@gmail.com",
                hireDate,
                lastLogin,
                superA.getEmail()
        );

        Optional<Admin> before = service.get(subA.getEmail());
        assertTrue(before.isPresent());
        assertEquals(superA.getEmail(), before.get().getSuperadminId());

        service.makeSuperAdmin(subA.getEmail());

        Optional<Admin> after = service.get(subA.getEmail());
        assertTrue(after.isPresent());
        assertNull(after.get().getSuperadminId());
    }

    // delete superadmin via simple delete
    @Test
    public void deleteSuperAdminViaDelete() throws IOException {
        AdminService service = (AdminService) emptyService;


        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        Admin superA = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi3@gmail.com",
                hireDate,
                lastLogin,
                null
        );

        Admin subA = service.create(
                "AYumi",
                "AHnatiuk",
                "APies",
                dob,
                "10203040",
                "yumi4@gmail.com",
                hireDate,
                lastLogin,
                superA.getEmail()
        );

        assertThrows(
                IllegalStateException.class,
                () -> service.delete(superA.getEmail())
        );
    }

    //delete superadmin via deleteSuperAdmin
    @Test
    public void deleteSuperAdmin() throws IOException {
        AdminService service = (AdminService) emptyService;


        LocalDate today = LocalDate.now();
        LocalDate dob = today.minusYears(21);
        LocalDate hireDate = today.minusYears(2);
        LocalDateTime lastLogin = LocalDateTime.now().minusDays(1);

        Admin superA = service.create(
                "Yumi",
                "Hnatiuk",
                "Pies",
                dob,
                "10203040",
                "yumi5@gmail.com",
                hireDate,
                lastLogin,
                null
        );

        Admin subA = service.create(
                "AYumi",
                "AHnatiuk",
                "APies",
                dob,
                "10203040",
                "yumi6@gmail.com",
                hireDate,
                lastLogin,
                superA.getEmail()
        );

        Admin newSuperA = service.create(
                "AAYumi",
                "AAHnatiuk",
                "AAPies",
                dob,
                "10203040",
                "yumi7@gmail.com",
                hireDate,
                lastLogin,
                null
        );

        service.deleteSuperAdmin(superA.getEmail(), newSuperA.getEmail());

        assertTrue(service.get(superA.getEmail()).isEmpty());

        Optional<Admin> newSuperAfter = service.get(newSuperA.getEmail());
        assertTrue(newSuperAfter.isPresent());
        assertNull(newSuperAfter.get().getSuperadminId());

        Optional<Admin> subAfter = service.get(subA.getEmail());
        assertTrue(subAfter.isPresent());
        assertEquals(newSuperA.getEmail(), subAfter.get().getSuperadminId());
    }

    // valid data
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
                "yumi@gmail.com",
                hireDate,
                lastLogin,
                null
        );

        assertNotNull(created);
        assertNotNull(created.getEmail());

        List<Admin> after = service.getAll();
        assertEquals(before + 1, after.size());
    }

    // hire date = null
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
                        "yumi@gmail.com",
                        null,
                        lastLogin,
                        null
                )
        );
    }

    // hire date is in future
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // person is younger than 18 at hire date
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // hire date is before person's birth
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // last login date is in future
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // last login time is before hire date
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
                        "yumi@gmail.com",
                        hireDate,
                        lastLogin,
                        null
                )
        );
    }

    // creating prototype with null
    @Test
    public void createWithNullPrototype() {
        AdminService service = (AdminService) emptyService;
        assertThrows(ValidationException.class, () -> service.create((Admin) null));
    }

    // create prototype and test for leackage
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
                "yumi@gmail.com",
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
