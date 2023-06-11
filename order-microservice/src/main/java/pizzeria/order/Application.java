package pizzeria.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SpringBootApplication(scanBasePackages = {"pizzeria.order"})
public class Application {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExcludeFromJacocoGeneratedReport {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.CONSTRUCTOR)
    public @interface ExcludeFromJacocoGeneratedReportConstructor{}

    @ExcludeFromJacocoGeneratedReport
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

