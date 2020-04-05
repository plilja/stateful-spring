package se.plilja.statefulspring;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import se.plilja.statefulspring.db.CartContent;
import se.plilja.statefulspring.db.CartEntity;
import se.plilja.statefulspring.db.CartRepository;
import se.plilja.statefulspring.db.ProductRepository;

@RequiredArgsConstructor
public class Cart {
    private final CartEntity cartEntity;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    void save() {
        var saved = cartRepository.save(cartEntity);
        if (cartEntity.getCartId() == null) {
            cartEntity.setCartId(saved.getCartId());
        }
    }

    void addItemToCart(String productId) {
        cartEntity.getContents().stream()
                .filter(content -> content.getProductEntity().getProductId().equalsIgnoreCase(productId))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + 1),
                        () -> {
                            var product = productRepository.findById(productId)
                                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Product with id %s not found", productId)));
                            var cartContent = CartContent.builder()
                                    .productEntity(product)
                                    .quantity(1)
                                    .build();
                            cartEntity.getContents().add(cartContent);
                        }
                );
        save();
    }

    void removeItemFromCart(String productId) {
        var cartContents = cartEntity.getContents().stream()
                .filter(content -> content.getProductEntity().getProductId().equalsIgnoreCase(productId))
                .findFirst()
                .orElseThrow(() -> {
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Product with id %s not present in cart with id %s", productId, cartEntity.getCartId()));
                });
        cartContents.setQuantity(cartContents.getQuantity() - 1);
        if (cartContents.getQuantity() == 0) {
            cartEntity.getContents().remove(cartContents);
        }
        save();
    }

    /**
     * Use the entity when serializing to JSON.
     */
    @JsonValue
    CartEntity jsonValue() {
        return cartEntity;
    }
}
