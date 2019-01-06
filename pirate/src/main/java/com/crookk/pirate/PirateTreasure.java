package com.crookk.pirate;

public class PirateTreasure {

    private String key;
    private Class clazz;
    private boolean auth;

    public PirateTreasure(String key, Class clazz, boolean auth) {

        this.key = key;
        this.clazz = clazz;
        this.auth = auth;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {

        this.key = key;
    }

    public Class getClazz() {

        return clazz;
    }

    public void setClazz(Class clazz) {

        this.clazz = clazz;
    }

    public boolean isAuth() {

        return auth;
    }

    public void setAuth(boolean auth) {

        this.auth = auth;
    }
}
