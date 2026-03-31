package com.hotel.io;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
public class DataManager<T extends Serializable> {
    private final String filePath;
    public DataManager(String filePath) {
        this.filePath = filePath;
        ensureDirectoryExists(filePath);
    }
    public void save(List<T> items) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(items); 
        }
    }
    @SuppressWarnings("unchecked")
    public List<T> load() throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<T>) ois.readObject(); 
        }
    }
    private void ensureDirectoryExists(String path) {
        File parent = new File(path).getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs(); 
        }
    }
    public String getFilePath() { return filePath; }
}