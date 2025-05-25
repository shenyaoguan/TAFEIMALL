package cn.edu.xidian.tafei_mall.controller.v2;


import cn.edu.xidian.tafei_mall.model.entity.User;
import cn.edu.xidian.tafei_mall.model.vo.Response.Order.MessageResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Payment.alipayResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Payment.createPaymentResponse;
import cn.edu.xidian.tafei_mall.service.PayService;
import cn.edu.xidian.tafei_mall.service.UserService;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/payment")
public class PayControllerV2 {
    @Autowired
    private UserService userService;

    @Autowired
    private PayService payService;

    @Value("${web.web-url}")
    private String webUrl;

    @PostMapping("/{orderId}")
    public ResponseEntity<?> createOrder(@RequestHeader("Session-Id") String sessionId,
                                         @PathVariable("orderId") String orderId,
                                         HttpServletResponse httpResponse){
        try {
            if (orderId == null || orderId.isEmpty()) {
                return ResponseEntity.badRequest().body("订单ID不能为空");
            }
            if (sessionId == null) {
                return ResponseEntity.status(401).body("未登录");
            }
            User user = userService.getUserInfo(sessionId);
            if (user == null) {
                return ResponseEntity.status(401).body("用户不存在");
            }
            // 创建支付订单
            createPaymentResponse response = payService.createPayOrder(orderId, user.getUserId());
            // 直接将html表单（支付宝官方支付页面）返回给浏览器
            httpResponse.setContentType("text/html;charset=" + response.getCharset());
            httpResponse.getWriter().write(response.getForm());
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> queryOrderStatus( @RequestHeader("Session-Id")String sessionId,
                                              @PathVariable("orderId") String orderId){
        if (orderId == null || orderId.isEmpty()) {
            return ResponseEntity.badRequest().body("订单ID不能为空");
        }
        if (sessionId == null) {
            return ResponseEntity.status(401).body("未登录");
        }
        if (userService.getUserInfo(sessionId) == null) {
            return ResponseEntity.status(401).body("用户不存在");
        }
        // 查询订单状态
        try {
            boolean flag = payService.queryOrderStatus(orderId);
            if (flag) {
                return ResponseEntity.ok().body(JSONUtil.createObj().set("status", "success"));
            } else {
                return ResponseEntity.accepted().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/return")
    public void returnUrl(HttpServletRequest request, HttpServletResponse httpResponse) throws Exception {
        httpResponse.setContentType("text/html;charset=UTF-8");
        try{
            alipayResponse res = payService.AliPayReturn(getRequestInfo(request));
            if(res.getCode().equals("success")){
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                int flag = res.getTradeStatus().equals("TRADE_SUCCESS") ? 2 : 1;
                httpResponse.getWriter().write(returnPage(webUrl, flag));
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                httpResponse.getWriter().write(returnPage(webUrl, 0));
            }
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.getWriter().write(returnPage(webUrl, -1));
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        }
    }

    @PostMapping("/notify")
    public void notifyUrl(HttpServletRequest request) {
        try{
            alipayResponse res = payService.AliPayNotify(getRequestInfo(request));
            System.out.println("INFO: TradeStatus="+ res.getTradeStatus() + ", TradeNo=" + res.getTradeNo() + ", OutTradeNo=" + res.getOutTradeNo() + ", TotalAmount=" + res.getTotalAmount());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    public static Map<String, String> getRequestInfo(HttpServletRequest request){ // 处理反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    private static String returnPage(String redirectUrl, int flag){
        String result = switch (flag) {
            case 0 -> "购买失败，请稍后再试";
            case 1 -> "等待扣款结果";
            case 2 -> "购买成功，感谢您的惠顾";
            case -1 -> "发生错误，若果您已支付，请联系客服";
            default -> "未知错误";
        };
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <style>" +
                "        body {" +
                "            background-color: white;" +
                "            display: flex;" +
                "            justify-content: center;" +
                "            align-items: center;" +
                "            height: 100vh;" +
                "            margin: 0;" +
                "            font-family: Arial, sans-serif;" +
                "        }" +
                "        .message-box {" +
                "            background-color: #faf0e6;" + // 方框颜色
                "            padding: 2rem;" +
                "            border-radius: 8px;" +
                "            text-align: center;" +
                "            color: #d86f6f;" +   // 字体颜色
                "        }" +
                "        a {" +
                "            text-decoration: underline;" +
                "            color: #ff99cc;" +  // 链接颜色
                "            cursor: pointer;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=\"message-box\">" +
                "        <p>" + result + "，<span id=\"countdown\">3</span>秒后返回首页</p>" +
                "        <p>如果超时未返回，请<a href=\"" + redirectUrl + "\">点击此处</a>跳转页面</p>" +
                "    </div>" +
                "    <script>" +
                "        let seconds = 3;" +
                "        const countdownElement = document.getElementById('countdown');" +
                "        const timer = setInterval(() => {" +
                "            seconds--;" +
                "            countdownElement.textContent = seconds;" +
                "            if (seconds <= 0) {" +
                "                clearInterval(timer);" +
                "                window.location.href = \"" + redirectUrl + "\";" +
                "            }" +
                "        }, 1000);" +
                "    </script>" +
                "</body>" +
                "</html>";
    }

    // 后端测试支付用
    @GetMapping("/test/pay")
    public ResponseEntity<?> createOrderTest(HttpServletResponse httpResponse){
        try {
            // 这里可以调用订单服务来创建订单（手动填写 OrderId 和 UserId）
            createPaymentResponse response = payService.createPayOrder("fc0246b3-97b5-4636-8a85-b53c95fa9da2", "92fca2bc-c9b4-477b-8c06-a82576ddadc5");
            // 直接将html表单（支付宝官方支付页面）返回给浏览器
            httpResponse.setContentType("text/html;charset=" + response.getCharset());
            httpResponse.getWriter().write(response.getForm());
            httpResponse.getWriter().flush();
            httpResponse.getWriter().close();
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    // 比较好的想法，可惜这次估计用不上了，我想把它留下来
    @GetMapping("/test/return")
    public void returnUrlTest(HttpServletRequest request, HttpServletResponse httpResponse) throws Exception {
        String redirectUrl = "http://localhost:5173/callback";
        try{
            alipayResponse res = payService.AliPayReturn(getRequestInfo(request));
            if (res.getCode().equals("success")) {
                // 此时支付宝可能还未完成扣款，因此 res 的 TradeStatus=null
                // 在浏览器打开这个链接时，前端应显示等待页面，同时向后端查询支付状态，确认支付成功后，应跳转到成功页面
                httpResponse.setStatus(HttpServletResponse.SC_OK);
                redirectUrl += "/waiting?"
                        + "tradeNo=" + URLEncoder.encode(res.getTradeNo(), StandardCharsets.UTF_8)
                        + "&outTradeNo=" + URLEncoder.encode(res.getOutTradeNo(), StandardCharsets.UTF_8)
                        + "&amount=" + URLEncoder.encode(res.getTotalAmount(), StandardCharsets.UTF_8);
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                redirectUrl += "/failed";
            }
            httpResponse.sendRedirect(redirectUrl);
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            redirectUrl += "/error?=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            httpResponse.sendRedirect(redirectUrl);
        }
    }

}
