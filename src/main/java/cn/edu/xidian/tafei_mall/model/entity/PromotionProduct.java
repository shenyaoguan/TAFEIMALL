package cn.edu.xidian.tafei_mall.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@TableName("t_promotion_products")
@ApiModel(value = "PromotionProduct对象", description = "促销商品关联表")
public class PromotionProduct implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("促销活动ID")
    @MppMultiId
    @TableField("promotion_id")
    private String promotionId;

    @ApiModelProperty("商品ID")
    @MppMultiId
    @TableField("product_id")
    private String productId;

    @ApiModelProperty("折扣率(1~100)")
    @TableField("discount_rate")
    private BigDecimal discountRate;
}