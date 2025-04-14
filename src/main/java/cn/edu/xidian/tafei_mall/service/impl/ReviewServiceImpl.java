package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.ReviewMapper;
import cn.edu.xidian.tafei_mall.model.entity.Review;
import cn.edu.xidian.tafei_mall.service.ReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    /**
     * 添加评论
     * @param review
     */
    @Override
    public void addReview(Review review) {
        reviewMapper.addReview(review);
    }

    /**
     * 分页查找评论
     *
     * @param productId
     * @param page
     * @param limit
     * @return
     */
    @Override
    public Map<String, Object> pageQuery(String productId, int page, int limit) {
        if (page <= 0) {
            page = 1; // 默认从第1页开始
        }
        if (limit <= 0) {
            limit = 10; // 默认每页返回10条记录
        }

        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        if (productId != null && !productId.isEmpty()) {
            queryWrapper.eq(Review::getProductId, productId);
        }

        // 分页查询
        Page<Review> productPage = new Page<>(page, limit);
        Page<Review> resultPage = reviewMapper.selectPage(productPage, queryWrapper);

        // 获取查询结果和总数
        List<Review> reviews = resultPage.getRecords();
        long total;
        total = reviewMapper.selectCount(new LambdaQueryWrapper<>());

        // 构造返回数据
        Map<String, Object> response = new HashMap<>();
        response.put("total", total);  // 返回总记录数
        response.put("results", reviews);  // 返回查询结果
        return response;

    }
}
