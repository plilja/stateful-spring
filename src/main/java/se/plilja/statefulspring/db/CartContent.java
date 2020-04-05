package se.plilja.statefulspring.db;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Builder
@Data
public class CartContent {
    @DBRef
    private ProductEntity productEntity;
    private int quantity;
}
