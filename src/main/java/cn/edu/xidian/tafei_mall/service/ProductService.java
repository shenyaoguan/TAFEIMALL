package cn.edu.xidian.tafei_mall.service;

import cn.edu.xidian.tafei_mall.model.entity.Product;
import cn.edu.xidian.tafei_mall.model.vo.ProductVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Seller.getProductResponse;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
public interface ProductService extends IService<Product> {


    /*
    * 查找商品
    * */
    Map<String, Object> searchProducts(String keyword, int page, int limit);
    Optional<Product> getProductById(String productId);
    String addProduct(ProductVO productVO, String userId);

    Product updateProduct(ProductVO productVO, String productId, String userId);
    boolean deleteProduct(String productId, String userId);

    getProductResponse getProduct(String userId);

    // Server层中，同层调用，不需要暴露给上层
    BigDecimal currentPrice(String productId);
}
