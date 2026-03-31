package com.hotel.model;
import com.hotel.interfaces.Billable;
import com.hotel.model.enums.PaymentStatus;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
public class Booking implements Serializable, Billable, Comparable<Booking> {
    private static final long serialVersionUID = 6L;
    private String        bookingId;
    private String        guestId;
    private String        roomId;
    private LocalDate     checkInDate;
    private LocalDate     checkOutDate;
    private PaymentStatus paymentStatus;
    private Double        totalAmount;    
    private Long          numberOfNights; 
    private boolean       checkedIn;
    private boolean       checkedOut;
    public Booking(String bookingId, String guestId, String roomId,
                   LocalDate checkInDate, LocalDate checkOutDate) {
        this.bookingId     = bookingId;
        this.guestId       = guestId;
        this.roomId        = roomId;
        this.checkInDate   = checkInDate;
        this.checkOutDate  = checkOutDate;
        this.paymentStatus = PaymentStatus.PENDING;
        this.totalAmount   = 0.0;
        this.checkedIn     = false;
        this.checkedOut    = false;
        this.numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
    public void doCheckIn() {
        this.checkedIn = true;
    }
    public void doCheckOut(double finalAmount) {
        this.checkedOut    = true;
        this.totalAmount   = finalAmount; 
        this.paymentStatus = PaymentStatus.PAID;
    }
    @Override
    public double calculateTotalBill() {
        return totalAmount != null ? totalAmount : 0.0; 
    }
    @Override
    public String generateInvoiceSummary() {
        return String.format(
            "Booking: %s | Room: %s | Guest: %s | Nights: %d | Amount: Rs.%.2f | %s",
            bookingId, roomId, guestId, numberOfNights, totalAmount,
            paymentStatus.getDisplayName());
    }
    @Override
    public int compareTo(Booking other) {
        return this.checkOutDate.compareTo(other.checkOutDate);
    }
    public String        getBookingId()                          { return bookingId; }
    public void          setBookingId(String bookingId)          { this.bookingId = bookingId; }
    public String        getGuestId()                            { return guestId; }
    public void          setGuestId(String guestId)              { this.guestId = guestId; }
    public String        getRoomId()                             { return roomId; }
    public void          setRoomId(String roomId)                { this.roomId = roomId; }
    public LocalDate     getCheckInDate()                        { return checkInDate; }
    public void          setCheckInDate(LocalDate d)             { this.checkInDate = d; }
    public LocalDate     getCheckOutDate()                       { return checkOutDate; }
    public void          setCheckOutDate(LocalDate d)            { this.checkOutDate = d; }
    public PaymentStatus getPaymentStatus()                          { return paymentStatus; }
    public void          setPaymentStatus(PaymentStatus p)           { this.paymentStatus = p; }
    public Double        getTotalAmount()                        { return totalAmount; }
    public void          setTotalAmount(Double totalAmount)      { this.totalAmount = totalAmount; }
    public Long          getNumberOfNights()                     { return numberOfNights; }
    public void          setNumberOfNights(Long n)               { this.numberOfNights = n; }
    public boolean       isCheckedIn()                           { return checkedIn; }
    public void          setCheckedIn(boolean checkedIn)         { this.checkedIn = checkedIn; }
    public boolean       isCheckedOut()                          { return checkedOut; }
    public void          setCheckedOut(boolean checkedOut)       { this.checkedOut = checkedOut; }
    @Override
    public String toString() { return generateInvoiceSummary(); }
}