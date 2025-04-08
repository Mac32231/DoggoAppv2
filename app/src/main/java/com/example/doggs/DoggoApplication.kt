package com.example.doggs;

import android.app.Application;
import com.example.doggs.data.AppContainer;
import com.example.doggs.data.DefaultAppContainer;

class DoggoApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
