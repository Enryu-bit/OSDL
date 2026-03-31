package com.hotel.interfaces;
public interface Billable {
    double calculateTotalBill();
    String generateInvoiceSummary();
}