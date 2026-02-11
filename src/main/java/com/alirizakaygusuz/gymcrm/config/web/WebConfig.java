package com.alirizakaygusuz.gymcrm.config.web;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ComponentScan(basePackages = "com.alirizakaygusuz.gymcrm.controller")
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {


}
