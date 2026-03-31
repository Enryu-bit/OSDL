package com.hotel.service;
import com.hotel.io.LogManager;
import com.hotel.io.RoomFileManager;
import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.enums.RoomStatus;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class BookingService {
    private final RoomRepository    roomRepo;
    private final BookingRepository bookingRepo;
    private final RoomFileManager   roomFileManager;
    private final LogManager        logger;
    private final ConcurrentHashMap<String, Integer> roomIndexMap = new ConcurrentHashMap<>();
    public BookingService(RoomRepository roomRepo, BookingRepository bookingRepo,
                          RoomFileManager roomFileManager, LogManager logger) {
        this.roomRepo        = roomRepo;
        this.bookingRepo     = bookingRepo;
        this.roomFileManager = roomFileManager;
        this.logger          = logger;
    }
    public synchronized String bookRoom(String guestId, String roomId,
                                        LocalDate checkIn, LocalDate checkOut) {
        Optional<Room> roomOpt = roomRepo.findById(roomId);
        if (roomOpt.isEmpty()) return "ERROR: Room not found.";
        Room room = roomOpt.get();
        if (room.getStatus() != RoomStatus.AVAILABLE) {
            return "ERROR: Room " + roomId + " is not available ("
                    + room.getStatus().getDisplayName() + ").";
        }
        String bookingId = "BK" + UUID.randomUUID().toString()
                                      .substring(0, 6).toUpperCase();
        Booking booking = new Booking(bookingId, guestId, roomId, checkIn, checkOut);
        bookingRepo.add(booking);
        room.setStatus(RoomStatus.BOOKED);
        updateRoomInRAF(roomId, RoomStatus.BOOKED);
        logger.info("Booked: Room=" + roomId + " Guest=" + guestId
                + " BookingID=" + bookingId);
        return "SUCCESS: Booking " + bookingId + " confirmed.";
    }
    public String bookRoomAsync(String guestId, String roomId,
                                String checkIn, String checkOut) {
        return bookRoom(guestId, roomId,
                LocalDate.parse(checkIn), LocalDate.parse(checkOut));
    }
    public synchronized String checkIn(String bookingId) {
        Optional<Booking> bOpt = bookingRepo.findById(bookingId);
        if (bOpt.isEmpty())        return "ERROR: Booking not found.";
        if (bOpt.get().isCheckedIn()) return "ERROR: Already checked in.";
        Booking booking = bOpt.get();
        booking.doCheckIn();
        roomRepo.findById(booking.getRoomId()).ifPresent(r -> {
            r.setStatus(RoomStatus.OCCUPIED);
            updateRoomInRAF(r.getRoomId(), RoomStatus.OCCUPIED);
        });
        logger.info("Check-in complete: Booking=" + bookingId);
        return "SUCCESS: Checked in for booking " + bookingId + ".";
    }
    public synchronized String checkOut(String bookingId) {
        Optional<Booking> bOpt = bookingRepo.findById(bookingId);
        if (bOpt.isEmpty())          return "ERROR: Booking not found.";
        if (bOpt.get().isCheckedOut()) return "ERROR: Already checked out.";
        Booking booking = bOpt.get();
        Optional<Room> roomOpt = roomRepo.findById(booking.getRoomId());
        if (roomOpt.isEmpty()) return "ERROR: Room data missing.";
        Room room = roomOpt.get();
        double total = room.calculateRate(booking.getNumberOfNights().intValue(), true);
        booking.doCheckOut(total);
        room.setStatus(RoomStatus.AVAILABLE);
        updateRoomInRAF(room.getRoomId(), RoomStatus.AVAILABLE);
        logger.info("Checkout: Booking=" + bookingId
                + " Amount=Rs." + String.format("%.2f", total));
        return "SUCCESS: Checkout complete. Total: Rs." + String.format("%.2f", total);
    }
    private void updateRoomInRAF(String roomId, RoomStatus status) {
        Integer idx = roomIndexMap.get(roomId);
        if (idx == null) return;
        try {
            roomFileManager.updateStatus(idx, status.getDisplayName());
        } catch (Exception e) {
            logger.error("RAF update error for " + roomId + ": " + e.getMessage());
        }
    }
    public void registerRoomIndex(String roomId, int index) {
        roomIndexMap.put(roomId, index);
    }
    public List<Booking> getAllBookings() { return bookingRepo.getAll(); }
    public BookingRepository getBookingRepository() { return bookingRepo; }
}