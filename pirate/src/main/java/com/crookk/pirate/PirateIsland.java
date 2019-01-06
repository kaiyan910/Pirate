package com.crookk.pirate;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.SOURCE)
@Target(value = TYPE)
public @interface PirateIsland {

    String key();
    boolean auth() default false;
}