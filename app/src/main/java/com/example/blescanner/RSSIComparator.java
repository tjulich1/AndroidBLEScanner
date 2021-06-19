package com.example.blescanner;

import java.util.Comparator;

public class RSSIComparator implements Comparator<BLEItem> {
    public int compare(BLEItem first, BLEItem second) {
        return Integer.compare(second.getRssi(), first.getRssi());
    }
}
