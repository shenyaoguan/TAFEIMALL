package cn.edu.xidian.tafei_mall.service.impl;


import cn.edu.xidian.tafei_mall.mapper.*;
import cn.edu.xidian.tafei_mall.model.entity.*;
import cn.edu.xidian.tafei_mall.model.vo.OrderCreateVO;
import cn.edu.xidian.tafei_mall.model.vo.OrderUpdateVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Order.*;
import cn.edu.xidian.tafei_mall.model.vo.Response.Seller.updateOrderResponse;
import cn.edu.xidian.tafei_mall.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Contract;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private UserMapper userMapper;

    /*----------------------同层调用----------------------*/

    /**
     * 获取订单(内部使用)
     * @param orderId 订单ID
     * @return 订单项列表
     */
    @Override
    public Order getOrderById(String orderId){
        return orderMapper.selectById(orderId);
    }

    @Override
    public String updateOrderStatus(String orderId, String status){
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        switch (status) {
            case "paid":{
                if (!order.getStatus().equals("pending")) {
                    throw new IllegalArgumentException("Order cannot be paid");
                }
                Cart cart = cartMapper.selectOne(new QueryWrapper<Cart>().eq("user_id", order.getUserId()));
                List<OrderItem> orderItems = orderItemService.getOrderItemByOrderId(orderId);
                Map<CartItem, Product> products = new HashMap<>();
                // 验证购物车商品是否存在，库存是否足够
                List<String> orderItemIds = new ArrayList<>();
                boolean cartItemExists = true;
                List<String> productIdsOfNotExist = new ArrayList<>();
                boolean productExists = true;
                List<String> productIdsOfNotEnough = new ArrayList<>();
                boolean productStockEnough = true;
                for (OrderItem orderItem : orderItems) {
                    CartItem cartItem = cartItemMapper.selectOne(new QueryWrapper<CartItem>().eq("cart_id", cart.getCartId()).eq("product_id", orderItem.getProductId()));
                    if (cartItem == null) {
                        orderItemIds.add(orderItem.getOrderItemId());
                        cartItemExists = false;
                    }
                    if (!cartItemExists) {
                        continue;
                    }
                    Optional<Product> product = productService.getProductById(cartItem.getProductId());
                    if (product.isEmpty()) {
                        productIdsOfNotExist.add(orderItem.getProductId());
                        productExists = false;
                    }
                    if (!productExists) {
                        continue;
                    }
                    if (cartItem.getQuantity() > product.get().getStock()) {
                        productIdsOfNotEnough.add(orderItem.getProductId());
                        productStockEnough = false;
                    }
                    if (!productStockEnough) {
                        continue;
                    }
                    products.put(cartItem, product.get());
                }
                if (!cartItemExists) {
                    throw new IllegalArgumentException("Order item not found in cart: " + String.join(",", orderItemIds));
                }
                if (!productExists) {
                    throw new IllegalArgumentException("Product not found: " + String.join(",", productIdsOfNotExist));
                }
                if (!productStockEnough) {
                    throw new IllegalArgumentException("Product not enough: " + String.join(",", productIdsOfNotEnough));
                }
                // 清空购物车，商品库存减少
                for (Map.Entry<CartItem, Product> entry : products.entrySet()) {
                    Product product = entry.getValue();
                    product.setStock(product.getStock() - entry.getKey().getQuantity());
                    product.setUpdatedAt(LocalDateTime.now());
                    productMapper.updateById(product);
                    cartItemMapper.deleteById(entry.getKey().getCartItemId());
                }
                break;
            }
            default: throw new IllegalArgumentException("Order status error");
        }
        order.setStatus(status);
        orderMapper.updateById(order);
        return order.getStatus();
    }

    /*----------------------买家视角----------------------*/

    /**
     * 获取订单详情(买家)
     * @param userId 用户ID
     * @return 订单详情列表
     */
    @Override
    public getOrderResponse getOrderByCustomer(String userId){
//        List <Order> orders = orderMapper.findByUserId(userId);
        List<Order> orders = orderMapper.selectList(new QueryWrapper<Order>().eq("user_id", userId));
        return new getOrderResponse(OrderListGenerator(orders, false));
    }

    /**
     * 获取订单详情(买家)
     * @param OrderId 订单ID
     * @param userId 用户ID
     * @return 订单详情列表
     */
    @Override
    public getOrderResponse getOrderByCustomer(String OrderId, String userId) {
        Order order = orderMapper.selectById(OrderId);
        if (order == null) {
            return new getOrderResponse(null);
        }
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to current user");
        }
        // 获取订单项
        List<OrderResponse> orderResponse = new ArrayList<>();
        orderResponse.add(OrderDetailGenerator(order));
        return new getOrderResponse(orderResponse);
    }

    /**
     * 创建订单
     * @param cartId 购物车ID
     * @param orderCreateVO 地址ID
     * @param userId 用户ID
     * @return 订单ID
     */
    @Override
    public createOrderResponse createOrder(String cartId, OrderCreateVO orderCreateVO, String userId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null) {
            throw new IllegalArgumentException("Invalid cart ID");
        }
        if (!cart.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Cart does not belong to current user");
        }
        // 获取购物车
        List<CartItem> cartItems = cartItemMapper.selectList(new QueryWrapper<CartItem>().eq("cart_id", cartId));
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        // 将所有购物车项按照卖家分开，每个商家分别生成一个List<OrderItem>
        Map<String, List<OrderItem>> orderItemListMap = new HashMap<>();
        for (CartItem cartItem : cartItems) {
            Optional<Product> product = productService.getProductById(cartItem.getProductId());
            if (product.isEmpty()) {
                throw new IllegalArgumentException("Invalid product ID");
            }

            String sellerId = product.get().getSellerId();
            if (!orderItemListMap.containsKey(sellerId)) {
                orderItemListMap.put(sellerId, new ArrayList<>());
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(productService.currentPrice(product.get().getProductId()).multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItemListMap.get(sellerId).add(orderItem);
        }
        // 获取地址
        String addressId;
        if (orderCreateVO != null) {
            addressId = orderCreateVO.getShippingAddressId();
            Address address = addressMapper.selectById(addressId);
            if (address == null) {
                throw new IllegalArgumentException("Invalid address ID");
            }
        } else { // 如果没有传入地址ID，使用用户默认地址(默认为第一个地址)
            Address address = addressMapper.selectList(new QueryWrapper<Address>().eq("user_id", userId)).get(0);
            if (address == null) {
                throw new IllegalArgumentException("Address not found");
            }
            addressId = address.getAddressId();
        }
        // 生成每个seller的订单
        List<String> orderIds = new ArrayList<>();
        for (Map.Entry<String, List<OrderItem>> entry : orderItemListMap.entrySet()) {
            // 创建订单
            Order order = new Order();
            order.setUserId(userId);
            order.setSellerId(entry.getKey());
            order.setShippingAddressId(addressId);
            order.setStatus("pending");
            order.setCreatedAt(LocalDateTime.now());
            order.setUpdatedAt(LocalDateTime.now());
            order.setOrderId(UUID.randomUUID().toString());
            orderMapper.insert(order);
            orderIds.add(order.getOrderId());
            // 生成订单项
            String orderId = order.getOrderId();
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderItem orderItem : entry.getValue()) {
                orderItem.setOrderId(orderId);
                orderItem.setOrderItemId(UUID.randomUUID().toString());
                orderItemService.addOrderItem(orderItem);
                totalAmount = totalAmount.add(orderItem.getPrice());
            }
            // 更新订单总金额
            order.setTotalAmount(totalAmount);
            orderMapper.updateById(order);
        }
        return new createOrderResponse(orderIds);
    }

    /**
     * 确认收货
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    public boolean confirmOrder(String orderId, String userId) {
        // 验证订单状态是否可以确认收货
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to current user");
        }
        if (!order.getStatus().equals("shipping")) {
            throw new IllegalArgumentException("Order cannot be confirmed");
        }
        // 更改订单状态为已完成
        order.setStatus("finished");
        // 提交
        orderMapper.updateById(order);
        return true;
    }

    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 是否成功
     */
    @Override
    public boolean cancelOrder(String orderId, String userId) {
        // 验证订单状态是否可以取消
        Order order = getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to current user");
        }
        if (!order.getStatus().equals("pending") && !order.getStatus().equals("paid")) {
            throw new IllegalArgumentException("Order cannot be cancelled");
        }
        // 更改订单状态为已取消
        order.setStatus("canceled");
        // 提交
        orderMapper.updateById(order);
        return true;
    }

    /*----------------------卖家视角----------------------*/

    /**
     * 获取订单详情(卖家)
     * @param userId 卖家ID
     * @return 订单详情列表
     */
    @Override
    public getOrderResponse getOrderBySeller(String userId) {
        List<Order> orders = orderMapper.selectList(new QueryWrapper<Order>().eq("seller_id", userId));
        return new getOrderResponse(OrderListGenerator(orders, true));
    }

    /**
     * 获取订单详情(卖家)
     * @param orderId 订单ID
     * @param userId 卖家ID
     * @return 订单详情列表
     */
    @Override
    public getOrderResponse getOrderBySeller(String orderId, String userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return new getOrderResponse(null);
        }
        if (!order.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to current seller");
        }
        if (order.getStatus().equals("pending")) {
            throw new IllegalArgumentException("Unpaid order is not visible");
        }
        // 获取订单项
        List<OrderResponse> orderResponse = new ArrayList<>();
        orderResponse.add(OrderDetailGenerator(order));
        return new getOrderResponse(orderResponse);
    }

    /**
     * 更新订单状态
     * @param orderId 订单ID
     * @param orderUpdateVO 订单更新信息
     * @param userId 卖家ID
     * @return 订单状态
     */
    @Override
    public updateOrderResponse updateOrderBySeller(String orderId, OrderUpdateVO orderUpdateVO, String userId){
        // 验证订单是否属于当前卖家
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        if (!order.getSellerId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to current seller");
        }
        // 更新订单状态
        switch (orderUpdateVO.getAction()) {
            case "shipping": {
                if (!order.getStatus().equals("paid")) {
                    throw new IllegalArgumentException("Order must be paid");
                }
                order.setStatus("shipping");
                // order.setTrackingNumber(orderUpdateVO.getTrackingNumber());
                order.setUpdatedAt(LocalDateTime.now());
                orderMapper.updateById(order);
                if (Objects.equals(order.getStatus(), "shipping")) {
                    return new updateOrderResponse("已发货");
                } else {
                    throw new RuntimeException("Failed to update order status");
                }

            }
            case "canceled": {
                if (!order.getStatus().equals("pending") && !order.getStatus().equals("paid")) {
                    throw new IllegalArgumentException("Order cannot be cancelled");
                }
                order.setStatus("canceled");
                order.setUpdatedAt(LocalDateTime.now());
                orderMapper.updateById(order);
                if (Objects.equals(order.getStatus(), "canceled")) {
                    return new updateOrderResponse("已取消");
                } else {
                    throw new RuntimeException("Failed to update order status");
                }
            }
            default:
                throw new IllegalArgumentException("Invalid status");
        }
    }

    /*----------------------内部方法----------------------*/

    /**
     * 生成订单列表
     * @param orders 订单列表
     * @return 订单ID
     */
    @Contract("_ -> new")
    private @Nullable List<OrderResponse> OrderListGenerator(@Nullable List <Order> orders, @NonNull boolean isSeller) {
        if (orders == null || orders.isEmpty()) {
            return null;
        }
        // 获取订单
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            if (isSeller && order.getStatus().equals("pending")) {
                continue; // 卖家不显示待付款订单
            }
            orderResponses.add(OrderDetailGenerator(order));
        }
        return orderResponses;
    }

    /**
     * 生成订单详情
     * @param order 订单
     * @return 订单详情
     */
    // 生成订单详情，class内部使用
    @Contract("_ -> new")
    private @NonNull OrderResponse OrderDetailGenerator(@NonNull Order order) {
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        // 获取买家和卖家信息
        String userName = userMapper.selectById(order.getUserId()).getUsername();
        String sellerName = userMapper.selectById(order.getSellerId()).getUsername();
        // 获取订单项
        List<OrderItem> orderItems = orderItemService.getOrderItemByOrderId(order.getOrderId());
        if (orderItems.isEmpty()) {
            throw new IllegalArgumentException("OrderItem is empty");
        }
        for (OrderItem orderItem : orderItems) {
            Optional<Product> product = productService.getProductById(orderItem.getProductId());
            if (product.isEmpty()) {
                throw new IllegalArgumentException("Product not found");
            }
            orderItemResponses.add(new OrderItemResponse(orderItem.getOrderItemId(), orderItem.getProductId(), product.get().getName(), orderItem.getQuantity(), orderItem.getPrice()));
        }
        // 生成订单详情
        return new OrderResponse(order.getOrderId(), order.getStatus(), userName, sellerName, new getOrderItemResponse(orderItemResponses));
    }
}
