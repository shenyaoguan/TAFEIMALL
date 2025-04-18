package cn.edu.xidian.tafei_mall.mapper;

import cn.edu.xidian.tafei_mall.model.entity.Favorite;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 收藏表 Mapper 接口
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}