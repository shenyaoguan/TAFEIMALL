package cn.edu.xidian.tafei_mall.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("t_promotions")
@ApiModel(value = "Promotion对象", description = "促销活动表")
public class Promotion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("促销活动ID")
    @TableId("promotion_id")
    private String promotionId;

    @ApiModelProperty("活动开始时间")
    @TableField("start_date")
    private LocalDateTime startDate;

    @ApiModelProperty("活动结束时间")
    @TableField("end_date")
    private LocalDateTime endDate;

    @ApiModelProperty("是否激活")
    @TableField("is_active")
    private Boolean isActive;
}