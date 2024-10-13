package uniauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@ServletComponentScan
@EnableAsync
@SpringBootApplication
public class UniauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniauthApplication.class, args);
    }

}
