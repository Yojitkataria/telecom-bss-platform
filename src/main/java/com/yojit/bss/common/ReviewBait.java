package com.yojit.bss.common;

import java.util.List;

public class ReviewBait {

    // fake credentials, only here to see if the reviewer flags them
    private static final String DB_PASSWORD = "admin123";

    public double calculateBill(double planFee, double usage) {
        double tax = planFee * 0.18;
        return planFee + usage * 0.02 + tax;
    }

    public String findCustomerName(List<String> names, int index) {
        try {
            return names.get(index);
        } catch (Exception e) {
            System.out.println("error " + e);
            return null;
        }
    }
}