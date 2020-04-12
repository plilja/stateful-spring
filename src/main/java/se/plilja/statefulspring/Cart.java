package se.plilja.statefulspring;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import se.plilja.statefulspring.db.CartContent;
import se.plilja.statefulspring.db.CartEntity;
import se.plilja.statefulspring.db.CartRepository;
import se.plilja.statefulspring.db.ProductRepository;

@Component
@Scope("prototype")
public class Cart {
    private String cartId;
    private final MemoizedSupplier<CartEntity> cartEntity = new MemoizedSupplier<>(this::getCartEntity);
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    public Cart(String cartId) {
        this.cartId = cartId;
    }

    void save() {
        var saved = cartRepository.save(cartEntity.get());
        if (cartId == null) {
            cartId = saved.getCartId();
            cartEntity.get().setCartId(saved.getCartId());
        }
    }

    private CartEntity getCartEntity() {
        if (cartId != null) {
            return cartRepository.findById(cartId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } else {
            return new CartEntity();
        }
    }

    void addItemToCart(String productId) {
        cartEntity.get().getContents().stream()
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
                            cartEntity.get().getContents().add(cartContent);
                        }
                );
        save();
    }

    void removeItemFromCart(String productId) {
        var cartContents = cartEntity.get().getContents().stream()
                .filter(content -> content.getProductEntity().getProductId().equalsIgnoreCase(productId))
                .findFirst()
                .orElseThrow(() -> {
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Product with id %s not present in cart with id %s", productId, cartId));
                });
        cartContents.setQuantity(cartContents.getQuantity() - 1);
        if (cartContents.getQuantity() == 0) {
            cartEntity.get().getContents().remove(cartContents);
        }
        save();
    }

    /**
     * Use the entity when serializing to JSON.
     */
    @JsonValue
    CartEntity jsonValue() {
        return cartEntity.get();
    }
}
