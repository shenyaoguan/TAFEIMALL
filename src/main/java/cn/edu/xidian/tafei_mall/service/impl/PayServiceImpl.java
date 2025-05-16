
package cn.edu.xidian.tafei_mall.service.impl;


import cn.edu.xidian.tafei_mall.model.entity.Order;
import cn.edu.xidian.tafei_mall.model.vo.Response.Payment.createPaymentResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Payment.alipayResponse;
import cn.edu.xidian.tafei_mall.service.OrderService;
import cn.edu.xidian.tafei_mall.service.PayService;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;

import java.util.*;
import java.util.concurrent.*;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private Alipay alipay;

    // 定时任务线程池
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    // 存储订单对应的定时任务
    private final ConcurrentMap<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    @Override
    public boolean queryOrderStatus(String orderId) {
        return "paid".equals(orderService.getOrderById(orderId).getStatus());
    }

    @Override
    public createPaymentResponse createPayOrder(String orderId, String userId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("用户不匹配");
        }
        if (order.getStatus().equals("paid")) {
            throw new RuntimeException("订单已支付");
        } else if (order.getStatus().equals("cancelled")) {
            throw new RuntimeException("订单已取消");
        }
        try {
            AlipayTradePagePayResponse response = alipay.pay(order.getOrderId(), order.getTotalAmount().doubleValue(), "测试商品", "FAST_INSTANT_TRADE_PAY");
            scheduleCancelTask(orderId); // 启动超时任务
            return new createPaymentResponse(response.getBody(), alipay.getCHAR_SET());
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public alipayResponse AliPayReturn(Map<String, String> params){
        // 同步验证，仅检查参数是否正确，签名是否无误等
        // 处理支付宝同步返回的参数
        try {
            Map<String, String> result = alipay.notify(params); // 此时支付宝可能还未完成扣款，因此 res 的 TradeStatus=null
            String status = queryOrderStatus(result.get("out_trade_no")) ? "TRADE_SUCCESS" : ""; // 但由于支付宝的延迟，异步验证有可能会比同步验证先到，所以可以提前判断是否扣款成功
            return new alipayResponse(result.get("code"), status, result.get("out_trade_no"), result.get("trade_no"), result.get("total_amount"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public alipayResponse AliPayNotify(Map<String, String> params){
        // 异步验证，告知扣款结果
        // 处理支付宝异步返回的参数
        try {
            Map<String, String> result = alipay.notify(params);
            if ("success".equals(result.get("code"))) {
                // 判断该笔订单是否在商户网站中已经做过处理
                // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                // 如果有做过处理，不执行商户的业务程序
                if("TRADE_SUCCESS".equals(result.get("trade_status"))){ // 付款成功
                    // 注意：
                    // 付款完成后，支付宝系统发送该交易状态通知
                    // 校验订单
                    Order order = orderService.getOrderById(result.get("out_trade_no"));
                    if (order == null) {
                        throw new RuntimeException("订单不存在");
                    }
                    if (order.getStatus().equals("paid")) {
                        throw new RuntimeException("订单已支付");
                    } else if (order.getStatus().equals("cancelled")) {
                        throw new RuntimeException("订单已取消");
                    }
                    if (Math.abs(order.getTotalAmount().doubleValue() - Double.parseDouble(result.get("total_amount"))) > 0.01) {
                        throw new RuntimeException("订单金额不匹配");
                    }
                    orderService.updateOrderStatus(order.getOrderId(), "paid");
                    cancelExistingTask(result.get("out_trade_no")); // 取消对应的超时任务
                    System.out.println("付款成功");
                } else if("TRADE_FINISHED".equals(result.get("trade_status"))){ // 交易已完成
                    //注意：
                    //退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                    System.out.println("交易已完成");
                } else if("TRADE_CLOSED".equals(result.get("trade_status"))){ // 交易已关闭
                    System.out.println("交易已关闭");
                } else{
                    // 其他状态
                    System.out.println("订单状态异常");
                }
            } else {
                // 验证失败
                System.out.println("验证失败");
                System.out.println(result);
            }
            return new alipayResponse(result.get("code"), result.get("trade_status"), result.get("out_trade_no"), result.get("trade_no"), result.get("total_amount"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*----------------------内部方法----------------------*/

    /**
     * 安排订单取消任务
     */
    private void scheduleCancelTask(String orderId) {
        long delay = Integer.parseInt(alipay.getTIMEOUT_EXPRESS().substring(0, alipay.getTIMEOUT_EXPRESS().length() - 1));
        TimeUnit unit;
        switch (alipay.getTIMEOUT_EXPRESS().substring(alipay.getTIMEOUT_EXPRESS().length() - 1)) {
            case "s" -> unit = TimeUnit.SECONDS;
            case "m" -> unit = TimeUnit.MINUTES;
            case "h" -> unit = TimeUnit.HOURS;
            case "d" -> unit = TimeUnit.DAYS;
            default -> throw new RuntimeException("不支持的时间单位");
        }
        // 如果已有旧任务先取消
        cancelExistingTask(orderId);

        ScheduledFuture<?> newTask = scheduler.schedule(() -> {
            try {
                Order currentOrder = orderService.getOrderById(orderId);
                // 使用CAS方式更新订单状态
                if (currentOrder != null && "pending".equals(currentOrder.getStatus())) {
                    orderService.updateOrderStatus(orderId, "canceled");
                    // 订单取消后的其他业务逻辑
                    System.out.println("订单超时自动取消：" + orderId);
                }
            } finally {
                scheduledTasks.remove(orderId);
            }
        }, delay, unit);
        scheduledTasks.put(orderId, newTask);
        System.out.println("订单超时取消任务已安排，订单ID：" + orderId + "，延迟时间：" + delay + unit);
    }

    /**
     * 取消已存在的定时任务
     */
    private void cancelExistingTask(String orderId) {
        ScheduledFuture<?> existingTask = scheduledTasks.get(orderId);
        if (existingTask != null) {
            existingTask.cancel(false);
            scheduledTasks.remove(orderId);
            System.out.println("已取消订单超时任务，订单ID：" + orderId);
        }
    }

    /**
     * 应用关闭时清理资源
     */
    @PreDestroy
    public void destroy() {
        scheduler.shutdownNow();
        scheduledTasks.clear();
    }
}

@Getter
@Component
class Alipay {
    @Value("${api.alipay.app-id}")
    private String APP_ID;
    @Value("${api.alipay.app-private-key}")
    private String APP_PRIVATE_KEY;
    @Value("${api.alipay.alipay-public-key}")
    private String ALIPAY_PUBLIC_KEY;
    @Value("${api.alipay.alipay-payee}")
    private String ALIPAY_PAYEE;
    @Value("${api.alipay.gateway-url}")
    private String GATEWAY_URL;
    @Value("${api.alipay.notify-url}")
    private String NOTIFY_URL;
    @Value("${api.alipay.return-url}")
    private String RETURN_URL;
    @Value("${api.alipay.sign-type}")
    private String SIGN_TYPE;
    @Value("${api.alipay.char-set}")
    private String CHAR_SET;
    @Value("${api.alipay.timeout-express}")
    private String TIMEOUT_EXPRESS;

    public AlipayTradePagePayResponse pay(String outTradeNo, double totalAmount, String subject, String product_code) throws AlipayApiException { // 支付宝支付请求
        // 初始化支付宝客户端服务
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL,APP_ID,APP_PRIVATE_KEY,"json",CHAR_SET,ALIPAY_PUBLIC_KEY,SIGN_TYPE);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(RETURN_URL);
        request.setNotifyUrl(NOTIFY_URL);
        // 打包请求参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("total_amount", totalAmount);
        bizContent.put("subject", subject);
        bizContent.put("product_code", product_code);
        bizContent.put("timeout_express", TIMEOUT_EXPRESS); // 超时时间
        bizContent.put("seller_id", ALIPAY_PAYEE);
        request.setBizContent(bizContent.toString());
        // 发送请求
        return alipayClient.pageExecute(request);
    }

    public Map<String, String> notify(Map<String, String> params) throws Exception { // 支付宝通知
        Map<String, String> result = new HashMap<>();
        if (AlipaySignature.rsaCheckV1(params, ALIPAY_PUBLIC_KEY, CHAR_SET, SIGN_TYPE)) { // 调用SDK验证签名
            result.put("code", "success");
            result.put("trade_status", params.get("trade_status")); //交易状态
            result.put("out_trade_no", params.get("out_trade_no")); // orderId
            result.put("trade_no", params.get("trade_no"));
            result.put("total_amount", params.get("total_amount"));
        } else { // 验签失败
            result.put("code", "failed");
        }
        result.put("method", "alipay");
        return result;
    }
}
