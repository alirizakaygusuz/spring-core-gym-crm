package com.alirizakaygusuz.gymcrm.config.web;

import com.alirizakaygusuz.gymcrm.config.app.AppConfig;
import com.alirizakaygusuz.gymcrm.config.jackson.JacksonConfig;
import com.alirizakaygusuz.gymcrm.config.persistence.PersistenceConfiguration;
import com.alirizakaygusuz.gymcrm.config.security.SecurityConfig;
import com.alirizakaygusuz.gymcrm.config.validation.ValidationConfig;
import jakarta.servlet.DispatcherType;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.EnumSet;

public class WebAppInitializer implements WebApplicationInitializer {
  @Override
  public void onStartup(jakarta.servlet.ServletContext sc) {

    // ROOT CONTEXT - Business logic, persistence, security
    var root = new AnnotationConfigWebApplicationContext();
    root.register(
            AppConfig.class,
            PersistenceConfiguration.class,
            SecurityConfig.class,
            JacksonConfig.class,
            ValidationConfig.class
    );

    sc.addListener(new ContextLoaderListener(root));

    var servlet = new AnnotationConfigWebApplicationContext();
    servlet.setParent(root);
    servlet.register(
            WebConfig.class,
            OpenApiConfig.class
    );

    var dispatcher = sc.addServlet("dispatcher", new DispatcherServlet(servlet));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");



    var requestContextFilter = sc.addFilter("requestContextFilter", new RequestContextFilter());
    requestContextFilter.addMappingForUrlPatterns(
            EnumSet.of(DispatcherType.REQUEST),
            false,
            "/*"
    );
    var jwtFilter = sc.addFilter(
            "jwtAuthenticationFilter",
            new DelegatingFilterProxy("jwtAuthenticationFilter", root)
    );
    jwtFilter.addMappingForUrlPatterns(
            EnumSet.of(DispatcherType.REQUEST),
            false,
            "/*"
    );


  }
}