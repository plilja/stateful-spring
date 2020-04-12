package se.plilja.statefulspring;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

@Component
public abstract class CartFactory {
    @Lookup
    abstract Cart getCart(String cartId);

    Cart newCart() {
        return getCart(null);
    }
}
