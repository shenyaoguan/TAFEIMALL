// cn.edu.xidian.tafei_mall.controller.ReportController.java
package cn.edu.xidian.tafei_mall.controller;

import cn.edu.xidian.tafei_mall.model.vo.MonthlySalesVO;
import cn.edu.xidian.tafei_mall.service.logic.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/seller/reports")
@RequiredArgsConstructor
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/monthly")
    public MonthlySalesVO getMonthlyReport(
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(required = false) Boolean detail
    ) {
        return reportService.generateMonthlyReport(year, month, detail);
    }
}