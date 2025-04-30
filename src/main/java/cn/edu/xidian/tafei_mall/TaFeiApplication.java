package cn.edu.xidian.tafei_mall;


import com.github.jeffreyning.mybatisplus.conf.EnableMPP;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.xidian.tafei_mall.mapper")
@EnableMPP
public class TaFeiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaFeiApplication.class, args);
    }
}