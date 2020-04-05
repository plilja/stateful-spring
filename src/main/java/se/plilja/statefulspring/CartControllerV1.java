package se.plilja.statefulspring;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.plilja.statefulspring.db.CartEntity;

@RequiredArgsConstructor
@RequestMapping("/v1/carts")
@RestController
class CartControllerV1 {
    private final CartService cartService;

    @GetMapping("/{cartId}")
    CartEntity getCart(@PathVariable("cartId") String cartId) {
        return cartService.getCart(cartId);
    }

    @PostMapping
    CartEntity createCart() {
        return cartService.createCart();
    }

    @PostMapping("/{cartId}/add")
    void addItemToCart(@PathVariable("cartId") String cartId, @RequestParam("productId") String productId) {
        cartService.addItemToCart(cartId, productId);
    }

    @DeleteMapping("/{cartId}/remove")
    void removeItemFromCart(@PathVariable("cartId") String cartId, @RequestParam("productId") String productId) {
        cartService.removeItemFromCart(cartId, productId);
    }
}
