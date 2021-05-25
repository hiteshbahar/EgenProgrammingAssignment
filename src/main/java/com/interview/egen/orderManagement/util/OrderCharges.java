package com.interview.egen.orderManagement.util;

public class OrderCharges {

    public static double getTaxesBasedOnZipCode(int postalCode, double subtotal) {
        if (postalCode == 0) {
            return 0.0;
        }
        else if (postalCode >= 100000 && postalCode < 200000)
            return subtotal * 0.1;
        else if (postalCode >= 200000 && postalCode < 400000)
            return  subtotal * 0.15;
        else
            return subtotal *0.08;
    }

    public static double getShippingCharge(int postalCode)  {
        if(postalCode == 0)
            return 0;
        else if (postalCode >= 200000 && postalCode < 400000)
            return 10;
        else
            return 5;
    }

}
