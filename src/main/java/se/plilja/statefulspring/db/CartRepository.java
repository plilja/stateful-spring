package se.plilja.statefulspring.db;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface CartRepository extends MongoRepository<CartEntity, String> {

}
