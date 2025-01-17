import java.util.HashMap;

public class Store {
    private HashMap<String, Record> storage;

    public Store() {
        storage = new HashMap<>();
    }

    public void addItem(String key, String value) {
        Record record = new Record(value);
        storage.put(key, record);
    }

    public void addItem(String key, String value, String expiry) {
        Record record = new Record(value, expiry);
        storage.put(key, record);
    }

    public Record getItem(String key) {
        return storage.get(key);
    }

    public boolean containsItem(String key) {
        return storage.containsKey(key);
    }

    public void removeItem(String key) {
        storage.remove(key);
    }

    public int getSize() {
        return storage.size();
    }

    public void clearStorage() {
        storage.clear();
    }

    public void displayItems() {
        if (storage.isEmpty()) {
            System.out.println("Storage is empty.");
        } else {
            System.out.println("Storage contains:");
            storage.forEach((key, value) -> System.out.println("Key : " + key + " Value : " + value));
        }
    }
}
