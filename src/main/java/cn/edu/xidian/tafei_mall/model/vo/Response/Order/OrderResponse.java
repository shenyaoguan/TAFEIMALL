package cn.edu.xidian.tafei_mall.model.vo.Response.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private final String orderId;
    private final String status;
    private final String userName;
    private final String sellerName;
    private final String city;
    private final String address;
    private final String postalCode;
    private final String trackingNumber;
    private final getOrderItemResponse items;

    public OrderResponse(String orderId, String status, String userName, String sellerName, String city, String address, String postalCode, String trackingNumber, getOrderItemResponse items) {
        this.orderId = orderId;
        this.status = status;
        this.userName = userName;
        this.sellerName = sellerName;
        this.city = city;
        this.address = address;
        this.postalCode = postalCode;
        this.trackingNumber = trackingNumber;
        this.items = items;
    }
}
