package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.ProductMapper;
import cn.edu.xidian.tafei_mall.mapper.PromotionMapper;
import cn.edu.xidian.tafei_mall.mapper.PromotionProductMapper;
import cn.edu.xidian.tafei_mall.model.entity.Promotion;
import cn.edu.xidian.tafei_mall.model.entity.PromotionProduct;
import cn.edu.xidian.tafei_mall.model.vo.PromotionCreateVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Promotion.PromotionListResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Promotion.PromotionProductItem;
import cn.edu.xidian.tafei_mall.model.vo.Response.Promotion.createPromotionResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Promotion.getPromotionResponse;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.edu.xidian.tafei_mall.service.PromotionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.xidian.tafei_mall.model.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


@Slf4j
@Service
public class PromotionServiceImpl extends ServiceImpl<PromotionMapper, Promotion> implements PromotionService {

    @Autowired
    private PromotionMapper promotionMapper;

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private PromotionProductMapper promotionProductMapper;

    @Override
    public createPromotionResponse createPromotion(PromotionCreateVO promotionCreateVO, String userId) {
        try {
            final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            if (promotionCreateVO == null) {
                throw new IllegalArgumentException("促销信息不能为空");
            }

            List<String> productIds = promotionCreateVO.getProductId();
            if (CollectionUtils.isEmpty(productIds)) {
                throw new IllegalArgumentException("必须至少选择一个商品");
            }

            BigDecimal discountRate = promotionCreateVO.getDiscountRate();
            if (discountRate == null) {
                throw new IllegalArgumentException("折扣率不能为空");
            }
            if (discountRate.compareTo(BigDecimal.ZERO) <= 0 ||
                    discountRate.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("折扣率必须大于0且小于等于100");
            }

            if (promotionCreateVO.getEndDate() == null) {
                throw new IllegalArgumentException("结束时间不能为空");
            }
            LocalDateTime startDate = promotionCreateVO.getStartDate() != null ? LocalDateTime.parse(promotionCreateVO.getStartDate(), DATE_TIME_FORMATTER) : LocalDateTime.now();
            LocalDateTime endDate = LocalDateTime.parse(promotionCreateVO.getEndDate(), DATE_TIME_FORMATTER);
            if (startDate.isBefore(LocalDateTime.now())) {
                startDate = LocalDateTime.now();
            }
            if (endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("结束时间不能早于开始时间");
            }

            List<String> NotExistProductIds = new ArrayList<>();
            boolean isExist = true;
            List<String> DoNotHavePermissionProductIds = new ArrayList<>();
            boolean hasPermission = true;
            List<String> onPromotionProducts = new ArrayList<>();
            boolean isOnPromotion = false;
            for (String productId : productIds) {
                Optional<Product> product = productService.getProductById(productId);
                if (product.isEmpty()) { // 商品不存在或已下架
                    NotExistProductIds.add(productId);
                    isExist = false;
                }
                if (!isExist) {
                    continue;
                }
                if (!Objects.equals(product.get().getSellerId(), userId)) { // 没有权限
                    DoNotHavePermissionProductIds.add(productId);
                    hasPermission = false;
                }
                if (!hasPermission) {
                    continue;
                }
                if (!Objects.equals(productService.currentPrice(product.get().getProductId()), product.get().getPrice())) { // 商品已参与其他促销
                    onPromotionProducts.add(productId);
                    isOnPromotion = true;
                }
            }
            if (!isExist) {
                throw new RuntimeException("The following product(s) do not exist or have been removed from the shelf: " + String.join(",", NotExistProductIds));
            }
            if (!hasPermission) {
                throw new RuntimeException("No permission to operate the following products: " + String.join(",", DoNotHavePermissionProductIds));
            }
            if (isOnPromotion) {
                throw new RuntimeException("The following products have already been included in other promotions: " + String.join(",", onPromotionProducts));
            }

            // 创建促销信息
            Promotion promotion = new Promotion();
            promotion.setPromotionId(UUID.randomUUID().toString());
            promotion.setStartDate(startDate);
            promotion.setEndDate(endDate);
            promotion.setIsActive(true);
            promotionMapper.insert(promotion);

            // 保存促销商品对照信息
            for (String productId : productIds) {
                PromotionProduct promotionProduct = new PromotionProduct();
                promotionProduct.setPromotionId(promotion.getPromotionId());
                promotionProduct.setProductId(productId);
                promotionProduct.setDiscountRate(discountRate);
                promotionProductMapper.insert(promotionProduct);
            }
            return new createPromotionResponse(promotion.getPromotionId());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("日期格式不正确，应为YYYY-MM-DD hh:mm");
        }
    }


    @Override
    public getPromotionResponse getPromotionById(String productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isEmpty()) {
            throw new RuntimeException("商品不存在或已下架");
        }
        List<PromotionProduct> promotionProducts = promotionProductMapper.selectList(new QueryWrapper<PromotionProduct>().eq("product_id", productId));
        if (promotionProducts == null || Objects.equals(productService.currentPrice(product.get().getProductId()), product.get().getPrice())) {
            throw new RuntimeException("该商品未参与促销");
        }

        for (PromotionProduct promotionProduct : promotionProducts) {
            Promotion promotion = promotionMapper.selectById(promotionProduct.getPromotionId());
            if (promotion == null || !promotion.getIsActive()) {
                continue;
            }
            if (promotion.getStartDate().isBefore(LocalDateTime.now()) && promotion.getEndDate().isAfter(LocalDateTime.now())) {
                return new getPromotionResponse(
                        promotion.getPromotionId(),
                        promotionProduct.getDiscountRate(),
                        product.get().getPrice(),
                        productService.currentPrice(product.get().getProductId()),
                        promotion.getEndDate()
                );
            }
        }
        throw new RuntimeException("未知错误");
    }

    @Override
    public PromotionListResponse getActivePromotions(int page, int limit) {
        page = page > 0 ? page : 1;
        limit = limit > 0 ? limit : 10;
        List<PromotionProduct> promotionProducts = promotionProductMapper.selectActivePromotionsWithStock(new Page<>(page, limit), LocalDateTime.now()).getRecords();

        if (promotionProducts == null || promotionProducts.isEmpty()) {
            return new PromotionListResponse(0, Collections.emptyList());
        }

        List<PromotionProductItem> promotionProductItems = new ArrayList<>();
        for (PromotionProduct promotionProduct : promotionProducts) {
            Product product = productMapper.selectById(promotionProduct.getProductId());
            Promotion promotion = promotionMapper.selectById(promotionProduct.getPromotionId());
            promotionProductItems.add(new PromotionProductItem(product.getProductId(), product.getName(), promotionProduct.getDiscountRate(), productService.currentPrice(product.getProductId()), promotion.getEndDate()));
        }
        return new PromotionListResponse(promotionProductItems.size(), promotionProductItems);
    }

}