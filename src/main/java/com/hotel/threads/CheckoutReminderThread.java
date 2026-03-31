package com.hotel.threads;
import com.hotel.io.LogManager;
import com.hotel.model.Booking;
import com.hotel.repository.BookingRepository;
import javafx.application.Platform;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Consumer;
public class CheckoutReminderThread implements Runnable {
    private final BookingRepository bookingRepository;
    private final LogManager        logger;
    private final Consumer<String>  alertCallback; 
    public CheckoutReminderThread(BookingRepository bookingRepo,
                                  LogManager logger,
                                  Consumer<String> alertCallback) {
        this.bookingRepository = bookingRepo;
        this.logger            = logger;
        this.alertCallback     = alertCallback;
    }
    @Override
    public void run() {
        logger.info("CheckoutReminderThread started.");
        while (!Thread.currentThread().isInterrupted()) {
            checkDueCheckouts();
            try {
                Thread.sleep(60_000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.info("CheckoutReminderThread shutting down.");
            }
        }
    }
    private void checkDueCheckouts() {
        PriorityQueue<Booking> queue = bookingRepository.getCheckoutQueue();
        synchronized (queue) {
            List<Booking> dueToday = bookingRepository.getDueToday();
            if (!dueToday.isEmpty()) {
                String msg = "⏰ " + dueToday.size() + " checkout(s) due today!";
                logger.warn(msg);
                Platform.runLater(() -> alertCallback.accept(msg));
            }
        } 
    }
}