package cn.edu.xidian.tafei_mall.controller;


import cn.edu.xidian.tafei_mall.service.OrderItemService;
import cn.edu.xidian.tafei_mall.service.OrderService;
import io.swagger.annotations.ApiKeyAuthDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@ApiKeyAuthDefinition(key = "Session-ID", in = ApiKeyAuthDefinition.ApiKeyLocation.HEADER, name = "Session-ID")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/")
    public ResponseEntity<?> searchOrder() {
        return ResponseEntity.ok("");
    }

    @PostMapping("/{cartId}")
    public ResponseEntity<?> createOrder(@PathVariable String cartId) {
        String orderId = "";

        return ResponseEntity.created(URI.create(""))
                .body(orderId);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable String orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
