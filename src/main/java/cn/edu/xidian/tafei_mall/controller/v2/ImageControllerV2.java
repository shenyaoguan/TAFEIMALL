package cn.edu.xidian.tafei_mall.controller.v2;

import cn.edu.xidian.tafei_mall.model.entity.User;
import cn.edu.xidian.tafei_mall.model.vo.ImageVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Image.ImageUploadResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Image.LskyResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Order.MessageResponse;
import cn.edu.xidian.tafei_mall.service.ImageService;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.edu.xidian.tafei_mall.service.UserService;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * <p>
 *     ImageController
 * </p>
 *
 *
 * 这个类是一个控制器，用于处理与图像相关的请求。
 *
 * @author shenyaoguan
 *
 * @since 2025-03-17
 */


@RestController
@RequestMapping("/api/v2/image")
public class ImageControllerV2 {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @PostMapping("{productId}")
    public ResponseEntity<?> imageUploadToLsky(@PathVariable("productId") String productId, @RequestParam("image") MultipartFile image,
                                               @RequestHeader("Session-Id") String sessionId) {
        try {
            if (sessionId == null) {
                return new ResponseEntity<>(new MessageResponse("未登录"), HttpStatus.UNAUTHORIZED);
            }
            if (userService.getUserInfo(sessionId) == null) {
                return new ResponseEntity<>(new MessageResponse("用户不存在"), HttpStatus.UNAUTHORIZED);
            }
            ImageUploadResponse response = imageService.uploadToLsky(productId, image);
            if (response != null) {
                return ResponseEntity.ok().body(response);
            } else {
                return new ResponseEntity<>(new MessageResponse("上传失败"), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
