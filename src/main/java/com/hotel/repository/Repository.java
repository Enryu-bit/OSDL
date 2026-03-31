package com.hotel.repository;
import com.hotel.interfaces.Searchable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
public abstract class Repository<T> implements Searchable<T> {
    protected final Map<String, T> store = new HashMap<>();
    public void add(T item) {
        store.put(getId(item), item);
    }
    public boolean remove(String id) {
        return store.remove(id) != null;
    }
    public Optional<T> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
    public List<T> getAll() {
        return new ArrayList<>(store.values());
    }
    public boolean exists(String id) {
        return store.containsKey(id);
    }
    public int count() {
        return store.size();
    }
    public void clear() {
        store.clear();
    }
    protected abstract String getId(T item);
    @Override
    public abstract List<T> search(String query);
}