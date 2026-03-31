package com.hotel.model.enums;
public enum PaymentStatus {
    PENDING,    
    PAID,       
    REFUNDED,   
    CANCELLED;  
    public String getDisplayName() {
        String name = this.name(); 
        return name.charAt(0) + name.substring(1).toLowerCase(); 
    }
    @Override
    public String toString() { return getDisplayName(); }
}