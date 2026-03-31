package com.hotel.model.enums;
public enum RoomStatus {
    AVAILABLE  ("Available",   "#4CAF50"),   
    BOOKED     ("Booked",      "#FF9800"),   
    OCCUPIED   ("Occupied",    "#F44336"),   
    MAINTENANCE("Maintenance", "#9E9E9E");   
    private final String displayName;
    private final String colorHex;
    RoomStatus(String displayName, String colorHex) {
        this.displayName = displayName;
        this.colorHex    = colorHex;
    }
    public String getDisplayName() { return displayName; }
    public String getColorHex()    { return colorHex; }
    @Override
    public String toString() { return displayName; }
}