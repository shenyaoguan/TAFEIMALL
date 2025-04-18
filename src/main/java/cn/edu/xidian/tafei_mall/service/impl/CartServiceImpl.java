package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.CartItemMapper;
import cn.edu.xidian.tafei_mall.model.entity.Cart;
import cn.edu.xidian.tafei_mall.mapper.CartMapper;
import cn.edu.xidian.tafei_mall.model.entity.CartItem;
import cn.edu.xidian.tafei_mall.model.entity.Product;
import cn.edu.xidian.tafei_mall.model.entity.User;
import cn.edu.xidian.tafei_mall.model.vo.CartItemAddVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Cart.CartResponse;
import cn.edu.xidian.tafei_mall.service.CartService;
import cn.edu.xidian.tafei_mall.service.ProductService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductService productService;

    @Override
    public CartResponse getCart(String userId) {


        Cart cart = getOne(new QueryWrapper<Cart>().eq("user_id", userId));
        if (cart == null) {
            return null;
        }
        CartResponse response = new CartResponse(cart.getCartId());
        List<CartItem> cartItems = cartItemMapper.selectList(new QueryWrapper<CartItem>().eq("cart_id", cart.getCartId()));
        double totalPrice = 0.0;
        for (CartItem cartItem : cartItems) {
            Product product = productService.getById(cartItem.getProductId());
            if (product != null) {
                BigDecimal current = productService.currentPrice(product.getProductId()); // 返回现价
                response.putItem(cartItem.getCartItemId() ,product.getProductId(), product.getName(), cartItem.getQuantity() ,current.doubleValue());
                totalPrice += current.doubleValue() * cartItem.getQuantity();
            }
        }
        response.setTotal(totalPrice);
        return response;
    }

    @Override
    public void addToCart(CartItemAddVO cartItemAddVO, User user) {
        Cart cart = getOne(new QueryWrapper<Cart>().eq("user_id", user.getUserId()));
        if (cart == null) {
            cart = new Cart();
            cart.setCartId(String.valueOf(UUID.randomUUID()));
            cart.setUserId(user.getUserId());
            save(cart);
        }
        CartItem cartItem = new CartItem();
        cartItem.setCartItemId(String.valueOf(UUID.randomUUID()));
        cartItem.setCartId(cart.getCartId());
        cartItem.setProductId(cartItemAddVO.getProductId());
        cartItem.setQuantity(cartItemAddVO.getQuantity());
        cartItemMapper.insert(cartItem);
    }
}
