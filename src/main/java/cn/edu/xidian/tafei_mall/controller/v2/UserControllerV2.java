package cn.edu.xidian.tafei_mall.controller.v2;

import cn.edu.xidian.tafei_mall.model.vo.AddressUpdateVO;
import cn.edu.xidian.tafei_mall.model.vo.FavoriteAddVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Address.AddressResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Address.getAddressResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Favorite.FavoriteResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Favorite.getFavoritesResponse;
import cn.edu.xidian.tafei_mall.service.AddressService;
import cn.edu.xidian.tafei_mall.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 这里对应的是/api/users路径，用于处理用户的地址更新请求和收藏功能
 * </p>
 *
 * @auther: shenyaoguan
 *
 * @date: 2025-03-17
 *
 */

@RestController
@RequestMapping("/api/v2/users")
public class UserControllerV2 {
    @Autowired
    private AddressService addressService;
    @Autowired(required = false)
    private FavoriteService favoriteService;

    @PostMapping("/address")
    public ResponseEntity<?> addAddress(@RequestBody AddressUpdateVO addressUpdate,
            @RequestHeader("Session-Id") String sessionId) {
        try {
            addressService.addAddress(addressUpdate, sessionId);
            return new ResponseEntity<>(new AddressResponse("地址添加成功"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new AddressResponse("地址添加失败"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/address")
    public ResponseEntity<?> getAddress(@RequestHeader("Session-Id") String sessionId) {
        try {
            getAddressResponse res = addressService.getAddress(sessionId);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new AddressResponse("获取地址失败"), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<?> addAddress(@RequestBody AddressUpdateVO addressUpdate,
            @RequestHeader("Session-Id") String sessionId, @PathVariable String addressId) {
        try {
            addressService.updateAddress(addressUpdate, sessionId, addressId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(new AddressResponse("地址更新失败"), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/address/{addressId}")
    public ResponseEntity<?> deleteAddress(@RequestHeader("Session-Id") String sessionId,
            @PathVariable String addressId) {
        try {
            addressService.deleteAddress(addressId, sessionId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(new AddressResponse("地址删除失败"), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 添加收藏
     * 
     * @param productId     商品ID
     * @param sessionId     会话ID
     * @return 处理结果
     */
    @PostMapping("/favorites/{productId}")
    public ResponseEntity<?> addFavorite(@RequestHeader("Session-Id") String sessionId, @PathVariable String productId) {
        FavoriteAddVO favoriteAddVO = new FavoriteAddVO();
        favoriteAddVO.setProductId(productId);
        try {
            favoriteService.addFavorite(favoriteAddVO, sessionId);
            return new ResponseEntity<>(new FavoriteResponse("收藏成功"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(new FavoriteResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 删除收藏
     * 
     * @param productId     商品ID
     * @param sessionId     会话ID
     * @return 处理结果
     */
    @DeleteMapping("/favorites/{productId}")
    public ResponseEntity<?> removeFavorite(@RequestHeader("Session-Id") String sessionId, @PathVariable String productId) {
        FavoriteAddVO favoriteAddVO = new FavoriteAddVO();
        favoriteAddVO.setProductId(productId);
        try {
            favoriteService.removeFavorite(favoriteAddVO, sessionId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(new FavoriteResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 获取收藏列表
     * 
     * @param sessionId 会话ID
     * @param page      页码
     * @param limit     每页数量
     * @return 收藏列表
     */
    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites(
            @RequestHeader("Session-Id") String sessionId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit) {
        try {
            getFavoritesResponse response = favoriteService.getFavorites(sessionId, page, limit);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new FavoriteResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}