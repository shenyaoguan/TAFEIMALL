
package cn.edu.xidian.tafei_mall.service;


import cn.edu.xidian.tafei_mall.model.vo.Response.Payment.createPaymentResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Payment.alipayResponse;

import java.util.Map;

public interface PayService{
    boolean queryOrderStatus(String orderId);
    createPaymentResponse createPayOrder(String orderId, String userId);
    alipayResponse AliPayReturn(Map<String, String> params);
    alipayResponse AliPayNotify(Map<String, String> params);
}

