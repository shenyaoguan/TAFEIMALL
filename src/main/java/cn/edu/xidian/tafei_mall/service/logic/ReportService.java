package cn.edu.xidian.tafei_mall.service.logic;

import cn.edu.xidian.tafei_mall.model.entity.Order;
import cn.edu.xidian.tafei_mall.model.vo.MonthlySalesVO;
import cn.edu.xidian.tafei_mall.model.vo.TopProductVO;
import cn.edu.xidian.tafei_mall.service.OrderItemService;
import cn.edu.xidian.tafei_mall.service.OrderService;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.edu.xidian.tafei_mall.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class ReportService {

    @Resource
    private OrderItemService orService;

    @Resource
    private OrderService oService;

    @Resource
    private ProductService pService;

    public MonthlySalesVO generateMonthlyReport(Integer year, Integer month, Boolean detail) {
        if(year < 2000 || year > 2100 || month < 1 || month > 12){
//            throw new IllegalArgumentException("Invalid year or month");
            return null;
        }
        String reportId = IdUtil.generateRandomId();
        String mouthStr = month < 10 ? "0" + month : String.valueOf(month);
        String period = year + "-" + mouthStr;
        // 查询summary
        String startDate = year + "-" + mouthStr + "-01";
        String endDate = year + "-" + mouthStr + "-31";
        Integer totalOrders = orService.getSalesVolume(startDate, endDate);
        // TODO 查询好评差评待做
        // 查询amount
        LambdaQueryWrapper<Order> oWrapper = new LambdaQueryWrapper<>();
        oWrapper.between(Order::getCreatedAt, startDate, endDate);
        List<Order> orders = oService.getBaseMapper().selectList(oWrapper);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Order order : orders) {
            totalAmount = totalAmount.add(order.getTotalAmount());
        }
        // TODO 周转数据待做
        TopProductVO topProduct = pService.getTopProduct();
        MonthlySalesVO.Summary summary = new MonthlySalesVO.Summary(totalAmount, totalOrders, 0.0, topProduct);
        if(detail == null || !detail){
            return new MonthlySalesVO(reportId, period, summary, new ArrayList<>());
        }
        // TODO detail待做
        return new MonthlySalesVO(reportId, period, summary, new ArrayList<>());

    }
}
