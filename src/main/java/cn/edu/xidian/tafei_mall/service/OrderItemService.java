package cn.edu.xidian.tafei_mall.service;

import cn.edu.xidian.tafei_mall.model.entity.OrderItem;
import cn.edu.xidian.tafei_mall.model.vo.Response.Order.getOrderItemResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单项表 服务类
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
public interface OrderItemService extends IService<OrderItem> {
    // Server层中，同层调用，不需要暴露给上层
    OrderItem getOrderItemById(String orderItemId);
    List<OrderItem> getOrderItemByOrderId(String orderId);
    String addOrderItem(OrderItem orderItem);
    // 卖家
    getOrderItemResponse getOrderItemBySeller(String userId);

    Integer getSalesVolume(String startDate, String endDate);
}