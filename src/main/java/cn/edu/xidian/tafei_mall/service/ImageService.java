package cn.edu.xidian.tafei_mall.service;


import cn.edu.xidian.tafei_mall.model.entity.Image;
import cn.edu.xidian.tafei_mall.model.vo.ImageVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Image.ImageUploadResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Image.LskyResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * <p>
 * 图片表 服务类
 * </p>
 *
 * @author shenyaoguan
 * @since 2025-03-17
 */

public interface ImageService extends IService<Image> {
    ImageUploadResponse uploadToLsky(String productId, MultipartFile file) throws IOException, InterruptedException;
    URI uploadToLocal(String productId, MultipartFile file) throws IOException;
}
