package cn.edu.xidian.tafei_mall.mapper;

import cn.edu.xidian.tafei_mall.model.entity.OrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 订单项表 Mapper 接口
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    public Integer getSalesVolume(@Param("startDate") String startDate, @Param("endDate") String endDate);

}
