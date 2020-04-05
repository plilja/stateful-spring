package se.plilja.statefulspring;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import se.plilja.statefulspring.db.CartEntity;
import se.plilja.statefulspring.db.CartRepository;
import se.plilja.statefulspring.db.ProductRepository;

@RequiredArgsConstructor
@Component
public class CartFactory {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    Cart getCart(String cartId) {
        var cartEntity = getCartEntity(cartId);
        return new Cart(cartEntity, cartRepository, productRepository);
    }

    Cart newCart() {
        var cartEntity = new CartEntity();
        return new Cart(cartEntity, cartRepository, productRepository);
    }

    private CartEntity getCartEntity(String cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Cart with id %s not found", cartId)));
    }
}
