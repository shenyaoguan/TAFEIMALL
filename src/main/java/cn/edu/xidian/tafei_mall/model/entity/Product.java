package cn.edu.xidian.tafei_mall.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Getter
@Setter
@TableName("product")
@ApiModel(value = "Product对象", description = "商品表")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品ID")
    @TableId("product_id")
    private String productId;

    @ApiModelProperty("商品名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("商品描述")
    @TableField("description")
    private String description;

    @ApiModelProperty("价格")
    @TableField("price")
    private BigDecimal price;

    @ApiModelProperty("当前价格")
    @TableField("current_price")
    private BigDecimal currentPrice;

    @ApiModelProperty("库存")
    @TableField("stock")
    private Integer stock;

    @ApiModelProperty("是否包邮")
    @TableField("is_free_shipping")
    private Boolean isFreeShipping;

    @ApiModelProperty("卖家ID")
    @TableField("seller_id")
    private String sellerId;

    @ApiModelProperty("创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @ApiModelProperty("更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @ApiModelProperty("商品图片URL列表")
    @TableField(exist = false)
    private List<String> mainPictures;
}
