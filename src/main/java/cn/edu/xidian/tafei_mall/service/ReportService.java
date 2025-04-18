package cn.edu.xidian.tafei_mall.service;

import cn.edu.xidian.tafei_mall.model.vo.Response.Report.createReportResponse;

public interface ReportService {
    createReportResponse createMonthlyReport(int year, int month, boolean detail, String userId);
}
