package pizzeria.food;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@SpringBootApplication(scanBasePackages = {"pizzeria.food"})
public class Application {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExcludeFromJacocoGeneratedReport {}
    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

