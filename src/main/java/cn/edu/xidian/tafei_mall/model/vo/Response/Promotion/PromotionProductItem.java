package cn.edu.xidian.tafei_mall.model.vo.Response.Promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionProductItem {
    private final String productId;
    private final String name;
    private final BigDecimal discountRate;
    private final BigDecimal currentPrice;
    private final LocalDateTime validUntil;

    public PromotionProductItem(String productId, String name, BigDecimal discountRate, BigDecimal currentPrice, LocalDateTime validUntil) {
        this.productId = productId;
        this.name = name;
        this.discountRate = discountRate;
        this.currentPrice = currentPrice;
        this.validUntil = validUntil;
    }
}
