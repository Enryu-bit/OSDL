package com.hotel.model;
import com.hotel.model.enums.RoomType;
public class StandardRoom extends Room {
    private static final long serialVersionUID = 2L;
    private static final double SERVICE_CHARGE_RATE = 0.08;
    public StandardRoom(String roomId, Integer floorNumber,
                        Integer maxOccupancy, String description) {
        super(roomId, floorNumber, RoomType.STANDARD, maxOccupancy, description);
    }
    @Override
    public double calculateRate(int nights) {
        return getPricePerNight() * nights;
    }
    @Override
    public double calculateRate(int nights, boolean includeServiceCharge) {
        double base = calculateRate(nights);               
        if (includeServiceCharge) {
            return base + (base * SERVICE_CHARGE_RATE);   
        }
        return base;
    }
}