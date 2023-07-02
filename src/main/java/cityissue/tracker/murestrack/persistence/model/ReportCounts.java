package cityissue.tracker.murestrack.persistence.model;

import lombok.Data;

@Data
public class ReportCounts {
        private int buildingIssuesCount;
        private int environmentalIssuesCount;
        private int publicFacilityCount;
        private int roadIssuesCount;
        private int utilityIssuesCount;
        private int highSeverityCount;
        private int mediumSeverityCount;
        private int lowSeverityCount;
        private int criticalSeverityCount;
        private int newStatusCount;
        private int inProgressStatusCount;
        private int fixedStatusCount;
        private int closedStatusCount;
        private int rejectedStatusCount;
        private int infoNeededStatusCount;

}
