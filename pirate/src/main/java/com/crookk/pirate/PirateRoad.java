package com.crookk.pirate;

import java.util.Map;

public interface PirateRoad {

    PirateTreasure sail(String key);
    Map<String, PirateTreasure> islands();
}
