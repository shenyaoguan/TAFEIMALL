package cn.edu.xidian.tafei_mall.service.impl;

import cn.edu.xidian.tafei_mall.mapper.ImageMapper;
import cn.edu.xidian.tafei_mall.mapper.ProductMapper;
import cn.edu.xidian.tafei_mall.model.entity.Image;
import cn.edu.xidian.tafei_mall.model.vo.ImageVO;
import cn.edu.xidian.tafei_mall.model.vo.Response.Image.ImageUploadResponse;
import cn.edu.xidian.tafei_mall.model.vo.Response.Image.LskyResponse;
import cn.edu.xidian.tafei_mall.service.ImageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private ProductMapper productMapper;

    @Value("${api.lsky-pro.api-url}")
    private String API_URL;
    @Value("${api.lsky-pro.auth-token}")
    private String AUTH_TOKEN;
    @Value("${api.lsky-pro.strategy-id}")
    private int STRATEGY_ID;
    @Value("${web.upload-path}")
    private String uploadPath;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public ImageUploadResponse uploadToLsky(String productId, MultipartFile file) throws IOException, InterruptedException {
        if (productMapper.selectById(productId) == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String boundary = UUID.randomUUID().toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", AUTH_TOKEN)
                .header("Accept", "application/json")
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(createMultipartBody(file, boundary))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        LskyResponse lskyResponse = parseResponse(response.body());

        if (lskyResponse.isStatus()) {
            System.out.println(lskyResponse.getData());
            String url = lskyResponse.getData().getLinks().getUrl();
            Image image = new Image();
            image.setImagePath(url);
            image.setProductId(productId);
            imageMapper.insert(image);
            return new ImageUploadResponse(url);
        } else {
            return null;
        }
    }

    @Override
    public URI uploadToLocal(String productId, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String saveUri = uploadPath + "/" + productId + "/" + fileName;
        File uploadFile = new File(saveUri);
        if (!uploadFile.exists()) {
            uploadFile.mkdirs();
        }
        file.transferTo(uploadFile);
        Image image = new Image();
        image.setProductId(productId);
        image.setImagePath(productId+ "/" + file.getOriginalFilename());
//        image.setImageId(UUID.randomUUID().toString());
        saveOrUpdate(image);
        return URI.create(productId + "/" + fileName);
    }

    /*----------------------内部方法----------------------*/

    private HttpRequest.BodyPublisher createMultipartBody(MultipartFile file, String boundary) {
        return HttpRequest.BodyPublishers.ofInputStream(() -> {
            try {
                return new SequenceInputStream(createFilePart(file, boundary), createFormField("strategy_id", String.valueOf(STRATEGY_ID), boundary));
            } catch (Exception e) {
                throw new RuntimeException("Failed to create request body", e);
            }
        });
    }

    private InputStream createFilePart(MultipartFile file, String boundary) throws IOException {
        String header = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file\"; filename=\"" +
                file.getOriginalFilename() + "\"\r\n" +
                "Content-Type: " + file.getContentType() + "\r\n\r\n";

        return new SequenceInputStream(
                new ByteArrayInputStream(header.getBytes(StandardCharsets.UTF_8)),
                new SequenceInputStream(
                        file.getInputStream(),
                        new ByteArrayInputStream("\r\n".getBytes(StandardCharsets.UTF_8))
                )
        );
    }

    private InputStream createFormField(String name, String value, String boundary) {
        String part = "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"" + name + "\"\r\n\r\n" +
                value + "\r\n" +
                "--" + boundary + "--\r\n";

        return new ByteArrayInputStream(part.getBytes(StandardCharsets.UTF_8));
    }

    private LskyResponse parseResponse(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, LskyResponse.class);
    }
}
