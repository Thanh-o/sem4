package com.example.appointmentservices.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Loại bỏ cấu hình ánh xạ chi tiết để kiểm tra mặc định
        // Nếu cần ánh xạ cụ thể, thêm lại sau khi xác minh tên property
        return modelMapper;
    }
}
