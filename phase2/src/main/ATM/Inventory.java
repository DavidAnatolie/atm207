package ATM;

import java.util.HashMap;

//Where users can store non-money goods. Stored in a HashMap with the string name of the good (Steel, Copper,
//etc.), with its value being the amount of it.
public class Inventory {
    HashMap<String, Integer> storage = new HashMap<>();

    public Inventory() {
    }

    Inventory(HashMap<String, Integer> storage) {
        this.storage = storage;
    }

    public HashMap<String, Integer> getStorage() {
        return storage;
    }

    boolean itemExists(String item) {
        return storage.containsKey(item);
    }

    Integer itemAmount(String item) {

        if (itemExists(item)) {
            return storage.get(item);
        }
        else {
            return 0;
        }
    }

    void addItem(String item, Integer amount) {
        if (storage.containsKey(item)) {
            storage.put(item, storage.get(item) + amount);
        } else {
            storage.put(item, amount);
        }
    }

}
