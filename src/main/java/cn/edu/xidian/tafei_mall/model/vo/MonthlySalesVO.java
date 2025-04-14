package cn.edu.xidian.tafei_mall.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

// MonthlySalesVO.java
@Data
@ApiModel("月度销售数据视图")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySalesVO {
    private String reportId;
    private String period;
    private Summary summary;
    private List<ProductDetail> details;

    @Data
    public static class Summary {
        private BigDecimal totalSales;
        private Integer totalOrders;
        private Double avgRating;
        private TopProductVO topProduct;

        public Summary(BigDecimal totalSales, Integer totalOrders, Double avgRating, TopProductVO topProduct) {
            this.totalSales = totalSales;
            this.totalOrders = totalOrders;
            this.avgRating = avgRating;
            this.topProduct = topProduct;
        }
    }

    @Data
    public static class ProductDetail {
        private String productId;
        private String name;
        private Integer salesCount;
        private Double totalRevenue;
        private Double avgRating;
        private PromotionEffect promotionEffect;
    }

    @Data
    public static class PromotionEffect {
        private String promotionId;
    }

}