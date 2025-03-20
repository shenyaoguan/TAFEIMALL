package cn.edu.xidian.tafei_mall.service;

import cn.edu.xidian.tafei_mall.model.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.zip.DataFormatException;
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

    String addProduct(Product product) throws DataFormatException;

    /*
    * 查找商品
    * */
    Map<String, Object> searchProducts(String keyword, int page, int limit);
    Optional<Product> getProductById(String productId);
}
