package com.byt.reporting;

import com.byt.persistence.util.DataSaveKeys;
import com.byt.reporting.IssueReport;
import com.byt.services.CRUDServiceTest;

import java.time.LocalDateTime;

public class IssueReportServiceCRUDTest extends CRUDServiceTest<IssueReport> {

    public IssueReportServiceCRUDTest() {
        super(DataSaveKeys.ISSUE_REPORTS, IssueReportService::new);
    }

    @Override
    protected IssueReport getSampleObject() {
        return new IssueReport(
                "IR-1",
                "Sample title",
                "Sample description",
                LocalDateTime.of(2025, 1, 2, 10, 0)
        );
    }

    @Override
    protected String getSampleObjectId() {
        return "IR-1";
    }

    @Override
    protected void alterEntity(IssueReport entity) {
        entity.setTitle(entity.getTitle() + " edited");
    }
}
