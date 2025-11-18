package com.example.kasapp

import android.app.Application
import com.example.kasapp.dependenciesinjection.ContainerApp

class KasApp : Application() {
    // Menyimpan instance ContainerApp agar bisa diakses di seluruh aplikasi
    lateinit var containerApp: ContainerApp

    override fun onCreate() {
        super.onCreate()
        // Membuat instance ContainerApp saat aplikasi pertama kali dijalankan
        containerApp = ContainerApp(this)
    }
}
