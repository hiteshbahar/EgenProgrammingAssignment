package com.interview.egen.orderManagement.util;

import com.interview.egen.orderManagement.model.Address;
import com.interview.egen.orderManagement.model.Chargers;
import com.interview.egen.orderManagement.model.Order;
import com.interview.egen.orderManagement.model.PaymentDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class Calculation {

    public static Order toPersistence(Order order){
        if (order == null)
            return null;
        // calculate Total = Tax + shipping + subtotal
        order.setChargers(CalculateTotal(order.getChargers(), order.getShippingAddress()));
        // set Purchase date as local date
        order.setPaymentDetails(PaymentDetails.builder()
                .paymentMethod(order.getPaymentDetails().getPaymentMethod())
                .paymentDate(LocalDateTime.now())
                .paymentConfirmationNumber(String.valueOf(new Random().nextInt(999999)))
                .build());
        return order;
    }

    private static Chargers CalculateTotal(Chargers chargers, Address shippingAddress) {
        if(shippingAddress == null)
            return null;
        chargers.setTax(OrderCharges.getTaxesBasedOnZipCode(
                shippingAddress.getPostalCode(), chargers.getSubtotal())
        );
        chargers.setShippingCharges(OrderCharges.getShippingCharge(
                shippingAddress.getPostalCode())
        );
        chargers.setTotal(
                chargers.getSubtotal() +
                chargers.getTax() +
                chargers.getShippingCharges()
        );
        return chargers;
    }
}
