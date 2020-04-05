package se.plilja.statefulspring.db;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Builder
@Data
@Document
public class ProductEntity {
    @Id
    private String productId;
    private String name;
    private String officialProductIdentifier;
    private BigDecimal cost;
}
