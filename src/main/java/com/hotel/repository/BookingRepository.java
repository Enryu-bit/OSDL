package com.hotel.repository;
import com.hotel.model.Booking;
import java.time.LocalDate;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
public class BookingRepository extends Repository<Booking> {
    private final PriorityQueue<Booking> checkoutQueue = new PriorityQueue<>();
    @Override
    public void add(Booking booking) {
        super.add(booking);                
        checkoutQueue.offer(booking);      
    }
    @Override
    protected String getId(Booking booking) {
        return booking.getBookingId();
    }
    @Override
    public List<Booking> search(String query) {
        String lq = query.toLowerCase();
        return store.values().stream()
                .filter(b -> b.getBookingId().toLowerCase().contains(lq)
                        || b.getGuestId().toLowerCase().contains(lq)
                        || b.getRoomId().toLowerCase().contains(lq))
                .collect(Collectors.toList());
    }
    public List<Booking> getByGuestId(String guestId) {
        return store.values().stream()
                .filter(b -> b.getGuestId().equals(guestId))
                .collect(Collectors.toList());
    }
    public List<Booking> getByRoomId(String roomId) {
        return store.values().stream()
                .filter(b -> b.getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }
    public List<Booking> getDueToday() {
        LocalDate today = LocalDate.now();
        return store.values().stream()
                .filter(b -> b.getCheckOutDate().equals(today) && !b.isCheckedOut())
                .collect(Collectors.toList());
    }
    public List<Booking> getActiveBookings() {
        return store.values().stream()
                .filter(b -> !b.isCheckedOut())
                .collect(Collectors.toList());
    }
    public PriorityQueue<Booking> getCheckoutQueue() {
        return checkoutQueue;
    }
    public double getTotalRevenue() {
        return store.values().stream()
                .filter(Booking::isCheckedOut)
                .mapToDouble(Booking::getTotalAmount)  
                .sum();
    }
}