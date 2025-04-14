package cn.edu.xidian.tafei_mall.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopProductVO {

    private String productId;
    private String name;
    private Integer salesCount;

}
