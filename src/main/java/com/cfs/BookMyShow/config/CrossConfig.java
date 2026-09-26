package com.cfs.BookMyShow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
           @Configuration                //WebMvcConfigurer allows us to customize Spring MVC behavior.
public class CrossConfig implements WebMvcConfigurer {
                              // CorsRegistry is a Spring class used to define CORS rules.
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/api/**") //Apply these CORS rules to URLs beginning with /api/.
                .allowedOriginPatterns("http://localhost:*","http://127.0.0.1:*")
                // This specifies which frontend origins are allowed.
                .allowedMethods("GET","POST","OPTIONS") // Currently you dont have: PUT,DELETE That's why they are not written
                .allowedHeaders("*"); // Allow the frontend to send request headers.
    }
}

/*
CORS = Cross-Origin Resource Sharing
CORS tells the browser:
  "It is okay for requests coming from these origins to access my backend."
So your CrossConfig is basically saying:
  "For my /api/** APIs, allow frontend applications running on localhost" +
  " or 127.0.0.1, regardless of their port, to make GET, POST, and OPTIONS requests."*/

//Allow local frontend applications to access my BookMyShow API through cross-origin requests,
// subject to these CORS rules."
