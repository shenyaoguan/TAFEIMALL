package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.model.entity.Order;
import cn.edu.xidian.tafei_mall.mapper.OrderMapper;
import cn.edu.xidian.tafei_mall.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Override
    public void cancelOrder(String orderId) {
        // 查询订单
        Order order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        // 检查订单状态（假设 "PENDING" 和 "PAID" 可取消）
        /*
        * 和数据库中订单表的状态保持一致
        *
        * */
        if (!"pending".equals(order.getStatus()) && !"paid".equals(order.getStatus())) {
            throw new RuntimeException("Order cannot be canceled");
        }

        // 更新订单状态为 "CANCELED"
        order.setStatus("canceled");
        order.setUpdatedAt(LocalDateTime.now());

        // 保存修改
        updateById(order);
    }
}
