package cn.edu.xidian.tafei_mall.model.vo.Response.Favorite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class getFavoritesResponse {
    private int total;
    private List<FavoriteItem> favorites = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteItem {
        private String productId;
        private String name;
        private double currentPrice;
        private String thumbnail;
    }

    public void addFavorite(String productId, String name, double currentPrice, String thumbnail) {
        favorites.add(new FavoriteItem(productId, name, currentPrice, thumbnail));
        total = favorites.size();
    }
}