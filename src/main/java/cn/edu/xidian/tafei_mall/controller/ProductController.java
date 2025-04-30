package cn.edu.xidian.tafei_mall.controller;

import cn.edu.xidian.tafei_mall.model.entity.Product;
import cn.edu.xidian.tafei_mall.model.entity.Review;
import cn.edu.xidian.tafei_mall.model.entity.User;
import cn.edu.xidian.tafei_mall.model.vo.*;
import cn.edu.xidian.tafei_mall.model.vo.Response.Order.MessageResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Promotion.getPromotionResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Review.createReviewResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Review.getReviewResponse;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.edu.xidian.tafei_mall.service.PromotionService;
import cn.edu.xidian.tafei_mall.service.ReviewService;
import cn.edu.xidian.tafei_mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;


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
@RequestMapping("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;
    @Autowired
    private PromotionService promotionService;
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
     * 添加评论
     * @param sessionId Session ID
     * @param productId 商品ID
     * @return 是否成功
     */
    @PostMapping("/{productId}/reviews")
    public ResponseEntity<?> addReview(@RequestHeader("Session-Id") String sessionId,
                                       @PathVariable String productId,
                                       @RequestBody ReviewCreateVO reviewCreateVO) {
        try {
            if (sessionId == null) {
                return new ResponseEntity<>(new MessageResponse("未登录"), HttpStatus.UNAUTHORIZED);
            }
            User user = userService.getUserInfo(sessionId);
            if (user == null) {
                return new ResponseEntity<>(new MessageResponse("用户不存在"), HttpStatus.UNAUTHORIZED);
            }
            createReviewResponse res = reviewService.createReview(reviewCreateVO, productId, user.getUserId());
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取商品的评论
     * @param productId 商品ID
     * @param page      页码（默认1）
     * @param limit     每页数量（默认10）
     * @return 商品评论
     */
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable String productId,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int limit) {
        try {
            getReviewResponse res = reviewService.getReviewsByProductId(page, limit, productId);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{productId}/promotion")
    public ResponseEntity<?> getPromotion(@PathVariable String productId){
        try {
            getPromotionResponse response = promotionService.getPromotionById(productId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}