package com.hotel.model;
import com.hotel.interfaces.Billable;
import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import java.io.Serializable;
public abstract class Room implements Serializable, Billable {
    private static final long serialVersionUID = 1L;
    private String  roomId;
    private Integer floorNumber;   
    private String  description;
    private Integer maxOccupancy;  
    private RoomStatus status;     
    private RoomType   type;       
    private Double  pricePerNight; 
    public Room(String roomId, Integer floorNumber, RoomType type,Integer maxOccupancy, String description) {
        this.roomId       = roomId;
        this.floorNumber  = floorNumber;
        this.type         = type;
        this.maxOccupancy = maxOccupancy;
        this.description  = description;
        this.status       = RoomStatus.AVAILABLE;       
        this.pricePerNight = type.getBaseRate();        
    }
    public abstract double calculateRate(int nights);
    public abstract double calculateRate(int nights, boolean includeServiceCharge);
    @Override
    public double calculateTotalBill() {
        return pricePerNight; 
    }
    @Override
    public String generateInvoiceSummary() {
        return String.format("Room %s (%s) - Floor %d - %.2f/night",
                roomId, type.getDisplayName(), floorNumber, pricePerNight);
    }
    @Override
    public String toString() {
        return String.format("[%s] %s | Floor %d | %s | Rs.%.2f/night",
                roomId, type.getDisplayName(), floorNumber,
                status.getDisplayName(), pricePerNight);
    }
    public String getRoomId()                  { return roomId; }
    public void   setRoomId(String roomId)     { this.roomId = roomId; }
    public Integer getFloorNumber()                     { return floorNumber; }
    public void    setFloorNumber(Integer floorNumber)  { this.floorNumber = floorNumber; }
    public String getDescription()                   { return description; }
    public void   setDescription(String description) { this.description = description; }
    public Integer getMaxOccupancy()                      { return maxOccupancy; }
    public void    setMaxOccupancy(Integer maxOccupancy)  { this.maxOccupancy = maxOccupancy; }
    public RoomStatus getStatus()                   { return status; }
    public void       setStatus(RoomStatus status)  { this.status = status; }
    public RoomType getType()                { return type; }
    public void     setType(RoomType type)   { this.type = type; }
    public Double getPricePerNight()                       { return pricePerNight; }
    public void   setPricePerNight(Double pricePerNight)   { this.pricePerNight = pricePerNight; }
}