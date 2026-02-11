package com.alirizakaygusuz.gymcrm.config.web;

import jakarta.servlet.DispatcherType;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import java.util.EnumSet;

public class WebAppInitializer implements WebApplicationInitializer {
  @Override
  public void onStartup(jakarta.servlet.ServletContext sc) {
    var root = new org.springframework.web.context.support.AnnotationConfigWebApplicationContext();
    root.register(
        com.alirizakaygusuz.gymcrm.config.app.AppConfig.class,
        com.alirizakaygusuz.gymcrm.config.persistence.PersistenceConfiguration.class,
        com.alirizakaygusuz.gymcrm.config.security.SecurityConfig.class,
        com.alirizakaygusuz.gymcrm.config.jackson.JacksonConfig.class,
        com.alirizakaygusuz.gymcrm.config.validation.ValidationConfig.class
    );

    sc.addListener(new org.springframework.web.context.ContextLoaderListener(root));

    var servlet = new org.springframework.web.context.support.AnnotationConfigWebApplicationContext();
    servlet.register(com.alirizakaygusuz.gymcrm.config.web.WebConfig.class);

    var dispatcher = sc.addServlet("dispatcher", new org.springframework.web.servlet.DispatcherServlet(servlet));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");

    var authFilter = sc.addFilter(
            "authenticationFilter",
            new DelegatingFilterProxy("authenticationFilter", root)
    );
    authFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), false, "/*");
  }


}
