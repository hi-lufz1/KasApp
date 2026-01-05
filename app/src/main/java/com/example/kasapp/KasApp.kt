package com.example.kasapp

import android.app.Application


import com.example.kasapp.dependenciesinjection.ContainerApp


class KasApp : Application() {

    lateinit var containerApp: ContainerApp

    override fun onCreate() {
        super.onCreate()
        containerApp = ContainerApp(this)
    }

    fun refreshContainer() {
        containerApp = ContainerApp(this)
    }
}
