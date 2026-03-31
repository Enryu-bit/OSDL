package com.hotel;
import com.hotel.io.DataManager;
import com.hotel.io.InvoiceExporter;
import com.hotel.io.LogManager;
import com.hotel.io.RoomFileManager;
import com.hotel.model.Booking;
import com.hotel.model.Guest;
import com.hotel.model.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.GuestRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.BookingService;
import com.hotel.threads.*;
import com.hotel.ui.MainWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
public class Main extends Application {
    private final LogManager        logger          = new LogManager("data/hotel.log");
    private final ReentrantLock     fileLock        = new ReentrantLock();
    private final RoomRepository    roomRepo        = new RoomRepository();
    private final GuestRepository   guestRepo       = new GuestRepository();
    private final BookingRepository bookingRepo     = new BookingRepository();
    private final DataManager<Room>    roomDM       = new DataManager<>("data/rooms.dat");
    private final DataManager<Guest>   guestDM      = new DataManager<>("data/guests.dat");
    private final DataManager<Booking> bookingDM    = new DataManager<>("data/bookings.dat");
    private final RoomFileManager   roomFileMgr     = new RoomFileManager("data/rooms.raf");
    private final InvoiceExporter   invoiceExporter = new InvoiceExporter("data/invoices");
    private final BookingService    bookingService  = new BookingService(
            roomRepo, bookingRepo, roomFileMgr, logger);
    private Thread                      reminderThread;
    private Thread                      autoSaveThread;
    private OccupancyReporterThread     occupancyReporter;
    private BookingProcessorThread      bookingProcessor;
    private Thread                      bookingProcessorThread;
    private RoomStatusUpdaterThread     statusUpdater;
    private Thread                      statusUpdaterThread;
    @Override
    public void start(Stage primaryStage) throws Exception {
        loadAllData();
        MainWindow mainWindow = new MainWindow(
                roomRepo, guestRepo, bookingRepo,
                bookingService, invoiceExporter, logger);
        startAllThreads(mainWindow);
        primaryStage.setTitle("Grand Hotel Management System");
        primaryStage.setScene(mainWindow.buildScene());
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();
        logger.info("Application started successfully.");
    }
    @Override
    public void stop() throws Exception {
        logger.info("Application shutting down.");
        if (reminderThread   != null) reminderThread.interrupt();
        if (statusUpdater    != null) statusUpdater.stopGracefully();
        if (bookingProcessor != null) bookingProcessor.stop();
        if (occupancyReporter != null) occupancyReporter.stop();
        AutoSaveThread saver = new AutoSaveThread(
                roomRepo, guestRepo, bookingRepo,
                roomDM, guestDM, bookingDM, logger, fileLock);
        saver.performSave();
        logger.info("Shutdown complete.");
    }
    private void loadAllData() {
        try {
            List<Room>    rooms    = roomDM.load();
            List<Guest>   guests  = guestDM.load();
            List<Booking> bookings = bookingDM.load();
            rooms.forEach(roomRepo::add);
            guests.forEach(guestRepo::add);
            bookings.forEach(bookingRepo::add);
            List<Room> allRooms = roomRepo.getAll();
            for (int i = 0; i < allRooms.size(); i++) {
                bookingService.registerRoomIndex(allRooms.get(i).getRoomId(), i);
            }
            logger.info("Data loaded: " + rooms.size() + " rooms, "
                    + guests.size() + " guests, " + bookings.size() + " bookings.");
        } catch (Exception e) {
            logger.warn("No existing data found — starting fresh. (" + e.getMessage() + ")");
        }
    }
    private void startAllThreads(MainWindow mainWindow) {
        reminderThread = new Thread(
                new CheckoutReminderThread(bookingRepo, logger,
                        msg -> Platform.runLater(() -> mainWindow.showAlert(msg))),
                "CheckoutReminderThread");
        reminderThread.setDaemon(true);
        reminderThread.start();
        AutoSaveThread autoSave = new AutoSaveThread(
                roomRepo, guestRepo, bookingRepo,
                roomDM, guestDM, bookingDM, logger, fileLock);
        autoSaveThread = new Thread(autoSave, "AutoSaveThread");
        autoSaveThread.setDaemon(true);
        autoSaveThread.start();
        occupancyReporter = new OccupancyReporterThread(
                roomRepo, bookingRepo, logger,
                data -> Platform.runLater(() -> mainWindow.updateDashboard(data)));
        occupancyReporter.start();
        bookingProcessor = new BookingProcessorThread(
                bookingService, logger,
                result -> Platform.runLater(() -> mainWindow.showBookingResult(result)));
        bookingProcessorThread = new Thread(bookingProcessor, "BookingProcessorThread");
        bookingProcessorThread.setDaemon(true);
        bookingProcessorThread.start();
        statusUpdater = new RoomStatusUpdaterThread(
                roomFileMgr, roomRepo, logger,
                () -> Platform.runLater(mainWindow::refreshRoomTable));
        statusUpdaterThread = new Thread(statusUpdater, "RoomStatusUpdaterThread");
        statusUpdaterThread.setDaemon(true);
        statusUpdaterThread.start();
        logger.info("All 5 background threads started.");
    }
    public static void main(String[] args) {
        launch(args);
    }
}