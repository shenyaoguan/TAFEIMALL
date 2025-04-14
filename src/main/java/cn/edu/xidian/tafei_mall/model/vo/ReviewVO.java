package cn.edu.xidian.tafei_mall.model.vo;

import lombok.Data;

@Data
public class ReviewVO {
    //订单号
    String orderId;
    //评分
    Integer rating;
    //评价内容
    String comment;
}
