package cn.edu.xidian.tafei_mall.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Getter
@Setter
@TableName("t_order")
@ApiModel(value = "Order对象", description = "订单表")
public class Order implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单ID")
    @TableId("order_id")
    private String orderId;

    @ApiModelProperty("用户ID")
    @TableField("user_id")
    private String userId;

    @ApiModelProperty("卖家ID")
    @TableField("seller_id")
    private String sellerId;

    @ApiModelProperty("总金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @ApiModelProperty("支付方式")
    @TableField("payment_method")
    private String paymentMethod;

    @ApiModelProperty("收货地址ID")
    @TableField("shipping_address_id")
    private String shippingAddressId;

    @ApiModelProperty("订单状态")
    @TableField("status")
    private String status;

    @ApiModelProperty("物流单号")
    @TableField("tracking_number")
    private String trackingNumber;

    @ApiModelProperty("创建时间")
    @TableField("created_at")
    private LocalDateTime createdAt;

    @ApiModelProperty("更新时间")
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
