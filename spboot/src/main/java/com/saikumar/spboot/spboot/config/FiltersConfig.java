package com.saikumar.spboot.spboot.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.saikumar.spboot.spboot.filters.RequestResponseLoggers;

@Configuration
public class FiltersConfig {


    @Bean
    FilterRegistrationBean<RequestResponseLoggers> createLoggers(RequestResponseLoggers requestResponseLoggers){
        FilterRegistrationBean<RequestResponseLoggers> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(requestResponseLoggers);
        registrationBean.addUrlPatterns("/start/all","/start/user/*");

        return registrationBean;
    }
}