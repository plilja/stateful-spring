package se.plilja.statefulspring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import se.plilja.statefulspring.db.CartEntity;
import se.plilja.statefulspring.db.ProductEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CartControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private ProductEntity socksProduct;

    @BeforeEach
    void setUp() {
        baseUrl = String.format("http://localhost:%d", port);
        for (ProductEntity productEntity : restTemplate.getForObject(baseUrl + "/products", ProductEntity[].class)) {
            if (productEntity.getName().toLowerCase().contains("socks")) {
                socksProduct = productEntity;
            }
        }
    }

    @MethodSource("apiVersions")
    @ParameterizedTest
    void testCreateCart(String apiVersion) {
        // when
        CartEntity cart = createCart(apiVersion);

        // then
        assertNotNull(cart);
        assertNotNull(cart.getCartId());
        assertEquals(BigDecimal.ZERO, cart.getSubTotal());
        assertEquals(List.of(), cart.getContents());
    }

    @MethodSource("apiVersions")
    @ParameterizedTest
    void testAddItemToCart(String apiVersion) {
        // when
        var initialCart = createCart(apiVersion);
        addItemToCart(apiVersion, initialCart, socksProduct);
        addItemToCart(apiVersion, initialCart, socksProduct);
        CartEntity cart = getCart(apiVersion, initialCart.getCartId());

        // then
        assertEquals(1, cart.getContents().size());
        assertEquals(socksProduct.getProductId(), cart.getContents().get(0).getProductEntity().getProductId());
        assertEquals(2, cart.getContents().get(0).getQuantity());
        assertEquals(BigDecimal.valueOf(2).multiply(socksProduct.getCost()), cart.getSubTotal());
    }

    @MethodSource("apiVersions")
    @ParameterizedTest
    void testRemoveItemFromCart(String apiVersion) {
        // when
        var initialCart = createCart(apiVersion);
        addItemToCart(apiVersion, initialCart, socksProduct);
        addItemToCart(apiVersion, initialCart, socksProduct);
        removeItemFromCart(apiVersion, initialCart, socksProduct);
        var cartAfterOneRemove = getCart(apiVersion, initialCart.getCartId());
        removeItemFromCart(apiVersion, initialCart, socksProduct);
        var cartAfterTwoRemove = getCart(apiVersion, initialCart.getCartId());

        // then
        assertEquals(1, cartAfterOneRemove.getContents().size());
        assertEquals(socksProduct.getProductId(), cartAfterOneRemove.getContents().get(0).getProductEntity().getProductId());
        assertEquals(socksProduct.getCost(), cartAfterOneRemove.getSubTotal());
        assertEquals(0, cartAfterTwoRemove.getContents().size());
        assertEquals(BigDecimal.ZERO, cartAfterTwoRemove.getSubTotal());
    }

    private CartEntity getCart(String apiVersion, String cartId) {
        return restTemplate.getForObject(baseUrl + "/{apiVersion}/carts/{cartId}", CartEntity.class, apiVersion, cartId);
    }

    private CartEntity createCart(String apiVersion) {
        var cartEntity = restTemplate.postForEntity(baseUrl + "/{apiVersion}/carts", null, CartEntity.class, apiVersion);
        var cart = cartEntity.getBody();
        assertEquals(HttpStatus.OK, cartEntity.getStatusCode());
        return cart;
    }

    private void addItemToCart(String apiVersion, CartEntity cartId, ProductEntity product) {
        var response = restTemplate.postForEntity(baseUrl + "/{apiVersion}/carts/{cartId}/add?productId={productId}", null, Void.class, apiVersion, cartId.getCartId(), product.getProductId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void removeItemFromCart(String apiVersion, CartEntity cartId, ProductEntity product) {
        var response = restTemplate.exchange(baseUrl + "/{apiVersion}/carts/{cartId}/remove?productId={productId}", HttpMethod.DELETE, null, Void.class, apiVersion, cartId.getCartId(), product.getProductId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private static Stream<String> apiVersions() {
        return Stream.of("v1", "v2");
    }
}