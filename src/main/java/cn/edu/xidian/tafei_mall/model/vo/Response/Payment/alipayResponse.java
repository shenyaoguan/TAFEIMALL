package cn.edu.xidian.tafei_mall.model.vo.Response.Payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class alipayResponse {
    private final String code;
    private final String tradeStatus;
    private final String outTradeNo;
    private final String tradeNo;
    private final String totalAmount;

    public alipayResponse(String code, String tradeStatus, String outTradeNo, String tradeNo, String totalAmount) {
        this.code = code;
        this.tradeStatus = tradeStatus;
        this.outTradeNo = outTradeNo;
        this.tradeNo = tradeNo;
        this.totalAmount = totalAmount;
    }
}
