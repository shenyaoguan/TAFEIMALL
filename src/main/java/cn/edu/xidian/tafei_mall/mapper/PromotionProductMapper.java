package cn.edu.xidian.tafei_mall.mapper;

import cn.edu.xidian.tafei_mall.model.entity.PromotionProduct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;

@Mapper
public interface PromotionProductMapper extends BaseMapper<PromotionProduct> {
    @Select(
            "SELECT pp.* FROM t_promotion_products pp " +
                    "INNER JOIN t_promotions p ON pp.promotion_id = p.promotion_id " +
                    "INNER JOIN product pr ON pp.product_id = pr.product_id " +
                    "WHERE p.is_active = 1 " +
                    "AND p.start_date <= #{now} " +
                    "AND p.end_date >= #{now} " +
                    "AND pr.stock > 0 " +
                    "ORDER BY p.start_date DESC"
    )
    IPage<PromotionProduct> selectActivePromotionsWithStock(
            Page<PromotionProduct> page,
            @Param("now") LocalDateTime now
    );
}
