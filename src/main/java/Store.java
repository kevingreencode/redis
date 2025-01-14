import java.util.HashMap;

public class Store {
    private HashMap<String,String> storage;

    public Store(){
        storage = new HashMap<>();
    }

    public void addItem(String key, String value){
        storage.put(key,value);
    }
    
    public String getItem(String key){
        return storage.get(key);
    }

    public boolean containsItem(String key){
        return storage.containsKey(key);
    }

    public String removeItem(String key){
        return storage.remove(key);
    }

    public int getSize(){
        return storage.size();
    }

    public void clearStorage(){
        storage.clear();
    }

    public void displayItems(){
        if (storage.isEmpty()){
            System.out.println("Storage is empty.");
        } else {
            System.out.println("Storage contains:");
            storage.forEach((key, value) -> System.out.println("Key : " + key + " Value : " + value));
        }
    }
}
