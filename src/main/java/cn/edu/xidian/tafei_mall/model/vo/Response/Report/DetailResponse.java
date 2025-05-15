package cn.edu.xidian.tafei_mall.model.vo.Response.Report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DetailResponse {
    private final String productId;
    private final String name;
    private final int salesCount;
    private final double totalRevenue;
    private final float avgRating;
    private final PromotionEffect promotionEffect;

    public DetailResponse(String productId, String name, int salesCount, double totalRevenue, float avgRating, String promotionId) {
        this.productId = productId;
        this.name = name;
        this.salesCount = salesCount;
        this.totalRevenue = totalRevenue;
        this.avgRating = avgRating;
        this.promotionEffect = new PromotionEffect(promotionId);
    }
}

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
class PromotionEffect {
    private final String promotionId;

    PromotionEffect(String promotionId) {
        this.promotionId = promotionId;
    }
}