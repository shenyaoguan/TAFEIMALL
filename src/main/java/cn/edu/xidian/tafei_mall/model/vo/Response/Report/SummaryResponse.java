package cn.edu.xidian.tafei_mall.model.vo.Response.Report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryResponse{
    private final double totalSales;
    private final int totalOrders;
    private final float avgRating;
    private final TopProductResponse topProduct;

    public SummaryResponse(double totalSales, int totalOrders, float avgRating, TopProductResponse topProduct) {
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
        this.avgRating = avgRating;
        this.topProduct = topProduct;
    }
}