package cityissue.tracker.murestrack.service;

import cityissue.tracker.murestrack.persistence.model.Attachment;
import cityissue.tracker.murestrack.persistence.model.Category;
import cityissue.tracker.murestrack.persistence.model.Report;
import cityissue.tracker.murestrack.persistence.model.ReportCounts;

import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Report> getAll();


    public Attachment getReportAttachment(Long id);
    boolean addReport(Report report);
    Report editReport(Long id, Report edited);
    Report closeReport(Long id);
    Report getReportById(Long id);

    String exportReportToPdf(Long id);

    ReportCounts getReportCounts();

}
