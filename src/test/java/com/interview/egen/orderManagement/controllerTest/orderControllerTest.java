package com.interview.egen.orderManagement.controllerTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.interview.egen.orderManagement.model.*;
import com.interview.egen.orderManagement.service.OrderService;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith({SpringExtension.class})
@WebFluxTest/*(controllers = OrderController.class)*/
//@Import(OrderService.class)
public class orderControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    OrderService orderService;

    @Configuration
//    @Import(OrderService.class)
    static class Config {
    }

    final static String ORDER_ID = "testOrderId";
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
    void getOrdersShouldReturnNotFoundError() {
        //Given
        given(orderService.getOrderById(anyString()))
                .willReturn(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found")));
        // When & Then
        webTestClient.get()
                .uri("/order/orderId/orderId")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(String.class)
                .returnResult();
    }


    @Test
    void getOrderShouldReturnPositive() throws JsonProcessingException, JSONException {
        //Given
        given(orderService.getOrderById(anyString()))
                .willReturn(Mono.just(testOrder));

        // When
        String actualBody = webTestClient.get()
                .uri("/order/orderId/" + ORDER_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        System.out.println(":"+actualBody);

        // Then
        String expectedBody = OBJECT_MAPPER.writeValueAsString(testOrder);
        JSONAssert.assertEquals(expectedBody, actualBody, true);
    }

    @Test
    void saveOrderTest() throws JsonProcessingException, JSONException {
        given(orderService.saveCustomerOrder(testOrder))
                .willReturn(Mono.just(testOrder));

        String actualBoy = webTestClient.post()
                .uri("/order/saveOrder")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(testOrder)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String expectedBody = OBJECT_MAPPER.writeValueAsString(testOrder);
        JSONAssert.assertEquals(expectedBody, actualBoy, true);
    }

    @Test
    public void testHelloWorld() {
        webTestClient.get()
                .uri("/orders/")
                .header("Content-type", "application/json")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isEqualTo("HelloWorld");
    }


}
