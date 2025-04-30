package cn.edu.xidian.tafei_mall.controller;


import cn.edu.xidian.tafei_mall.model.entity.User;
import cn.edu.xidian.tafei_mall.service.PayService;
import cn.edu.xidian.tafei_mall.service.UserService;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/payment")
public class PayController {
    // 这里是支付相关的接口
    // 例如：创建订单、查询订单状态等


    @Autowired
    private UserService userService;

    @Autowired
    private PayService payService;
    // 示例接口
    @PostMapping("/{orderId}")
    public ResponseEntity<?> createOrder(@RequestHeader("Session-Id") String sessionId,
                                         @PathVariable("orderId") String orderId){
        // 处理创建订单的逻辑
        // 这里可以调用订单服务来创建订单
        // 假设创建订单成功，返回201 Created
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
        // 这里可以调用订单服务来创建订单
        payService.createPayOrder(orderId, user.getUserId());
        // 假设创建订单成功，返回201 Created
        return ResponseEntity.created(URI.create("")).build();
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
        // 这里可以调用订单服务来查询订单状态
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
}
