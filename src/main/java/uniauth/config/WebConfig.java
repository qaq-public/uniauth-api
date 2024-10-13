package uniauth.config;

import com.qaq.base.intercepter.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/web/**",
                        "/permissions/**",
                        "/device/**",
                        "/gitlock/**",
                        "/knowagesub/**",
                        "/alabin/user_access_token");
    }
}
