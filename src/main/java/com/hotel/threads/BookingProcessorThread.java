package com.hotel.threads;
import com.hotel.io.LogManager;
import com.hotel.service.BookingService;
import javafx.application.Platform;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
public class BookingProcessorThread implements Runnable {
    private static class BookingRequest {
        final String guestId, roomId, checkIn, checkOut;
        BookingRequest(String guestId, String roomId, String checkIn, String checkOut) {
            this.guestId  = guestId;  this.roomId  = roomId;
            this.checkIn  = checkIn;  this.checkOut = checkOut;
        }
    }
    private final Queue<BookingRequest> taskQueue = new LinkedList<>();
    private final BookingService   bookingService;
    private final LogManager       logger;
    private final Consumer<String> resultCallback; 
    private volatile boolean       running = true;
    public BookingProcessorThread(BookingService bookingService,
                                  LogManager logger,
                                  Consumer<String> resultCallback) {
        this.bookingService = bookingService;
        this.logger         = logger;
        this.resultCallback = resultCallback;
    }
    public void submitRequest(String guestId, String roomId,
                              String checkIn, String checkOut) {
        synchronized (taskQueue) {           
            taskQueue.add(new BookingRequest(guestId, roomId, checkIn, checkOut));
            taskQueue.notifyAll();           
        }                                    
    }
    @Override
    public void run() {
        logger.info("BookingProcessorThread started (Producer-Consumer).");
        while (running) {
            BookingRequest request;
            synchronized (taskQueue) {       
                while (taskQueue.isEmpty() && running) {
                    try {
                        taskQueue.wait();    
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (!running) break;
                request = taskQueue.poll();  
            }                                
            if (request != null) {
                process(request);            
            }
        }
    }
    private void process(BookingRequest req) {
        String result = bookingService.bookRoomAsync(
                req.guestId, req.roomId, req.checkIn, req.checkOut);
        logger.info("BookingProcessor result: " + result);
        Platform.runLater(() -> resultCallback.accept(result));
    }
    public void stop() {
        running = false;
        synchronized (taskQueue) {
            taskQueue.notifyAll(); 
        }
    }
}