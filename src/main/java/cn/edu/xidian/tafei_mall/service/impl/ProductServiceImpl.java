package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.ImageMapper;
import cn.edu.xidian.tafei_mall.mapper.PromotionMapper;
import cn.edu.xidian.tafei_mall.mapper.PromotionProductMapper;
import cn.edu.xidian.tafei_mall.model.entity.Image;
import cn.edu.xidian.tafei_mall.model.entity.Product;
import cn.edu.xidian.tafei_mall.mapper.ProductMapper;
import cn.edu.xidian.tafei_mall.model.entity.Promotion;
import cn.edu.xidian.tafei_mall.model.entity.PromotionProduct;
import cn.edu.xidian.tafei_mall.model.vo.ProductSimpleVO;
import cn.edu.xidian.tafei_mall.model.vo.ProductVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Seller.getProductResponse;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author shenyaoguan
 *
 * @since 2025-03-17
 */
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Resource
    private ImageMapper imageMapper;

    @Resource
    private PromotionProductMapper promotionProductMapper;

    @Resource
    private PromotionMapper promotionMapper;

  @Override
  public Map<String, Object> searchProducts(String keyword, int page, int limit) {
      if (page <= 0) page = 1;
      if (limit <= 0) limit = 10;

      LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
      if (keyword != null && !keyword.trim().isEmpty()) {
          queryWrapper.like(Product::getName, keyword);
      }

      Page<Product> productPage = new Page<>(page, limit);
      Page<Product> resultPage = productMapper.selectPage(productPage, queryWrapper);

      List<Product> products = resultPage.getRecords();
      long total;

      if (keyword == null || keyword.trim().isEmpty()) {
          total = productMapper.selectCount(new LambdaQueryWrapper<>());
      } else {
          LambdaQueryWrapper<Product> countQueryWrapper = new LambdaQueryWrapper<>();
          countQueryWrapper.like(Product::getName, keyword);
          total = productMapper.selectCount(countQueryWrapper);
      }

      // 转换为 VO 列表
      List<ProductSimpleVO> productVOList = products.stream().map(product -> {
          ProductSimpleVO vo = new ProductSimpleVO();
          vo.setProductId(product.getProductId());
          vo.setName(product.getName());
          vo.setPrice(product.getPrice());

          // 查询图片
          List<String> imageUrls = imageMapper.selectList(
                          new LambdaQueryWrapper<Image>()
                                  .eq(Image::getProductId, product.getProductId())
                  ).stream()
                  .map(Image::getImagePath)
                  .toList();

          // 设置缩略图为第一张图片
          if (!imageUrls.isEmpty()) {
              vo.setThumbnail(imageUrls.get(0));
          }

          return vo;
      }).collect(Collectors.toList());

      // 构造响应
      Map<String, Object> response = new HashMap<>();
      response.put("total", total);
      response.put("results", productVOList);
      return response;
  }



    /**
     * 根据 ID 获取商品详情
     * @param productId 商品ID
     * @return 商品信息
     */
    @Override
    public Optional<Product> getProductById(String productId) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            // 查询图片
            LambdaQueryWrapper<Image> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Image::getProductId, productId);
            List<Image> imageList = imageMapper.selectList(queryWrapper);

            // 提取图片URL
            List<String> imageUrls = imageList.stream()
                    .map(Image::getImagePath)
                    .collect(Collectors.toList());

            // 设置进 product 对象
            product.setMainPictures(imageUrls);
        }
        return Optional.ofNullable(product);
    }


    /**
     * 添加商品
     * @param productVO 商品信息
     * @return 商品ID
     */
    @Override
    public String addProduct(ProductVO productVO, String userId) {
        UUID uuid = UUID.randomUUID();
        Product product = BeanUtil.toBean(productVO, Product.class);
        product.setProductId(uuid.toString());
        product.setSellerId(userId);
        productMapper.insert(product);
//        save(product);
        return uuid.toString();
    }

    /**
     * 更新商品
     *
     * @param productVO 商品信息
     * @param productId 商品ID
     * @param userId    用户ID
     * @return 是否更新成功
     */
    @Override
    public Product updateProduct(ProductVO productVO, String productId, String userId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return null;
        }
        BeanUtil.copyProperties(productVO, product);
        productMapper.updateById(product);
        return product;
    }

    /**
     * 删除商品
     *
     * @param productId 商品ID
     * @param userId    用户ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteProduct(String productId, String userId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return false;
        }
        productMapper.deleteById(productId);
        return true;
    }

    /**
     * 获取商品列表
     *
     * @param userId 用户ID
     * @return 商品列表
     */
    @Override
    public getProductResponse getProduct(String userId) {
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>().eq(Product::getSellerId, userId));
        return new getProductResponse(products);
    }

    @Override
    public BigDecimal currentPrice(String productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return BigDecimal.ZERO;
        }

        List<PromotionProduct> promotionProducts = promotionProductMapper.selectList(new QueryWrapper<PromotionProduct>().eq("product_id", productId));
        if (promotionProducts != null && !promotionProducts.isEmpty()) {
            // 如果有促销活动，返回促销价格
            for (PromotionProduct promotionProduct : promotionProducts) {
                Promotion promotion = promotionMapper.selectById(promotionProduct.getPromotionId());
                if (promotion != null) {
                    // 促销活动有效
                    if (promotion.getIsActive() && promotion.getStartDate().isBefore(LocalDateTime.now()) && promotion.getEndDate().isAfter(LocalDateTime.now())) {
                        // 计算折扣价格
                        return product.getPrice().multiply(BigDecimal.valueOf(100).subtract(promotionProduct.getDiscountRate()).multiply(BigDecimal.valueOf(0.01)));
                    }
                }
            }
        }

        return product.getPrice();
    }
}
