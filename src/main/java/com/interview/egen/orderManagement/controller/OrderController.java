package com.interview.egen.orderManagement.controller;

import com.interview.egen.orderManagement.model.Order;
import com.interview.egen.orderManagement.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(
        path = "/order",
        produces = MediaType.APPLICATION_JSON_VALUE
        )
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class OrderController {

    @Autowired
    OrderService orderService;


    @RequestMapping("/all")
    public Flux<Order> getAllOrders() {
        return orderService.allOrders();
    }

    @GetMapping("/orderId/{orderId}")
    public Mono<Order> getOrder(@Valid @NotBlank @PathVariable("orderId") String id) {
        return  orderService.getOrderById(id);
    }

    @PostMapping("/saveOrder")
    public Mono<Order> saveOrder(@RequestBody Order order) {
        return orderService.saveCustomerOrder(order);
    }

    @DeleteMapping("/removeOrder/{orderId}")
    public Mono<Void> deleteOrder(@Valid @NotBlank @PathVariable("orderId") String orderId){
        return orderService.deleteOrderById(orderId);

    }



}
