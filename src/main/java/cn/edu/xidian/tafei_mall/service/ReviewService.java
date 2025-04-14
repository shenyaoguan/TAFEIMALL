package cn.edu.xidian.tafei_mall.service;

import cn.edu.xidian.tafei_mall.model.entity.Review;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface ReviewService extends IService<Review> {
    void addReview(Review review);

    Map<String, Object> pageQuery(String productId, int page, int limit);
}
