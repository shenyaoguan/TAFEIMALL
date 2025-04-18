package cn.edu.xidian.tafei_mall.service;

import cn.edu.xidian.tafei_mall.model.vo.PromotionCreateVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Promotion.PromotionListResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Promotion.createPromotionResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Promotion.getPromotionResponse;

public interface PromotionService {
    createPromotionResponse createPromotion(PromotionCreateVO promotionCreateVO, String userId);
    getPromotionResponse getPromotionById(String productId);
    PromotionListResponse getActivePromotions(int page, int limit);
}
