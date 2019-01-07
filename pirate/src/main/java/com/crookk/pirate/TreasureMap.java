package com.crookk.pirate;

import java.util.Map;

public interface TreasureMap {

    Treasure sail(String key);
    Map<String, Treasure> islands();
}
