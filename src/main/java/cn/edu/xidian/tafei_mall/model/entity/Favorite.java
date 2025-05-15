package cn.edu.xidian.tafei_mall.model.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>
 * 收藏表
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Getter
@Setter
@TableName("t_user_favorites")
@ApiModel(value = "Favorite对象", description = "收藏表")
public class Favorite implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("收藏ID")
    @TableId("favorite_id")
    private String favoriteId;

    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("商品ID")
    @TableField("product_id")

    private String productId;

    @ApiModelProperty("创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @ApiModelProperty("更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;

}