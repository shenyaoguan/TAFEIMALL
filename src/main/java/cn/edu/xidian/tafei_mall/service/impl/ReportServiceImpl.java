package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.*;
import cn.edu.xidian.tafei_mall.model.entity.*;
import cn.edu.xidian.tafei_mall.model.vo.Response.Report.DetailResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Report.SummaryResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Report.TopProductResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Report.createReportResponse;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.edu.xidian.tafei_mall.service.ReportService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    PromotionProductMapper promotionProductMapper;
    @Autowired
    PromotionMapper promotionMapper;

    @Override
    public createReportResponse createMonthlyReport(int year, int month, boolean detail, String userId){
        List<Order> orders = orderMapper.selectList(new QueryWrapper<Order>()
                .eq("seller_id", userId)
                .apply("YEAR(created_at) = {0}", year)
                .apply("MONTH(created_at) = {0}", month));

        if (orders == null || orders.isEmpty()) {
            throw new RuntimeException("No orders found for the specified period");
        }

        String reportId = UUID.randomUUID().toString();
        String period = String.format("%04d-%02d", year, month);

        // 对所有OrderItem按ProductId分组
        int totalOrders = orders.size();
        Map<String, List<OrderItem>> OrderItemsMap = new HashMap<>();
        double totalSales = 0;
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_id", order.getOrderId()));
            if (orderItems == null || orderItems.isEmpty()) {
                continue;
            }
            // 按ProductId分组
            for (OrderItem orderItem : orderItems) {
                totalSales += orderItem.getPrice().doubleValue();
                String productId = orderItem.getProductId();
                if (!OrderItemsMap.containsKey(productId)) {
                    OrderItemsMap.put(productId, new ArrayList<>());
                }
                OrderItemsMap.get(productId).add(orderItem);
            }
        }
        // 处理分组后的数据
        String topProductId = null;
        int TopProductSalesCount = 0;
        List<Double> productAveRatings = new ArrayList<>();
        List<DetailResponse> detailResponses = new ArrayList<>();
        for (Map.Entry<String, List<OrderItem>> entry : OrderItemsMap.entrySet()) {
            // 对于每件商品
            String productId = entry.getKey();
            List<OrderItem> orderItems = entry.getValue();
            // 计算商品的平均评分
            List<Review> reviews = reviewMapper.selectList(new QueryWrapper<Review>().eq("product_id", productId));
            int  totalRating = 0;
            for (Review review : reviews) {
                totalRating += review.getRating();
            }
            double averageRating = !reviews.isEmpty() ? (double) totalRating / reviews.size() : 0;
            productAveRatings.add(averageRating);
            // 计算商品的销售数量，以获得销售数量最大的商品
            int productSalesCount = 0;
            double productTotalRevenue = 0;
            for (OrderItem orderItem : orderItems) {
                productSalesCount += orderItem.getQuantity();
                productTotalRevenue += orderItem.getPrice().doubleValue();
            }
            if (productSalesCount > TopProductSalesCount) {
                TopProductSalesCount = productSalesCount;
                topProductId = productId;
            }
            // 当detail = true时，添加详细报告
            if (detail) {
                Product product = productMapper.selectById(productId);
                if (product != null) {
                    // 获取本月订单创建时间
                    List<LocalDateTime> createTimes = new ArrayList<>();
                    for (OrderItem orderItem : orderItems) {
                        createTimes.add(orderItem.getCreatedAt());
                    }
                    // 处理促销ID
                    List<String> promotionIds = new ArrayList<>();
                    List<PromotionProduct> promotionProducts = promotionProductMapper.selectList(new LambdaQueryWrapper<PromotionProduct>().eq(PromotionProduct::getProductId, productId));
                    // 遍历促销活动
                    for (PromotionProduct promotionProduct : promotionProducts) {
                        String promotionId = promotionProduct.getPromotionId();
                        Promotion promotion = promotionMapper.selectById(promotionId);
                        if (promotion != null) {
                            // 检查促销活动的开始和结束时间，保证在本月内有效过
                            if (promotion.getStartDate().isBefore(LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(), 23, 59))
                                    && promotion.getEndDate().isAfter(LocalDateTime.of(year, month, 1, 0, 0))) {
                                // 如果有某个订单项的创建时间在促销活动的开始和结束时间之间，则添加该促销ID
                                for (LocalDateTime createTime : createTimes) {
                                    if (createTime.isAfter(promotion.getStartDate()) && createTime.isBefore(promotion.getEndDate())) {
                                        promotionIds.add(promotionId);
                                        break; // 找到一个符合条件的促销活动就可以了
                                    }
                                }
                            }
                        }
                    }
                    String productName = product.getName();
                    if (!promotionIds.isEmpty()) {
                        // 根据沟通，目前一个月最多只能有一个活动参与，因此仅返回一个活动
                        detailResponses.add(new DetailResponse(productId, productName, productSalesCount, productTotalRevenue, (float)averageRating, promotionIds.get(0)));
                    } else {
                        detailResponses.add(new DetailResponse(productId, productName, productSalesCount, productTotalRevenue, (float)averageRating, null));
                    }

                }
            }
        }
        String topProductName = productMapper.selectById(topProductId).getName();
        double averageRating = productAveRatings.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        // 创建报告
        if (detail){
            // 详细报告
            return new createReportResponse(reportId, period, new SummaryResponse(totalSales, totalOrders, (float)averageRating, new TopProductResponse(topProductId, topProductName, TopProductSalesCount)), detailResponses);
        } else {
            // 简要报告
            return new createReportResponse(reportId, period, new SummaryResponse(totalSales, totalOrders, (float)averageRating, new TopProductResponse(topProductId, topProductName, TopProductSalesCount)), null);
        }
    }
}
