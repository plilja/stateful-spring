package se.plilja.statefulspring;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v2/carts")
@RestController
class CartControllerV2 {
    private final CartFactory cartFactory;

    @GetMapping("/{cartId}")
    Cart getCart(@PathVariable("cartId") String cartId) {
        return cartFactory.getCart(cartId);
    }

    @PostMapping
    Cart createCart() {
        var cart = cartFactory.newCart();
        cart.save();
        return cart;
    }

    @PostMapping("/{cartId}/add")
    void addItemToCart(@PathVariable("cartId") String cartId, @RequestParam("productId") String productId) {
        var cart = cartFactory.getCart(cartId);
        cart.addItemToCart(productId);
    }

    @DeleteMapping("/{cartId}/remove")
    void removeItemFromCart(@PathVariable("cartId") String cartId, @RequestParam("productId") String productId) {
        var cart = cartFactory.getCart(cartId);
        cart.removeItemFromCart(productId);
    }
}
