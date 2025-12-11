package com.byt.services.reporting;

import com.byt.data.reporting.IssueReport;
import com.byt.persistence.util.DataSaveKeys;
import com.byt.services.CRUDServiceTest;

import java.time.LocalDateTime;

public class IssueReportServiceCRUDTest extends CRUDServiceTest<IssueReport> {

    public IssueReportServiceCRUDTest() {
        super(DataSaveKeys.ISSUE_REPORTS, IssueReportService::new);
    }

    @Override
    protected IssueReport getSampleObject() {
        return new IssueReport(
                "sample@mail.com",
                "Sample title",
                "Sample description",
                LocalDateTime.of(2025, 1, 2, 10, 0)
//                null
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
}
