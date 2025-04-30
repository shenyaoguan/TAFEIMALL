
package cn.edu.xidian.tafei_mall.service;


public interface PayService{
    boolean queryOrderStatus(String orderId);

    void createPayOrder(String orderId, String userId);

    String generatePaymentInfo(String orderId);//生成支付信息,但因强制成功不实际支付,没有展示支付页面的必要
}

