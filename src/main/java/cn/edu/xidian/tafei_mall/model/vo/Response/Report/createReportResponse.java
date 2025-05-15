package cn.edu.xidian.tafei_mall.model.vo.Response.Report;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class createReportResponse {
    private final String reportId;
    private final String period;
    private final SummaryResponse summary;
    private final List<DetailResponse> details;

    public createReportResponse(String reportId, String period, SummaryResponse summary, List<DetailResponse> details) {
        this.reportId = reportId;
        this.period = period;
        this.summary = summary;
        this.details = details;
    }
}