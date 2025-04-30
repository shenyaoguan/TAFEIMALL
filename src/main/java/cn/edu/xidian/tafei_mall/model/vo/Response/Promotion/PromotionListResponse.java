package cn.edu.xidian.tafei_mall.model.vo.Response.Promotion;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionListResponse {
    private final Integer total;
    private final List<PromotionProductItem> promotions;

    public PromotionListResponse(Integer total, List<PromotionProductItem> promotions) {
        this.total = total;
        this.promotions = promotions;
    }
}

