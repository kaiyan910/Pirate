package com.crookk.pirate

import android.app.Application

@Pirate(value = ["com.crookk.pirate2"])
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // let teh pirates study the generated map
        Pirates.study(PirateTreasureMap())
    }
}