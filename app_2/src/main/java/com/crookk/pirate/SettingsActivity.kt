package com.crookk.pirate

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

@PirateIsland(key = "/settings")
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}
