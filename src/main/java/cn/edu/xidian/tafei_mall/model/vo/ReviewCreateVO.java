package cn.edu.xidian.tafei_mall.model.vo;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class ReviewCreateVO {
    private String orderId;
    private int rating;
    private String comment;
}
