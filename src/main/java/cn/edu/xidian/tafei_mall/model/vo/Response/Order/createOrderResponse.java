package cn.edu.xidian.tafei_mall.model.vo.Response.Order;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class createOrderResponse {
    private final List<String> orderIds;

    public createOrderResponse(List<String> orderIds) {
        this.orderIds = orderIds;
    }
}
