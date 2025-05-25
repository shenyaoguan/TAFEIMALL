package cn.edu.xidian.tafei_mall;

/*import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;*/
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TaFeiConfiguration implements WebMvcConfigurer {
    @Value("${web.web-url}")
    String webUrl;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(webUrl) // 允许的域名，可以用*表示允许所有域名访问
                .allowedMethods("*") // 允许的请求方法
                .allowedHeaders("*") // 允许的请求头
                .allowCredentials(true) // 是否允许发送Cookie
                .maxAge(3600); // 预检请求的有效期
    }

      /*  @Bean
        public PaginationInterceptor paginationInterceptor() {
            PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
            paginationInterceptor.setDbType(DbType.MYSQL);
            paginationInterceptor.setOverflow(false);
            paginationInterceptor.setLimit(500);
            return paginationInterceptor;
        }*/
    }

