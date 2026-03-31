package com.hotel.threads;
import com.hotel.io.LogManager;
import com.hotel.io.RoomFileManager;
import com.hotel.repository.RoomRepository;
import javafx.application.Platform;
public class RoomStatusUpdaterThread implements Runnable {
    private volatile boolean running = true;
    private final RoomFileManager roomFileManager;
    private final RoomRepository  roomRepo;
    private final LogManager      logger;
    private final Runnable        uiRefreshCallback;
    private Thread                thread; 
    public RoomStatusUpdaterThread(RoomFileManager roomFileManager,
                                   RoomRepository roomRepo,
                                   LogManager logger,
                                   Runnable uiRefreshCallback) {
        this.roomFileManager   = roomFileManager;
        this.roomRepo          = roomRepo;
        this.logger            = logger;
        this.uiRefreshCallback = uiRefreshCallback;
    }
    @Override
    public void run() {
        thread = Thread.currentThread();
        logger.info("RoomStatusUpdaterThread started. Monitoring RAF file.");
        while (running) {           
            checkForUpdates();
            try {
                Thread.sleep(10_000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        logger.info("RoomStatusUpdaterThread stopped gracefully.");
    }
    public void stopGracefully() {
        this.running = false;       
        if (thread != null) thread.interrupt(); 
    }
    private void checkForUpdates() {
        try {
            int records = roomFileManager.getTotalRecords();
            if (records > 0) {
                Platform.runLater(uiRefreshCallback);
            }
        } catch (Exception e) {
            if (running) logger.error("RoomStatusUpdater error: " + e.getMessage());
        }
    }
    public Thread.State getThreadState() {
        return thread != null ? thread.getState() : Thread.State.NEW;
    }
    public boolean isRunning() { return running; }
}