package com.hotel.model;
import com.hotel.model.enums.RoomType;
public class SuiteRoom extends Room {
    private static final long serialVersionUID = 4L;
    private static final double LUXURY_TAX_RATE  = 0.18;   
    private static final double BUTLER_CHARGE    = 1500.0; 
    private static final double MINIBAR_CHARGE   = 800.0;  
    private boolean butlerService;
    private boolean minibarAccess;
    public SuiteRoom(String roomId, Integer floorNumber, Integer maxOccupancy,
                     String description, boolean butlerService, boolean minibarAccess) {
        super(roomId, floorNumber, RoomType.SUITE, maxOccupancy, description);
        this.butlerService = butlerService;
        this.minibarAccess = minibarAccess;
    }
    @Override
    public double calculateRate(int nights) {
        double base    = getPricePerNight() * nights;
        double butler  = butlerService ? BUTLER_CHARGE * nights : 0;
        double minibar = minibarAccess  ? MINIBAR_CHARGE * nights : 0;
        return base + butler + minibar;
    }
    @Override
    public double calculateRate(int nights, boolean includeServiceCharge) {
        double base = calculateRate(nights);
        return includeServiceCharge ? base + (base * LUXURY_TAX_RATE) : base;
    }
    public boolean isButlerService()             { return butlerService; }
    public void    setButlerService(boolean val) { this.butlerService = val; }
    public boolean isMinibarAccess()             { return minibarAccess; }
    public void    setMinibarAccess(boolean val) { this.minibarAccess = val; }
}