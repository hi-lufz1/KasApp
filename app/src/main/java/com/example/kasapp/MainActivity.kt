package com.example.kasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.kasapp.ui.navigation.PengelolaHalaman
import com.example.kasapp.ui.view.menu.HomeMenuView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PengelolaHalaman()
        }
    }
}
