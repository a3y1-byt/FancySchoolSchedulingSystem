package com.byt.services.reporting;

import com.byt.data.reporting.IssueReport;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IssueReportServiceCRUDTest extends CRUDServiceTest<IssueReport> {

    public IssueReportServiceCRUDTest() {
        super(DataSaveKeys.ISSUE_REPORTS, IssueReportService::new);
    }

    @Override
    protected IssueReport getSampleObject() {
        return new IssueReport(
                "sample@mail.com",
                "admin@mail.com",
                "Sample title",
                "Sample description",
                LocalDateTime.of(2025, 1, 2, 10, 0)
        );
    }

    @Override
    protected String getSampleObjectId() {
        IssueReport sample = getSampleObject();
        return IssueReportService.compositeId(
                sample.getEmail(),
                sample.getTitle()
        );
    }

    @Override
    protected void alterEntity(IssueReport entity) {
        // do not change email or title because they are part of the key
        entity.setDescription(entity.getDescription() + " edited");
    }

    @Test
    public void createDuplicateIssueReportThrows() throws IOException {
        IssueReportService service = (IssueReportService) emptyService;

        IssueReport r1 = getSampleObject();

        service.create(r1);

        IssueReport r2 = new IssueReport(
                r1.getEmail(),
                r1.getAdminEmail(),
                r1.getTitle(),
                "Another description",
                LocalDateTime.now()
        );

        assertThrows(IllegalArgumentException.class, () -> service.create(r2));
    }

    @Test
    public void getAllByEmailReturnsOnlyMatchingReports() throws IOException {
        IssueReportService service = (IssueReportService) emptyService;

        service.create(new IssueReport(
                "sample@mail.com",
                "admin@mail.com",
                "Title A",
                "Desc A",
                LocalDateTime.now()
        ));

        service.create(new IssueReport(
                "sample@mail.com",
                "admin@mail.com",
                "Title B",
                "Desc B",
                LocalDateTime.now()
        ));

        service.create(new IssueReport(
                "other@mail.com",
                "admin@mail.com",
                "Title A",
                "Desc C",
                LocalDateTime.now()
        ));

        List<IssueReport> bySample = service.getAllByEmail("sample@mail.com");
        assertEquals(2, bySample.size());
        assertTrue(bySample.stream().allMatch(r -> "sample@mail.com".equalsIgnoreCase(r.getEmail())));

        List<IssueReport> byOther = service.getAllByEmail("other@mail.com");
        assertEquals(1, byOther.size());
        assertEquals("other@mail.com", byOther.getFirst().getEmail());
    }

    @Test
    public void updateReporterEmailMovesReportsToNewEmail() throws IOException {
        IssueReportService service = (IssueReportService) emptyService;

        IssueReport report = new IssueReport(
                "old@mail.com",
                "oldAdmin@mail.com",
                "Same title",
                "Desc",
                LocalDateTime.now()
        );

        service.create(report);

        String oldId = IssueReportService.compositeId("old@mail.com", "Same title");
        assertTrue(service.exists(oldId));

        service.updateReporterEmail("old@mail.com", "new@mail.com");

        List<IssueReport> oldList = service.getAllByEmail("old@mail.com");
        assertEquals(0, oldList.size());

        List<IssueReport> newList = service.getAllByEmail("new@mail.com");
        assertEquals(1, newList.size());

        String newId = IssueReportService.compositeId("new@mail.com", "Same title");
        assertFalse(service.exists(oldId));
        assertTrue(service.exists(newId));
    }

    @Test
    public void deleteRemovesAssociationAndReverseNavigation() throws IOException {
        IssueReportService service = (IssueReportService) emptyService;

        service.create(new IssueReport(
                "del@mail.com",
                "deAdmin@mail.com",
                "To delete",
                "Desc",
                LocalDateTime.now()
        ));

        assertEquals(1, service.getAllByEmail("del@mail.com").size());

        String id = IssueReportService.compositeId("del@mail.com", "To delete");
        service.delete(id);

        assertEquals(0, service.getAllByEmail("del@mail.com").size());
        assertFalse(service.exists(id));
    }
    @Test
    public void getAllByAdminEmailReturnsOnlyMatchingReports() throws IOException {
        IssueReportService service = (IssueReportService) emptyService;

        service.create(new IssueReport(
                "user1@mail.com",
                "admin1@mail.com",
                "T1",
                "D1",
                LocalDateTime.now()
        ));
        service.create(new IssueReport(
                "user2@mail.com",
                "admin1@mail.com",
                "T2",
                "D2",
                LocalDateTime.now()
        ));
        service.create(new IssueReport(
                "user3@mail.com",
                "admin2@mail.com",
                "T3",
                "D3",
                LocalDateTime.now()
        ));

        List<IssueReport> a1 = service.getAllByAdminEmail("admin1@mail.com");
        assertEquals(2, a1.size());
        assertTrue(a1.stream().allMatch(r -> "admin1@mail.com".equalsIgnoreCase(r.getAdminEmail())));

        List<IssueReport> a2 = service.getAllByAdminEmail("admin2@mail.com");
        assertEquals(1, a2.size());
    }

    @Test
    public void assignAdminChangesReverseNavigation() throws IOException {
        IssueReportService service = (IssueReportService) emptyService;

        service.create(new IssueReport(
                "user@mail.com",
                "admin1@mail.com",
                "Same title",
                "Desc",
                LocalDateTime.now()
        ));

        String id = IssueReportService.compositeId("user@mail.com", "Same title");

        assertEquals(1, service.getAllByAdminEmail("admin1@mail.com").size());
        assertEquals(0, service.getAllByAdminEmail("admin2@mail.com").size());

        service.assignAdmin(id, "admin2@mail.com");

        assertEquals(0, service.getAllByAdminEmail("admin1@mail.com").size());
        assertEquals(1, service.getAllByAdminEmail("admin2@mail.com").size());
    }

    @Test
    public void updateAssignedAdminEmailMovesReportsToNewAdminEmail() throws IOException {
        IssueReportService service = (IssueReportService) emptyService;

        service.create(new IssueReport(
                "user@mail.com",
                "oldAdmin@mail.com",
                "T",
                "D",
                LocalDateTime.now()
        ));

        assertEquals(1, service.getAllByAdminEmail("oldAdmin@mail.com").size());

        service.updateAssignedAdminEmail("oldAdmin@mail.com", "newAdmin@mail.com");

        assertEquals(0, service.getAllByAdminEmail("oldAdmin@mail.com").size());
        assertEquals(1, service.getAllByAdminEmail("newAdmin@mail.com").size());
    }


}
