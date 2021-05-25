package com.interview.egen.orderManagement.serviceTest;

import com.interview.egen.orderManagement.model.*;
import com.interview.egen.orderManagement.repository.OrderRepository;
import com.interview.egen.orderManagement.service.OrderService;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = PRIVATE)
public class OrderServiceTest {

    @Spy
    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;

    final static String ORDER_ID = "orderId";
    final static String Customer_ID = "customerId";

     final Order testOrder =  Order.builder()
                .id(ORDER_ID)
                .status("confirmed")
                .customerId(Customer_ID)
                .items(getItemList())
                .chargers(getChargers())
                .paymentDetails(getPaymentDetails())
                .billingAddress(getAddress())
                .shippingAddress(getAddress())
                .build();

    private Address getAddress() {
        return Address.builder()
                .country("USA")
                .city("Pullman")
                .postalCode(99163)
                .state("WA")
                .streetAddress(Stream.of(
                        "1234 NE Drive",
                        "Unit #1")
                        .collect(Collectors.toList())
                ).build();
    }

    private PaymentDetails getPaymentDetails() {
        return PaymentDetails.builder()
                .paymentMethod("Credit_Card")
                .paymentConfirmationNumber(String.valueOf(new Random().nextInt(999999)))
                .paymentDate(LocalDateTime.now())
                .build();
    }

    private Chargers getChargers() {
        return Chargers.builder()
                .shippingCharges(5.0)
                .subtotal(30)
                .tax(2.0)
                .total(37)
                .build();
    }

    private List<Item> getItemList() {
        return Stream.of(Item.builder()
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
                .collect(Collectors.toList());
    }


    @Test
    public void shouldRelayFailureWhenNoOrderIdFound() {
        //Given
        given(orderRepository.findById(anyString()))
                .willReturn(Mono.empty());
        // When & Then
        StepVerifier.create(orderService.getOrderById("testOrderId"))
                .verifyErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(ResponseStatusException.class)
                        .hasMessage("404 NOT_FOUND \"testOrderId not found\""));

    }

    @Test
    public void shouldRelayFailureWhenNoOrdersFound() {
        //Given
        given(orderRepository.findAll())
                .willReturn(Flux.empty());
        // When & Then
        StepVerifier.create(orderService.allOrders())
                .verifyErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(ResponseStatusException.class)
                        .hasMessage("404 NOT_FOUND \"No orders at all\""));
    }

    @Test
    public void shouldReturnOrderWhenFindByOrderId() {
        // Given
        given(orderRepository.findById(ORDER_ID)).willReturn(Mono.just(testOrder));

        //When
        StepVerifier.create(orderService.getOrderById(ORDER_ID))

                //Then
                .expectNext(testOrder)
                .verifyComplete();
    }

    @Test
    public void shouldSaveOrder() {
        // Given
        given(orderRepository.save(any()))
                .willAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // when
        StepVerifier.create(orderService.saveCustomerOrder(testOrder))

        // Then
        .expectNext(testOrder).verifyComplete();
    }
}
