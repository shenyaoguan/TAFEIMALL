package cn.edu.xidian.tafei_mall.model.vo.Response.Payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class createPaymentResponse {
    private final String form;
    private final String charset;

    public createPaymentResponse(String form, String charset) {
        this.form = form;
        this.charset = charset;
    }
}
