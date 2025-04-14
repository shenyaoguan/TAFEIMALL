package cn.edu.xidian.tafei_mall.mapper;

import cn.edu.xidian.tafei_mall.model.entity.Product;
import cn.edu.xidian.tafei_mall.model.vo.MonthlySalesVO;
import cn.edu.xidian.tafei_mall.model.vo.TopProductVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    public TopProductVO getTopProduct();

}
