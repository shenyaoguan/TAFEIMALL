
package cn.edu.xidian.tafei_mall.service.impl;


import cn.edu.xidian.tafei_mall.model.entity.Order;
import cn.edu.xidian.tafei_mall.model.entity.User;
import cn.edu.xidian.tafei_mall.service.OrderService;
import cn.edu.xidian.tafei_mall.service.PayService;
import cn.edu.xidian.tafei_mall.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PayServiceImpl implements PayService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;

    // 模拟支付记录存储
    private final Map<String, PaymentRecord> paymentRecords = new ConcurrentHashMap<>();
    // 定时任务线程池
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    public boolean queryOrderStatus(String orderId) {
        // 检查是否超时
        PaymentRecord record = paymentRecords.get(orderId);
        if (LocalDateTime.now().isAfter(record.getExpireTime())) {
            record.setPaid(false);
            return false;
        }
        return "paid".equals(orderService.getOrderById(orderId).getStatus());
    }

    @Override
    public void createPayOrder(String orderId, String userId) {
        //获得订单信息
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作该订单");
        }
        // 如果已支付则直接返回
        if ("paid".equals(order.getStatus())) {
            return;
        }
        // 支付方式
        String paymentMethod = order.getPaymentMethod();
        // 总金额
        BigDecimal totalAmount = order.getTotalAmount();
        // 创建支付记录，30分钟超时
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(orderId);
        record.setPaymentMethod(paymentMethod);
        record.setAmount(totalAmount);
        record.setCreatedAt(LocalDateTime.now());
        record.setExpireTime(LocalDateTime.now().plusMinutes(30));
        record.setPaid(false);
        paymentRecords.put(orderId, record);
        // 模拟异步支付处理
        scheduler.schedule(() -> {
            // 模拟支付成功
            record.setPaid(true);
            orderService.updateOrderStatus(orderId, "paid");

            // 模拟异步通知
            sendPaymentNotification(orderId);
        }, 3, TimeUnit.SECONDS); // 模拟3秒后支付成功
    }
    @Override
    public String generatePaymentInfo(String orderId) {
        PaymentRecord record = paymentRecords.get(orderId);
        String paymentMethod = record.getPaymentMethod();
        // 检查是否超时
        if (LocalDateTime.now().isAfter(record.getExpireTime())) {
            throw new RuntimeException("支付已超时");
        }
        // 模拟生成不同支付方式的支付信息
        switch (paymentMethod.toLowerCase()) {
            case "alipay":
                return "alipay://payment?orderId=" + orderId + "&amount=" + record.getAmount();
            case "wechat":
                return "wechat://pay?orderId=" + orderId + "&amount=" + record.getAmount();
            case "bank":
                return "bank://transfer?orderId=" + orderId + "&amount=" + record.getAmount();
            default:
                throw new RuntimeException("不支持的支付方式");
        }
    }

    // 模拟发送支付通知
    private void sendPaymentNotification(String orderId) {
        System.out.println("发送支付成功通知，订单ID: " + orderId);
    }

    // 支付记录类
    @Data
    private static class PaymentRecord {
        private String orderId;
        private String paymentMethod;
        private BigDecimal amount;
        private LocalDateTime createdAt;
        private LocalDateTime expireTime;
        private boolean paid;
    }
}
