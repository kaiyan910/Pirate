package com.crookk.pirate;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Pirates {

    private static TreasureMap sPirateMap;

    public static void study(@NonNull TreasureMap map) {
        sPirateMap = map;
    }

    @Nullable
    public static Treasure sail(@NonNull String key) {

        if (sPirateMap == null) return null;

        return sPirateMap.sail(key);
    }

    public static Map<String, Treasure> islands() {

        if (sPirateMap == null) return new HashMap<>();

        return sPirateMap.islands();
    }
}
