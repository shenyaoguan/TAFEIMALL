package cn.edu.xidian.tafei_mall.model.vo;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageVO {
    private MultipartFile file;
    private String productId;
}
