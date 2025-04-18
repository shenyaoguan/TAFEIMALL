package cn.edu.xidian.tafei_mall.model.vo.Response.Report;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TopProductResponse{
    private final String productId;
    private final String name;
    private final int salesCount;

    public TopProductResponse(String productId, String name, int salesCount) {
        this.productId = productId;
        this.name = name;
        this.salesCount = salesCount;
    }
}
