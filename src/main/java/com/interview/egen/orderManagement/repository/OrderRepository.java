package com.interview.egen.orderManagement.repository;

import com.interview.egen.orderManagement.model.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface OrderRepository extends ReactiveMongoRepository<Order, String> {
    Mono<Order> findById(String id);

    Mono<Void> deleteById(String id);
}
