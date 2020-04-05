package se.plilja.statefulspring;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import se.plilja.statefulspring.db.CartContent;
import se.plilja.statefulspring.db.CartEntity;
import se.plilja.statefulspring.db.CartRepository;
import se.plilja.statefulspring.db.ProductEntity;
import se.plilja.statefulspring.db.ProductRepository;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@EnableSwagger2
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class Application {
    @Autowired
    @Lazy
    private CartRepository cartRepository;
    @Autowired
    @Lazy
    private ProductRepository productRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    @PostConstruct
    void initializeDatabaseTestDate() {
        var socks = ProductEntity.builder()
                .name("Socks")
                .officialProductIdentifier("1001_socks")
                .cost(new BigDecimal("50"))
                .build();
        var book = ProductEntity.builder()
                .name("Book")
                .officialProductIdentifier("1002_book")
                .cost(new BigDecimal("121"))
                .build();
        var lamp = ProductEntity.builder()
                .name("Lamp")
                .officialProductIdentifier("1003_lamp")
                .cost(new BigDecimal("250"))
                .build();
        List.of(socks, book, lamp).forEach(productEntity -> {
            var savedProduct = productRepository.save(productEntity);
            log.info("Created product {} with id {}", savedProduct.getName(), savedProduct.getProductId());
        });
        var cartContents = List.of(
                CartContent.builder()
                        .productEntity(socks)
                        .quantity(2)
                        .build(),
                CartContent.builder()
                        .productEntity(book)
                        .quantity(3)
                        .build()
        );
        var cart = CartEntity.builder()
                .contents(cartContents)
                .build();
        var savedCart = cartRepository.save(cart);
        log.info("Created cart with id {}", savedCart.getCartId());
    }
}
