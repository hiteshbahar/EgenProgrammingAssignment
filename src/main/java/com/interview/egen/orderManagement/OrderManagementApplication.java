package com.interview.egen.orderManagement;

import com.interview.egen.orderManagement.model.*;
import com.interview.egen.orderManagement.repository.OrderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class OrderManagementApplication {

    @Bean
    CommandLineRunner order(OrderRepository orderRepository) {

        return args -> orderRepository
                .deleteAll()
                .subscribe(null, null, () -> Stream.of(buildOrderForDataBase())
                        .forEach(order -> orderRepository
                                .save(order)
                                .subscribe(System.out::println)));
    }

    private Order buildOrderForDataBase() {
        return Order.builder()
                .id(UUID.randomUUID().toString())
                .status("confirmed")
                .customerId("hit1234")
                .items(Stream.of(Item.builder()
                                .itemName("Oats")
                                .qty(2)
                                .build(),
                        Item.builder()
                                .itemName("Milk")
                                .qty(1)
                                .build(),
                        Item.builder()
                                .itemName("Banana")
                                .qty(6)
                                .build(),
                        Item.builder()
                                .itemName("Eggs")
                                .qty(12)
                                .build())
                        .collect(Collectors.toList()))
                .chargers(Chargers.builder()
                        .shippingCharges(5.0)
                        .subtotal(30)
                        .tax(2.0)
                        .total(37)
                        .build())
                .paymentDetails(PaymentDetails.builder()
                        .paymentMethod("Credit_Card")
                        .paymentConfirmationNumber(String.valueOf(new Random().nextInt(999999)))
                        .paymentDate(LocalDateTime.now())
                        .build())
                .billingAddress(Address.builder()
                        .country("USA")
                        .city("Pullman")
                        .postalCode(99163)
                        .state("WA")
                        .streetAddress(Stream.of(
                                "1234 NE Drive",
                                "Unit #1")
                                .collect(Collectors.toList())
                        ).build())
                .shippingAddress(Address.builder()
                        .country("USA")
                        .city("Pullman")
                        .postalCode(99163)
                        .state("WA")
                        .streetAddress(Stream.of(
                                "1234 NE Drive",
                                "Unit #1")
                                .collect(Collectors.toList())
                        ).build())
                .build();
    }


    public static void main(String[] args) {
        SpringApplication.run(OrderManagementApplication.class, args);
    }
}
