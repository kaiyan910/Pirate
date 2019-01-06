package com.crookk.pirate

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

@PirateIsland(key = "/main")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
