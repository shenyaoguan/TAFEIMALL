package cn.edu.xidian.tafei_mall.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("")
public class RedirectController {
    @Value("${web.web-url}")
    String webUrl;

    @GetMapping("")
    public void redirectToWebBaseUrl(HttpServletResponse httpResponse) throws IOException {
        httpResponse.setStatus(HttpServletResponse.SC_OK);
        httpResponse.sendRedirect(webUrl);
    }
}
