package cn.edu.xidian.tafei_mall.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/*
*
* 用于按名称查询商品时返回第一张图片
*
* */

@Data
@Getter
@Setter
public class ProductSimpleVO {

    @ApiModelProperty("商品ID")
    private String productId;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品价格")
    private BigDecimal price;

    @ApiModelProperty("商品缩略图（第一张图片URL）")
    private String thumbnail;
}
