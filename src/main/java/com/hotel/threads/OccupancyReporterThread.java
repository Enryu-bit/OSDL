package com.hotel.threads;
import com.hotel.io.LogManager;
import com.hotel.model.enums.RoomStatus;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import javafx.application.Platform;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
public class OccupancyReporterThread {
    private final ConcurrentHashMap<String, Double> reportData = new ConcurrentHashMap<>();
    private final AtomicInteger totalBookingsProcessed = new AtomicInteger(0);
    private final RoomRepository    roomRepo;
    private final BookingRepository bookingRepo;
    private final LogManager        logger;
    private final Consumer<ConcurrentHashMap<String, Double>> uiCallback;
    private ScheduledExecutorService executor;
    public OccupancyReporterThread(RoomRepository roomRepo,
                                   BookingRepository bookingRepo,
                                   LogManager logger,
                                   Consumer<ConcurrentHashMap<String, Double>> uiCallback) {
        this.roomRepo   = roomRepo;
        this.bookingRepo = bookingRepo;
        this.logger     = logger;
        this.uiCallback = uiCallback;
    }
    public void start() {
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "OccupancyReporterThread");
            t.setDaemon(true); 
            return t;
        });
        executor.scheduleAtFixedRate(this::compute, 0, 30, TimeUnit.SECONDS);
        logger.info("OccupancyReporterThread (ScheduledExecutorService) started.");
    }
    private void compute() {
        int    total       = roomRepo.count();
        long   occupied    = roomRepo.countByStatus(RoomStatus.OCCUPIED);
        long   booked      = roomRepo.countByStatus(RoomStatus.BOOKED);
        long   available   = roomRepo.countByStatus(RoomStatus.AVAILABLE);
        double revenue     = bookingRepo.getTotalRevenue();
        double occupancyPct = total > 0 ? ((double)(occupied + booked) / total) * 100 : 0;
        totalBookingsProcessed.incrementAndGet();
        reportData.put("total",      (double) total);
        reportData.put("occupied",   (double) occupied);
        reportData.put("booked",     (double) booked);
        reportData.put("available",  (double) available);
        reportData.put("revenue",    revenue);
        reportData.put("occupancyPct", occupancyPct);
        logger.info(String.format("Report: Occupancy=%.1f%%, Revenue=Rs.%.2f",
                occupancyPct, revenue));
        Platform.runLater(() -> uiCallback.accept(reportData));
    }
    public void stop() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
    public ConcurrentHashMap<String, Double> getReportData()       { return reportData; }
    public int getTotalBookingsProcessed()  { return totalBookingsProcessed.get(); }
}