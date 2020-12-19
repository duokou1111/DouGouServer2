package dougou.web.rest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"dougou.web.core","dougou.web.rest"})
@MapperScan("dougou.web.core.mapper")
@EnableDiscoveryClient

public class WebServer7071 {
    public static void main(String[] args) {
        SpringApplication.run(WebServer7071.class,args);
    }
}
