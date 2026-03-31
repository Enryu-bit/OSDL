package com.hotel.io;
import com.hotel.model.Booking;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class InvoiceExporter {
    private final String invoiceDir;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    public InvoiceExporter(String invoiceDir) {
        this.invoiceDir = invoiceDir;
        new File(invoiceDir).mkdirs(); 
    }
    public String exportInvoice(Booking booking, String guestName,
                                String roomDetails, double amount) throws IOException {
        String fileName = invoiceDir + File.separator
                + "INV_" + booking.getBookingId() + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writeLine(writer, "=".repeat(52));
            writeLine(writer, "        GRAND HOTEL — INVOICE");
            writeLine(writer, "=".repeat(52));
            writeLine(writer, "Invoice No  : INV_" + booking.getBookingId());
            writeLine(writer, "Date        : " + LocalDateTime.now().format(FMT));
            writeLine(writer, "-".repeat(52));
            writeLine(writer, "Guest Name  : " + guestName);
            writeLine(writer, "Booking ID  : " + booking.getBookingId());
            writeLine(writer, "Room        : " + roomDetails);
            writeLine(writer, "Check-In    : " + booking.getCheckInDate());
            writeLine(writer, "Check-Out   : " + booking.getCheckOutDate());
            writeLine(writer, "No. Nights  : " + booking.getNumberOfNights());
            writeLine(writer, "-".repeat(52));
            writeLine(writer, String.format("%-35s Rs. %,10.2f", "TOTAL AMOUNT:", amount));
            writeLine(writer, "=".repeat(52));
            writeLine(writer, "  Thank you for staying at Grand Hotel!");
            writeLine(writer, "=".repeat(52));
        } 
        return fileName;
    }
    private void writeLine(BufferedWriter writer, String line) throws IOException {
        writer.write(line);    
        writer.newLine();      
    }
}