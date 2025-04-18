package cn.edu.xidian.tafei_mall.model.vo.Response.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class
getOrderResponse {
    private final List<OrderResponse> orders;

    public getOrderResponse(List<OrderResponse> orders) {
        this.orders = orders;
    }
}
