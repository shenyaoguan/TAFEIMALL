package cn.edu.xidian.tafei_mall.controller;

import cn.edu.xidian.tafei_mall.model.entity.Product;
import cn.edu.xidian.tafei_mall.model.entity.Review;
import cn.edu.xidian.tafei_mall.model.vo.*;
import cn.edu.xidian.tafei_mall.model.vo.Response.Seller.addReviewResponse;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.edu.xidian.tafei_mall.service.ReviewService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


/**
 *  <p>
 *  这里对应的是/api/products路径，用于处理商品的搜索和详情请求
 * </p>
 *
 * @auther: shenyaoguan
 *
 * @date: 2025-03-17
 *
 */

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;


    /**
     * 商品搜索接口
     * @param keyword  搜索关键字（为空时返回所有商品）
     * @param page     页码（默认1）
     * @param limit    每页数量（默认10）
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> searchResult = productService.searchProducts(keyword, page, limit);
        return ResponseEntity.ok(searchResult);
    }

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情
     */
    @GetMapping("/{productId}")
    public ResponseEntity<?> getProductDetails(@PathVariable String productId) {
        Optional<Product> product = productService.getProductById(productId);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 提交商品评论
     * @param productId
     * @return
     */
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<addReviewResponse> addReview(@PathVariable String productId,
                                                       @RequestHeader("Session-Id") String sessionId,
                                                       @RequestBody ReviewVO reviewVO) {
//        log.info("添加评论：{}", reviewVO);

        String reviewId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Review review = new Review();
        BeanUtils.copyProperties(reviewVO, review);
        review.setProductId(productId);
        review.setReviewId(reviewId);
        review.setCreatedAt(now);
        review.setUpdatedAt(now);

        reviewService.addReview(review);
        return ResponseEntity.ok(new addReviewResponse(reviewId, "评论成功"));
    }


    @GetMapping("/{productId}/reviews")
    public ResponseEntity<?> getReview(@PathVariable String productId,
                                       @RequestParam(required = false, defaultValue = "1") int page,
                                       @RequestParam(required = false, defaultValue = "10") int limit){
        Map<String, Object> reviews = reviewService.pageQuery(productId, page, limit);
        return ResponseEntity.ok(reviews);
    }
}