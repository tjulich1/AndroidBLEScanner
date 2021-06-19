package com.example.blescanner;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BLEDataParser {

    public static List<String> parseData(byte[] rawData) {
        List<String> result = new ArrayList<>();

        int i = 0;
        while (i < rawData.length) {
            int length = rawData[i++];

            // The data field is empty.
            if (length == 0) {
                break;
            }

            int entryType = rawData[i];

            // Invalid type
            if (entryType == 0) {
                break;
            }

            String data = Arrays.copyOfRange(rawData, i+1, i+length).toString();

            Log.d("BLE DATA", "Length: " + length);
            Log.d("BLE DATA", "Type: " + entryType);
            Log.d("BLE DATA", "Data: " + data);

            String uuid = UUID.nameUUIDFromBytes(rawData).toString();

            Log.d("BLE DATA", "Uuid: " + uuid);

            result.add(data);
            i += length;
        }

        return result;
    }

}
