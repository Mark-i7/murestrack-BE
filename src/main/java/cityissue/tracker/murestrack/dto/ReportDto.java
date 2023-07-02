package cityissue.tracker.murestrack.dto;

import cityissue.tracker.murestrack.persistence.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.Date;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ReportDto {
    private Long id;
    private String title;
    private String description;
    private Category category;
    private String location;
    private Date targetDate;
    private Severity severity;
    private String creator;
    private Status status;
    private String assignee;
    private AttachmentDTO attachmentDTO;

    public static ReportDto convertToDTO(Report report){
        AttachmentDTO attachmentDTO= null;
        if(report.getAttachment() != null){
            Attachment reportAttachment = report.getAttachment();
            attachmentDTO = AttachmentDTO.builder().
                    id(reportAttachment.getId())
                    .fileName(reportAttachment.getFileName())
                    .type(reportAttachment.getType())
                    .size(reportAttachment.getData().length)
                    .build();

        }
        return ReportDto.builder()
                .id(report.getId())
                .title(report.getTitle())
                .description(report.getDescription())
                .category(report.getCategory())
                .location(report.getLocation())
                .targetDate(report.getTargetDate())
                .severity(report.getSeverity())
                .creator(report.getCreator().getUsername())
                .assignee(report.getAssignee().getUsername())
                .status(report.getStatus())
                .attachmentDTO(attachmentDTO)
                .build();
    }

}
