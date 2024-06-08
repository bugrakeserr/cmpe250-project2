import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


// Entry class represents a key-value pair
class Entry<K, V> {
    K key;
    V value;


    // Constructor to initialize key and value
    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    // Getter method for value
    public V getValue() {
        return value;
    }
}

// MyHashMap class represents a simple hash map implementation
public class MyHashMap<K, V> {
    private static final int DEFAULT_CAPACITY = 1001;

    private static final double LOAD_FACTOR_THRESHOLD = 0.75;
    private LinkedList<Entry<K, V>>[] buckets;

    private int size;


    // Constructor to initialize the hash map with default capacity
    public MyHashMap() {
        buckets = new LinkedList[DEFAULT_CAPACITY];
        for (int i = 0; i < DEFAULT_CAPACITY; i++) {
            buckets[i] = new LinkedList<>();
        }
    }


    // Method to get the bucket index for a given key
    private int getBucketIndex(K key) {
        int hashCode = key.hashCode();
        return Math.abs(hashCode % buckets.length);
    }


    // Method to check if a key is present in the hash map
    public boolean containsKey(K key) {
        int index = getBucketIndex(key);
        LinkedList<Entry<K, V>> bucket = buckets[index];
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    // Method to insert or update a key-value pair in the hash map
    public void put(K key, V value) {
        int index = getBucketIndex(key);
        LinkedList<Entry<K, V>> bucket = buckets[index];
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        bucket.add(new Entry<>(key, value));
        size ++;
        double loadFactor = (double) size() / buckets.length;
        if (loadFactor > LOAD_FACTOR_THRESHOLD) {
            rehash();
        }
    }

    // Method to get the value associated with a given key
    public V get(K key) {
        int index = getBucketIndex(key);
        LinkedList<Entry<K, V>> bucket = buckets[index];
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null; // Key not found
    }


    // Method to remove a key-value pair from the hash map
    public void remove(K key) {
        int index = getBucketIndex(key);
        LinkedList<Entry<K, V>> bucket = buckets[index];
        Entry<K, V> entryToRemove = null;

        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                entryToRemove = entry;
                break;
            }
        }

        if (entryToRemove != null) {
            bucket.remove(entryToRemove);
            size--;
        }
    }

    // CustomEntrySet class represents a set of entries in the hash map
    public static class CustomEntrySet<K, V> implements Iterable<Entry<K, V>> {
        private List<Entry<K, V>> entries;


        // Constructor to initialize the entry set
        public CustomEntrySet() {
            entries = new ArrayList<>();
        }

        // Method to add an entry to the set
        public void add(Entry<K, V> entry) {
            entries.add(entry);
        }

        // Iterator method to iterate over the entries in the set
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return entries.iterator();
        }
    }

    // Method to get the entry set of the hash map
    public CustomEntrySet<K, V> entrySet() {
        CustomEntrySet<K, V> entrySet = new CustomEntrySet<>();
        for (LinkedList<Entry<K, V>> bucket : buckets) {
            for (Entry<K, V> entry : bucket) {
                entrySet.add(new Entry<>(entry.key, entry.value));
            }
        }
        return entrySet;
    }

    // Method to get the size of the hash map
    public int size() {
        return size;
    }


    // Method to rehash the hash map when the load factor exceeds the threshold
    private void rehash() {
        int newCapacity = buckets.length * 2;
        LinkedList<Entry<K, V>>[] newBuckets = new LinkedList[newCapacity];

        // Initialize the new buckets
        for (int i = 0; i < newCapacity; i++) {
            newBuckets[i] = new LinkedList<>();
        }

        // Rehash all entries into the new buckets
        for (LinkedList<Entry<K, V>> bucket : buckets) {
            for (Entry<K, V> entry : bucket) {
                int newIndex = Math.abs(entry.key.hashCode() % newCapacity);
                newBuckets[newIndex].add(entry);
            }
        }

        // Update the buckets and recompute hash index
        this.buckets = newBuckets;
    }
}