package cn.edu.xidian.tafei_mall.controller.v2;

import cn.edu.xidian.tafei_mall.model.vo.ImageVO;
import cn.edu.xidian.tafei_mall.service.ImageService;
import cn.edu.xidian.tafei_mall.service.ProductService;
import cn.edu.xidian.tafei_mall.service.UserService;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

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

    @GetMapping("")
    public ResponseEntity<?> getImage(@RequestBody ImageVO imageVO, @RequestHeader("Session-Id") String sessionId) throws IOException {
        // 检查是否登录
        if (sessionId == null) {
            return ResponseEntity.status(401).build();
        }
        // 检查用户是否存在
        if (userService.getUserInfo(sessionId) == null) {
            return ResponseEntity.status(401).build();
        }
        // 检查图像是否存在
        InputStream image = imageService.getImage(imageVO.getImagePath());
        if (image == null) {
            return ResponseEntity.status(404).body("Image not found");
        }
        byte[] imageByte = StreamUtils.copyToByteArray(image);
        // 这里可以添加处理图像请求的逻辑
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageByte);
    }

    @PostMapping("{productId}")
    public ResponseEntity<?> createImage(@PathVariable("productId") String productId, @RequestParam("image") MultipartFile image,
                                         @RequestHeader("Session-Id") String sessionId) {
        // 检查是否登录
        if (sessionId == null) {
            return ResponseEntity.status(401).build();
        }
        // 检查用户是否存在
        if (userService.getUserInfo(sessionId) == null) {
            return ResponseEntity.status(401).build();
        }
        // 检查产品是否存在
        if (productService.getProductById(productId).isEmpty()) {
            return ResponseEntity.status(404).body("Product not found");
        }
        try {
            // 这里可以添加处理图像上传的逻辑
            URI imagePath = imageService.uploadImage(productId, image);
            return ResponseEntity.created(imagePath).body(JSONUtil.createObj().set("imagePath", imagePath));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
