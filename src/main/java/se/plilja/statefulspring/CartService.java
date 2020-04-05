package se.plilja.statefulspring;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import se.plilja.statefulspring.db.CartContent;
import se.plilja.statefulspring.db.CartEntity;
import se.plilja.statefulspring.db.CartRepository;
import se.plilja.statefulspring.db.ProductRepository;

@RequiredArgsConstructor
@Service
class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    CartEntity getCart(String cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cart with id %s not found", cartId)));
    }

    CartEntity createCart() {
        var newCart = new CartEntity();
        return cartRepository.save(newCart);
    }

    void addItemToCart(String cartId, String productId) {
        var cart = getCart(cartId);
        cart.getContents().stream()
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
                            cart.getContents().add(cartContent);
                        }
                );
        cartRepository.save(cart);
    }

    void removeItemFromCart(String cartId, String productId) {
        var cart = getCart(cartId);
        var cartContents = cart.getContents().stream()
                .filter(content -> content.getProductEntity().getProductId().equalsIgnoreCase(productId))
                .findFirst()
                .orElseThrow(() -> {
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Product with id %s not present in cart with id %s", productId, cartId));
                });
        cartContents.setQuantity(cartContents.getQuantity() - 1);
        if (cartContents.getQuantity() == 0) {
            cart.getContents().remove(cartContents);
        }
        cartRepository.save(cart);
    }
}
