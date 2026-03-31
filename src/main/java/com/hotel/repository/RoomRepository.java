package com.hotel.repository;
import com.hotel.model.Room;
import com.hotel.model.enums.RoomStatus;
import com.hotel.model.enums.RoomType;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
public class RoomRepository extends Repository<Room> {
    private final TreeMap<String, Room> sortedRooms = new TreeMap<>();
    @Override
    public void add(Room room) {
        super.add(room);                          
        sortedRooms.put(room.getRoomId(), room);  
    }
    @Override
    public boolean remove(String id) {
        sortedRooms.remove(id);
        return super.remove(id);
    }
    @Override
    protected String getId(Room room) {
        return room.getRoomId();
    }
    @Override
    public List<Room> search(String query) {
        String lq = query.toLowerCase();
        return store.values().stream()
                .filter(r -> r.getRoomId().toLowerCase().contains(lq)
                        || r.getType().getDisplayName().toLowerCase().contains(lq)
                        || r.getDescription().toLowerCase().contains(lq))
                .collect(Collectors.toList());
    }
    public List<Room> getAvailableRooms() {
        return store.values().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .collect(Collectors.toList());
    }
    public List<Room> getByType(RoomType type) {
        return store.values().stream()
                .filter(r -> r.getType() == type)
                .collect(Collectors.toList());
    }
    public List<Room> getByStatus(RoomStatus status) {
        return store.values().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }
    public List<Room> getAllSorted() {
        return new ArrayList<>(sortedRooms.values());
    }
    public long countByStatus(RoomStatus status) {
        return store.values().stream()
                .filter(r -> r.getStatus() == status)
                .count();
    }
}