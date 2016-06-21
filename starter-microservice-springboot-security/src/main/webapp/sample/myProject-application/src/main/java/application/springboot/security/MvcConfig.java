package application.springboot.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/springbootsecurity/home").setViewName("security-home");
        registry.addViewController("/springbootsecurity").setViewName("security-home");
        registry.addViewController("/springbootsecurity/hello").setViewName("security-hello");
        registry.addViewController("/springbootsecurity/login").setViewName("security-login");
    }

}
