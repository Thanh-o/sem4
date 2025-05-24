package com.example.usersevice.config;


import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class StringToDateConverter {

    public static class DateConverter implements Converter<String, LocalDateTime> {
        @Override
        public LocalDateTime convert(MappingContext<String, LocalDateTime> context) {
            String source = context.getSource();
            if (source == null || source.isEmpty()) {
                return null;
            }
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                return LocalDateTime.parse(source, formatter);
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(new DateConverter());
        return modelMapper;
    }
}
