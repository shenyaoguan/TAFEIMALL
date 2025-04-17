package cn.edu.xidian.tafei_mall.model.vo.Response.Seller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class addReviewResponse {
    private final String reviewId;
    private final String message;

    public addReviewResponse(String reviewId, String message){
        this.reviewId = reviewId;
        this.message = message;
    }

}
