package com.hotel.threads;
import com.hotel.io.DataManager;
import com.hotel.io.LogManager;
import com.hotel.model.Booking;
import com.hotel.model.Guest;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.GuestRepository;
import com.hotel.repository.RoomRepository;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
public class AutoSaveThread implements Runnable {
    private final RoomRepository    roomRepo;
    private final GuestRepository   guestRepo;
    private final BookingRepository bookingRepo;
    private final DataManager<Room>    roomDM;
    private final DataManager<Guest>   guestDM;
    private final DataManager<Booking> bookingDM;
    private final LogManager        logger;
    private final ReentrantLock fileLock;
    public AutoSaveThread(RoomRepository roomRepo, GuestRepository guestRepo,
                          BookingRepository bookingRepo,
                          DataManager<Room> roomDM, DataManager<Guest> guestDM,
                          DataManager<Booking> bookingDM,
                          LogManager logger, ReentrantLock fileLock) {
        this.roomRepo   = roomRepo;   this.guestRepo   = guestRepo;
        this.bookingRepo = bookingRepo;
        this.roomDM     = roomDM;     this.guestDM     = guestDM;
        this.bookingDM  = bookingDM;
        this.logger     = logger;     this.fileLock    = fileLock;
    }
    @Override
    public void run() {
        logger.info("AutoSaveThread started (daemon=true).");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(5 * 60 * 1000); 
                performSave();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("AutoSaveThread interrupted — performing final save.");
                performSave(); 
            }
        }
    }
    public void performSave() {
        try {
            if (fileLock.tryLock(3, TimeUnit.SECONDS)) {
                try {
                    roomDM.save(roomRepo.getAll());
                    guestDM.save(guestRepo.getAll());
                    bookingDM.save(bookingRepo.getAll());
                    logger.info("AutoSave completed successfully.");
                } catch (Exception e) {
                    logger.error("AutoSave failed: " + e.getMessage());
                } finally {
                    fileLock.unlock(); 
                }
            } else {
                logger.warn("AutoSave skipped — could not acquire file lock in 3s.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}