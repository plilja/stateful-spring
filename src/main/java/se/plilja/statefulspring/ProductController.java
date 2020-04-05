package se.plilja.statefulspring;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import se.plilja.statefulspring.db.ProductEntity;
import se.plilja.statefulspring.db.ProductRepository;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/products")
@RestController
public class ProductController {
    private final ProductRepository productRepository;

    @GetMapping("/{productId}")
    ProductEntity getProduct(@PathVariable("productId") String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Product with id %s not found", productId)));
    }

    @GetMapping
    List<ProductEntity> listProducts() {
        return productRepository.findAll();
    }
}
