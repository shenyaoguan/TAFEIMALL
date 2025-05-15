package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.FavoriteMapper;

import cn.edu.xidian.tafei_mall.model.entity.Favorite;
import cn.edu.xidian.tafei_mall.model.entity.Product;
import cn.edu.xidian.tafei_mall.model.entity.User;
import cn.edu.xidian.tafei_mall.model.vo.FavoriteAddVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Favorite.getFavoritesResponse;
import cn.edu.xidian.tafei_mall.service.FavoriteService;
import cn.edu.xidian.tafei_mall.service.ImageService;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.edu.xidian.tafei_mall.service.UserService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>
 * 收藏表 服务实现类
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Service
@ConditionalOnProperty(name = "features.favorite.enabled", havingValue = "true", matchIfMissing = true)
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Override
    public void addFavorite(FavoriteAddVO favoriteAddVO, String sessionId) {
        User user = userService.getUserInfo(sessionId);
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }

        // 检查商品是否存在
        Optional<Product> productOpt = productService.getProductById(favoriteAddVO.getProductId());
        if (productOpt.isEmpty()) {
            throw new RuntimeException("商品不存在");
        }

        // 检查是否已经收藏过
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getUserId())
                .eq("product_id", favoriteAddVO.getProductId());
        Favorite existingFavorite = favoriteMapper.selectOne(queryWrapper);

        if (existingFavorite != null) {
            throw new RuntimeException("已经收藏过该商品");
        }

        // 添加收藏
        Favorite favorite = new Favorite();
        favorite.setFavoriteId(String.valueOf(UUID.randomUUID()));
        favorite.setUserId(user.getUserId());
        favorite.setProductId(favoriteAddVO.getProductId());
        favorite.setCreatedAt(LocalDateTime.now());
        favorite.setUpdatedAt(LocalDateTime.now());

        favoriteMapper.insert(favorite);
    }

    @Override
    public void removeFavorite(FavoriteAddVO favoriteAddVO, String sessionId) {
        User user = userService.getUserInfo(sessionId);
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }

        // 删除收藏
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getUserId())
                .eq("product_id", favoriteAddVO.getProductId());

        favoriteMapper.delete(queryWrapper);
    }

    @Override
    public getFavoritesResponse getFavorites(String sessionId, Integer page, Integer limit) {
        User user = userService.getUserInfo(sessionId);
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }

        // 默认分页参数
        page = (page == null || page < 1) ? 1 : page;
        limit = (limit == null || limit < 1) ? 10 : limit;

        // 分页查询收藏
        Page<Favorite> favoritePages = new Page<>(page, limit);
        QueryWrapper<Favorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getUserId())
                .orderByDesc("created_at");

        Page<Favorite> favoritePage = favoriteMapper.selectPage(favoritePages, queryWrapper);
        List<Favorite> favorites = favoritePage.getRecords();

        // 构建响应
        getFavoritesResponse response = new getFavoritesResponse();
        response.setTotal((int) favoritePage.getTotal());

        for (Favorite favorite : favorites) {
            Optional<Product> productOpt = productService.getProductById(favorite.getProductId());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                String thumbnail = ""; // 获取商品缩略图，暂时留空
                response.addFavorite(
                        product.getProductId(),
                        product.getName(),
                        productService.currentPrice(product.getProductId()).doubleValue(),
                        thumbnail);
            }
        }

        return response;
    }
}

