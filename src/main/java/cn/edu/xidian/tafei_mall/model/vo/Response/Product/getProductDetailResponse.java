package cn.edu.xidian.tafei_mall.model.vo.Response.Product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class getProductDetailResponse {
    private final String productId;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final int stock;
    private final boolean isFreeShipping;
    private final String sellerName;
    private final String sellerEmail;
    private final List<String> mainPictures;

    public getProductDetailResponse(String productId, String name, String description, BigDecimal price, int stock, boolean isFreeShipping, String sellerName, String sellerEmail, List<String> mainPictures) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.isFreeShipping = isFreeShipping;
        this.sellerName = sellerName;
        this.sellerEmail = sellerEmail;
        this.mainPictures = mainPictures;
    }
}
