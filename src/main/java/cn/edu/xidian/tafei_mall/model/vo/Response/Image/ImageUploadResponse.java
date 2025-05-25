package cn.edu.xidian.tafei_mall.model.vo.Response.Image;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageUploadResponse {
    private final String imagePath;

    public ImageUploadResponse(String imagePath) {
        this.imagePath = imagePath;
    }
}
