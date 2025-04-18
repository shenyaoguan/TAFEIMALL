package cn.edu.xidian.tafei_mall.model.vo.Response.Promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class createPromotionResponse {
    private final String promotionId;

    public createPromotionResponse(String promotionId) {
        this.promotionId = promotionId;
    }
}

