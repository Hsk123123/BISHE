package com.skillchain.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.base-url}")
    private String baseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射静态资源，将/uploads/**请求映射到实际的文件存储路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
