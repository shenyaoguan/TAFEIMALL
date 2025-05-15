package cn.edu.xidian.tafei_mall.model.vo.Response.Promotion;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class getPromotionResponse{
    private final String promotionId;
    private final BigDecimal discountRate;
    private final BigDecimal basePrice;
    private final BigDecimal currentPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH")
    private final LocalDateTime validUntil;

    public getPromotionResponse(String promotionId,
                                BigDecimal discountRate,
                                BigDecimal basePrice,
                                BigDecimal currentPrice,
                                LocalDateTime validUntil) {
        this.promotionId = promotionId;
        this.discountRate = discountRate;
        this.basePrice = basePrice;
        this.currentPrice = currentPrice;
        this.validUntil = validUntil;
    }
}