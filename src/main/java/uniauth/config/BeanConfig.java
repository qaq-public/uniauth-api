package uniauth.config;

import com.qaq.base.aspect.LogAspect;
import com.qaq.base.aspect.PermissionCheckAspect;
import com.qaq.base.component.HttpUtil;
import com.qaq.base.config.JWTGeneratorConfig;
import com.qaq.base.config.LarkConfig;
import com.qaq.base.config.NotifyConfig;
import com.qaq.base.config.RestTemplateConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@Import({JWTGeneratorConfig.class, LarkConfig.class, RestTemplateConfig.class, HttpUtil.class, NotifyConfig.class})
public class BeanConfig {

    @Bean
    public LogAspect apiLogAspect() {
        return new LogAspect();
    }

    @Bean
    public PermissionCheckAspect permissionCheckAspect() {
        return new PermissionCheckAspect();
    }

}
