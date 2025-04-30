package cn.edu.xidian.tafei_mall.service;

import cn.edu.xidian.tafei_mall.model.entity.Favorite;
import cn.edu.xidian.tafei_mall.model.vo.FavoriteAddVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Favorite.getFavoritesResponse;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 收藏表 服务类
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 添加收藏
     *
     * @param favoriteAddVO 收藏商品
     * @param sessionId     会话ID
     */
    void addFavorite(FavoriteAddVO favoriteAddVO, String sessionId);

    /**
     * 删除收藏
     *
     * @param favoriteAddVO 收藏商品
     * @param sessionId     会话ID
     */
    void removeFavorite(FavoriteAddVO favoriteAddVO, String sessionId);

    /**
     * 获取收藏列表
     *
     * @param sessionId 会话ID
     * @param page      页码
     * @param limit     每页数量
     * @return 收藏列表
     */
    getFavoritesResponse getFavorites(String sessionId, Integer page, Integer limit);
}