package com.hotel.model;
import com.hotel.model.enums.RoomType;
public class DeluxeRoom extends Room {
    private static final long serialVersionUID = 3L;
    private static final double SERVICE_CHARGE_RATE = 0.12; 
    private static final double BREAKFAST_CHARGE    = 500.0; 
    private boolean breakfastIncluded;
    public DeluxeRoom(String roomId, Integer floorNumber,
                      Integer maxOccupancy, String description,
                      boolean breakfastIncluded) {
        super(roomId, floorNumber, RoomType.DELUXE, maxOccupancy, description);
        this.breakfastIncluded = breakfastIncluded;
    }
    @Override
    public double calculateRate(int nights) {
        double base      = getPricePerNight() * nights;
        double breakfast = breakfastIncluded ? BREAKFAST_CHARGE * nights : 0;
        return base + breakfast;
    }
    @Override
    public double calculateRate(int nights, boolean includeServiceCharge) {
        double base = calculateRate(nights); 
        return includeServiceCharge ? base + (base * SERVICE_CHARGE_RATE) : base;
    }
    public boolean isBreakfastIncluded()                        { return breakfastIncluded; }
    public void    setBreakfastIncluded(boolean val)            { this.breakfastIncluded = val; }
}