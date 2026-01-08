package com.byt.data.user_system;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AdminTest {

    private Admin sampleAdmin1 = new Admin("Admin", "Adminski", null, LocalDate.now().minusYears(20), "329432432", "a@a.com", LocalDate.now().minusDays(1), LocalDateTime.now(), null);
    private Admin sampleAdmin2 = new Admin("Admin", "Adminski", null, LocalDate.now().minusYears(20), "329432432", "a@a.com", LocalDate.now().minusDays(1), LocalDateTime.now(), null);

    @Test
    public void testAddSuperAdminAddsSupervisedAdminOnTheOtherSide() {
        sampleAdmin1.addSuperAdmin(sampleAdmin2);

        assertEquals(sampleAdmin2, sampleAdmin1.getSuperAdmin());
        assertTrue(sampleAdmin2.getSupervisedAdmins().contains(sampleAdmin1));
    }

    @Test
    public void testRemoveSuperAdminRemovesSupervisedAdminOnTheOtherSide() {
        Admin sampleSupervisedAdmin = new Admin("Admin", "Adminski", null, LocalDate.now().minusYears(20), "329432432", "a@a.com", LocalDate.now().minusDays(1), LocalDateTime.now(), sampleAdmin1);

        sampleSupervisedAdmin.removeSuperAdmin(sampleAdmin1);

        assertNull(sampleSupervisedAdmin.getSuperAdmin());
        assertFalse(sampleAdmin1.getSupervisedAdmins().contains(sampleSupervisedAdmin));
    }
}
