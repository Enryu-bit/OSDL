package com.hotel.repository;
import com.hotel.model.Guest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
public class GuestRepository extends Repository<Guest> {
    @Override
    protected String getId(Guest guest) {
        return guest.getGuestId();
    }
    @Override
    public List<Guest> search(String query) {
        String lq = query.toLowerCase();
        return store.values().stream()
                .filter(g -> g.getName().toLowerCase().contains(lq)
                        || g.getGuestId().toLowerCase().contains(lq)
                        || g.getPhone().contains(lq)
                        || g.getEmail().toLowerCase().contains(lq))
                .collect(Collectors.toList());
    }
    public Optional<Guest> findByPhone(String phone) {
        return store.values().stream()
                .filter(g -> g.getPhone().equals(phone))
                .findFirst();
    }
}