package se.plilja.statefulspring.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document
public class CartEntity {
    @Id
    private String cartId;
    @Builder.Default
    private List<CartContent> contents = new ArrayList<>();

    public BigDecimal getSubTotal() {
        return contents.stream()
                .map(content -> BigDecimal.valueOf(content.getQuantity()).multiply(content.getProductEntity().getCost()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
