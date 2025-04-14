package cn.edu.xidian.tafei_mall.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("product_reviews")
//@ApiModel(value = "", description = "")
public class Review implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("评价ID")
    @TableId("review_id")
    private String reviewId;

    @ApiModelProperty("商品ID")
    @TableId("product_id")
    private String productId;

    @ApiModelProperty("订单ID")
    @TableId("order_id")
    private String orderId;

    @ApiModelProperty("评分")
    @TableId("rating")
    private Integer rating;

    @ApiModelProperty("评价内容")
    @TableId("comment")
    private String comment;

    @ApiModelProperty("创建时间")
    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @ApiModelProperty("更新时间")
    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

}
