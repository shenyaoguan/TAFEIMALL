package cn.edu.xidian.tafei_mall.service;

import cn.edu.xidian.tafei_mall.model.vo.Response.Review.createReviewResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Review.getReviewResponse;
import cn.edu.xidian.tafei_mall.model.vo.ReviewCreateVO;

public interface ReviewService {
    createReviewResponse createReview(ReviewCreateVO reviewCreateVO, String productId, String userId);
    getReviewResponse getReviewsByProductId(int page, int limit, String productId);
}
