package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.OrderItemMapper;
import cn.edu.xidian.tafei_mall.mapper.OrderMapper;
import cn.edu.xidian.tafei_mall.mapper.ReviewMapper;
import cn.edu.xidian.tafei_mall.model.entity.Order;
import cn.edu.xidian.tafei_mall.model.entity.OrderItem;
import cn.edu.xidian.tafei_mall.model.entity.Review;
import cn.edu.xidian.tafei_mall.model.vo.Response.Review.ReviewResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Review.createReviewResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Review.getReviewResponse;
import cn.edu.xidian.tafei_mall.model.vo.ReviewCreateVO;
import cn.edu.xidian.tafei_mall.service.ReviewService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {
    @Autowired
    private ReviewMapper reviewMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public createReviewResponse createReview(ReviewCreateVO reviewCreateVO, String productId, String userId) {
        Order order = orderMapper.selectById(reviewCreateVO.getOrderId());
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        if (!order.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to current user");
        }
        if (!Objects.equals(order.getStatus(), "finished")) {
            throw new RuntimeException("Order must be finished");
        }
        List<OrderItem> orderItems = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getOrderId()));
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("No order items found for this order");
        }
        for (OrderItem orderItem : orderItems) {
            if (orderItem.getProductId().equals(productId)) {
                // 检查订单项状态，等支持此功能后，"检查是否已经评价"可以删除
                /*
                if (!orderItem.getStatus().equals("review_wait")) {
                    throw new RuntimeException("Order item is not delivered yet");
                }
                */
                // 检查是否已经评价
                LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Review::getProductId, productId)
                            .eq(Review::getOrderId, order.getOrderId());
                Long count = reviewMapper.selectCount(queryWrapper);
                if (count > 0) {
                    // throw new RuntimeException("You have already reviewed this product");
                    if (count > 1) {
                        throw new RuntimeException("You have already reviewed this product");
                    }
                    Review review = reviewMapper.selectOne(queryWrapper);
                    review.setRating(reviewCreateVO.getRating());
                    review.setComment(reviewCreateVO.getComment());
                    review.setUpdatedAt(LocalDateTime.now());
                    reviewMapper.updateById(review);
                    return new createReviewResponse(review.getReviewId(), "修改成功");
                }
                // 创建评价
                Review review = new Review();
                review.setReviewId(UUID.randomUUID().toString());
                review.setProductId(productId);
                review.setOrderId(reviewCreateVO.getOrderId());
                review.setRating(reviewCreateVO.getRating());
                review.setComment(reviewCreateVO.getComment());
                review.setCreatedAt(LocalDateTime.now());
                review.setUpdatedAt(LocalDateTime.now());
                reviewMapper.insert(review);
                /* 更新订单项状态
                orderItem.setStatus("review_done");
                orderItem.setUpdatedAt(LocalDateTime.now());
                orderItemMapper.updateById(orderItem);
                */
                return new createReviewResponse(review.getReviewId(), "评价成功");
            }
        }
        throw new RuntimeException("The product is not in this order");
    }

    @Override
    public getReviewResponse getReviewsByProductId(int page, int limit, String productId) {
        if (page <= 0) {
            page = 1; // 默认从第1页开始
        }
        if (limit <= 0) {
            limit = 10; // 默认每页返回10条记录
        }
        LambdaQueryWrapper<Review> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Review::getProductId, productId); // 根据 product_id 查询

        // 分页查询
        Page<Review> reviewPage = new Page<>(page, limit);
        Page<Review> resultPage = reviewMapper.selectPage(reviewPage, queryWrapper);

        // 获取查询结果和总数
        List<Review> reviews = resultPage.getRecords();

        long total = resultPage.getTotal(); // 总记录数
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for (Review review : reviews) {
            reviewResponses.add(new ReviewResponse(review.getReviewId(), review.getRating(), review.getComment(), review.getCreatedAt().toString()));
        }

        return new getReviewResponse(total, reviewResponses);
    }
}
