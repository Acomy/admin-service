//package com.bossien.service.config;
//
//import com.bossien.common.filter.HttpBasicAuthorizeFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//public class FilterConfig {
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        HttpBasicAuthorizeFilter httpBasicFilter = new HttpBasicAuthorizeFilter();
//        registrationBean.setFilter(httpBasicFilter);
//
//        List<String> urlPatterns = new ArrayList<String>(1);
//        urlPatterns.add("/*");
//        registrationBean.setUrlPatterns(urlPatterns);
//        return registrationBean;
//    }
//}
