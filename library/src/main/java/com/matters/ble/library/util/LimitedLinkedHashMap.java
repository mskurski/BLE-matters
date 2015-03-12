package com.matters.ble.library.util;

import java.util.LinkedHashMap;

/**
 * Limited linked hash map acts as a normal map with subtle difference concerning
 * size which is limited. Thanks to overriding removeEldestEntry() method it can
 * conveniently be used as a log with limited size.
 *
 * @param <K>  generic key parameter
 * @param <V>  generic value parameter
 */
public class LimitedLinkedHashMap<K, V> extends LinkedHashMap<K, V>{
    private final int maxSize;

    /**
     * Instantiates a new Limited linked hash map.
     *
     * @param maxSize the size of
     */
    public LimitedLinkedHashMap(int maxSize){
        super(maxSize + 1, 1, false);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return size() > maxSize;
    }
}