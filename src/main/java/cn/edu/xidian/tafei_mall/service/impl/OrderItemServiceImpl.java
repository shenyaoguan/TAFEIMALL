package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.ProductMapper;
import cn.edu.xidian.tafei_mall.model.entity.OrderItem;
import cn.edu.xidian.tafei_mall.mapper.OrderItemMapper;
import cn.edu.xidian.tafei_mall.model.entity.Product;
import cn.edu.xidian.tafei_mall.model.vo.Response.Order.OrderItemResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Order.getOrderItemResponse;
import cn.edu.xidian.tafei_mall.service.OrderItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 订单项表 服务实现类
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    /*----------------------同层调用----------------------*/

    @Override
    public OrderItem getOrderItemById(String orderItemId){
        return orderItemMapper.selectById(orderItemId);
    }

    @Override
    public List<OrderItem> getOrderItemByOrderId(String orderId){
        return orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("order_id", orderId));
    }

    @Override
    public String addOrderItem(OrderItem orderItem){
        orderItemMapper.insert(orderItem);
        return orderItem.getOrderItemId();
    }

    /*----------------------卖家视角----------------------*/

    @Override
    public getOrderItemResponse getOrderItemBySeller(String userId){
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>().eq(Product::getSellerId, userId));
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();

        for (Product product : products) {
            List<OrderItem> orderItems = orderItemMapper.selectList(new QueryWrapper<OrderItem>().eq("product_id", product.getProductId()));
            for (OrderItem orderItem : orderItems) {
                orderItemResponses.add(new OrderItemResponse(orderItem.getOrderItemId(), product.getProductId(), product.getName(), orderItem.getQuantity(), orderItem.getPrice()));
            }
        }
        return new getOrderItemResponse(orderItemResponses);
    }

    @Override
    public Integer getSalesVolume(String startDate, String endDate) {
        return orderItemMapper.getSalesVolume(startDate, endDate);
    }


}