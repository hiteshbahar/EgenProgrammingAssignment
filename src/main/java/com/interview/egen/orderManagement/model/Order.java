package com.interview.egen.orderManagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.FieldDefaults;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
@FieldNameConstants
@Document
public class Order {
    @With
    @Id
    String id;
    String status;
    String customerId;
    List<Item> items;
    Chargers chargers;
    PaymentDetails paymentDetails;
    Address billingAddress;
    Address shippingAddress;
}
