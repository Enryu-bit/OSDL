package com.hotel.model.enums;
public enum RoomType {
    STANDARD("Standard", 1500.0),
    DELUXE("Deluxe",     3000.0),
    SUITE("Suite",       6000.0);
    private final String displayName;
    private final double baseRate;
    RoomType(String displayName, double baseRate) {
        this.displayName = displayName;
        this.baseRate    = baseRate;
    }
    public String getDisplayName() { return displayName; }
    public double getBaseRate()    { return baseRate; }
    @Override
    public String toString() { return displayName; }
}