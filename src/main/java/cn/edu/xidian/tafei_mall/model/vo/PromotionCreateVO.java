package cn.edu.xidian.tafei_mall.model.vo;

import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Data
public class PromotionCreateVO {
    private List<String> productId;
    private BigDecimal discountRate;
    private String startDate;
    private String endDate;
}
