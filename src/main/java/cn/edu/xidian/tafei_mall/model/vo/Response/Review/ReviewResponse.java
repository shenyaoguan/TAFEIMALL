package cn.edu.xidian.tafei_mall.model.vo.Response.Review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {
    private final String reviewId;
    private final int rating;
    private final String comment;
    private final String createdAt;

    public ReviewResponse(String reviewId, int rating, String comment, String createdAt) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }
}
