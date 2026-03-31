package com.hotel.interfaces;
import java.util.List;
public interface Searchable<T> {
    List<T> search(String query);
}