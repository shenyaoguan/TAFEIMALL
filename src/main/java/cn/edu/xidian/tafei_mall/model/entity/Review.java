package cn.edu.xidian.tafei_mall.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_product_reviews")
@ApiModel(value = "ProductReview对象", description = "商品评价表")
public class Review implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("评价ID")
    @TableId("review_id")
    private String reviewId;

    @ApiModelProperty("商品ID")
    @TableField("product_id")
    private String productId;

    @ApiModelProperty("订单ID")
    @TableField("order_id")
    private String orderId;

    @ApiModelProperty("评分(1-5)")
    @TableField("rating")
    private Integer rating;

    @ApiModelProperty("评价内容")
    @TableField("comment")
    private String comment;

    @ApiModelProperty("创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @ApiModelProperty("更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}