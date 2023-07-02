package cityissue.tracker.murestrack.service.impl;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import cityissue.tracker.murestrack.persistence.model.Report;
import cityissue.tracker.murestrack.persistence.model.Status;
import com.itextpdf.text.Document;
import cityissue.tracker.murestrack.persistence.model.*;
import cityissue.tracker.murestrack.persistence.model.validator.EntityValidator;
import cityissue.tracker.murestrack.persistence.repository.ReportRepository;
import cityissue.tracker.murestrack.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ReportServiceImpl implements ReportService {

    private ReportRepository reportRepository;
    private EntityValidator<Report> reportValidator;

    @Autowired
    public ReportServiceImpl(ReportRepository reportRepository, EntityValidator<Report> reportValidator) {
        this.reportRepository = reportRepository;
        this.reportValidator = reportValidator;
    }


    @Override
    public List<Report> getAll() {
        return reportRepository.findAll();
    }

    public Attachment getReportAttachment(Long id){
        Optional<Report> report =reportRepository.findById(id);
        Attachment attachment = new Attachment();
        if(report.isPresent()){
            attachment = report.get().getAttachment();
        }
        return attachment;
    }
    @Override
    public Report getReportById(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    private PdfPCell createTableCell(String content, BaseColor backgroundColor, Font font){
        PdfPCell cell1 = new PdfPCell(new Paragraph(content, font));
        cell1.setBorderColor(BaseColor.BLACK);
        cell1.setBackgroundColor(backgroundColor);
        cell1.setPaddingLeft(10);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell1.setUseBorderPadding(true);
        cell1.setPadding(10f);
        return cell1;
    }

    @Override
    public String exportReportToPdf(Long id) {
        Optional<Report> maybeReport = reportRepository.findById(id);
        if(maybeReport.isEmpty()){
            return null;
        }
        Report report = maybeReport.get();
        String fileName = report.getTitle() + "-" + report.getId() + ".pdf";
        File file = new File("./src/main/resources/generated/" + fileName);
        try
        {
            file.createNewFile();
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file, false));
            document.open();

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            //Set Column widths
            float[] columnWidths = {1.5f, 1.5f};
            table.setWidths(columnWidths);
            Font propertyFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,18, Font.BOLD, BaseColor.BLACK);
            Font valueFont = FontFactory.getFont(FontFactory.TIMES_ROMAN,18, BaseColor.BLACK);
            table.addCell(createTableCell( "Title", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell( report.getTitle(), BaseColor.WHITE, valueFont));
            table.addCell(createTableCell("Description", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell(report.getDescription(), BaseColor.WHITE, valueFont));
            table.addCell(createTableCell( "Version", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell( report.getCategory().toString(), BaseColor.WHITE, valueFont));
            table.addCell(createTableCell("Fixed-in Version", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell(report.getLocation(), BaseColor.WHITE, valueFont));
            table.addCell(createTableCell( "Target date", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell( report.getTargetDate().toString(), BaseColor.WHITE, valueFont));
            table.addCell(createTableCell("Severity", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell(report.getSeverity().toString(), BaseColor.WHITE, valueFont));
            table.addCell(createTableCell( "Status", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell( report.getStatus().toString(), BaseColor.WHITE, valueFont));
            table.addCell(createTableCell("Creator", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell(report.getCreator().getUsername(), BaseColor.WHITE, valueFont));
            table.addCell(createTableCell( "Assignee", BaseColor.LIGHT_GRAY, propertyFont));
            table.addCell(createTableCell( report.getAssignee().getUsername(), BaseColor.WHITE, valueFont));

            document.add(table);

            document.close();
            writer.close();
            return fileName;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    @Transactional
    public boolean addReport(Report report) {
        List<String> violations = reportValidator.validate(report);
        if(!violations.isEmpty()){
            return false;
        }
        this.reportRepository.save(report);
        return true;
    }

    @Override
    @Transactional
    public Report editReport(Long id, Report edited) {
        List<String> violations = reportValidator.validate(edited);
        if(!violations.isEmpty()){
            return null;
        }
        Optional<Report> opt = reportRepository.findById(id);
        if(opt.isEmpty()){
            return null;
        }
        Report toUpdate = opt.get();
        Report oldReport = toUpdate.toBuilder().build();
        toUpdate.setTitle(edited.getTitle());
        toUpdate.setDescription(edited.getDescription());
        toUpdate.setCategory(edited.getCategory());
        toUpdate.setLocation(edited.getLocation());
        toUpdate.setSeverity(edited.getSeverity());
        toUpdate.setStatus(edited.getStatus());
        toUpdate.setAssignee(edited.getAssignee());
        toUpdate.setAttachment(edited.getAttachment());
        toUpdate.setTargetDate(edited.getTargetDate());
        toUpdate.setAttachment(edited.getAttachment());
        reportRepository.save(toUpdate);
        return oldReport;
    }

    @Override
    @Transactional
    public Report closeReport(Long id) {
        Optional<Report> opt = reportRepository.findById(id);
        opt.ifPresent(report -> {report.setStatus(Status.CLOSED);reportRepository.save(report);});
        return opt.orElse(null);
    }



    @Override
    public ReportCounts getReportCounts() {
        ReportCounts counts = new ReportCounts();
        counts.setBuildingIssuesCount(reportRepository.countByCategory(Category.BUILDING_ISSUES));
        counts.setEnvironmentalIssuesCount(reportRepository.countByCategory(Category.ENVIRONMENTAL_ISSUES));
        counts.setPublicFacilityCount(reportRepository.countByCategory(Category.PUBLIC_FACILITY));
        counts.setRoadIssuesCount(reportRepository.countByCategory(Category.ROAD_ISSUES));
        counts.setUtilityIssuesCount(reportRepository.countByCategory(Category.UTILITY_ISSUES));
        counts.setHighSeverityCount(reportRepository.countBySeverity(Severity.HIGH));
        counts.setMediumSeverityCount(reportRepository.countBySeverity(Severity.MEDIUM));
        counts.setLowSeverityCount(reportRepository.countBySeverity(Severity.LOW));
        counts.setCriticalSeverityCount(reportRepository.countBySeverity(Severity.CRITICAL));
        counts.setNewStatusCount(reportRepository.countByStatus(Status.NEW));
        counts.setInProgressStatusCount(reportRepository.countByStatus(Status.IN_PROGRESS));
        counts.setFixedStatusCount(reportRepository.countByStatus(Status.FIXED));
        counts.setClosedStatusCount(reportRepository.countByStatus(Status.CLOSED));
        counts.setRejectedStatusCount(reportRepository.countByStatus(Status.REJECTED));
        counts.setInfoNeededStatusCount(reportRepository.countByStatus(Status.INFO_NEEDED));

        return counts;
    }
}

