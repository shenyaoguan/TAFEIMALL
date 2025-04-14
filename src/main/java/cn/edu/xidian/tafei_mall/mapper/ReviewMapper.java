package cn.edu.xidian.tafei_mall.mapper;

import cn.edu.xidian.tafei_mall.model.entity.Review;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
    @Insert("insert into product_reviews (review_id, product_id, order_id, rating, comment, created_at, updated_at) " +
            "values (#{review_id}, #{product_id}, #{order_id}, #{rating}, #{comment}, #{created_at}, #{updated_at})")
    void addReview(Review review);

}
