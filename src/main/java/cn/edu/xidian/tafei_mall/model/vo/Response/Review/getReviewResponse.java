package cn.edu.xidian.tafei_mall.model.vo.Response.Review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class getReviewResponse {
    private final long total;
    private final List<ReviewResponse> reviews;

    public getReviewResponse(long total, List<ReviewResponse> reviews) {
        this.total = total;
        this.reviews = reviews;
    }
}
