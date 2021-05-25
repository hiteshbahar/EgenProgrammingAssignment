package com.interview.egen.orderManagement.service;

import com.interview.egen.orderManagement.model.Order;
import com.interview.egen.orderManagement.repository.OrderRepository;
import com.interview.egen.orderManagement.util.Calculation;
import com.mongodb.DuplicateKeyException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OrderService {

    OrderRepository orderRepository;

    public Mono<Order> getOrderById(String orderId){
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, orderId +" not found"))
                );
    }

    public Flux<Order> allOrders() {
        return orderRepository.findAll().switchIfEmpty(
                Flux.error(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No orders at all")
                )
        );
    }

    public Mono<Order> saveCustomerOrder(Order order) {
        if (order == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Empty Order, not allowed to create an empty order"));
        } else {
            return Mono.just(order)
                    .map(Calculation::toPersistence)
                    .flatMap(orderRepository::save)
                    .onErrorResume(DuplicateKeyException.class,
                            cause -> getOrderById(order.getId()));
        }

    }

    public Mono<Void> deleteOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        orderId + " not found")))
                .flatMap(existing -> orderRepository.deleteById(orderId));
    }
}
