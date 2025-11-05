package com.example.kasapp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Halaman utama (Home) aplikasi yang berisi navigasi utama.
 */
@Composable
fun HomeView(
    onNavigateToKelolaMenu: () -> Unit,
    onNavigateToKasir: () -> Unit,
    onNavigateToRiwayat: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9EF)), // Background cream
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Selamat Datang di KasApp",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3A1D00), // Teks Coklat Tua
            modifier = Modifier.padding(bottom = 40.dp)
        )

        // Tombol 1: Kasir
        HomeScreenButton(
            text = "Kasir",
            onClick = onNavigateToKasir
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol 2: Kelola Menu
        HomeScreenButton(
            text = "Kelola Menu",
            onClick = onNavigateToKelolaMenu
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tombol 3: Riwayat Pesanan
        HomeScreenButton(
            text = "Riwayat Pesanan",
            onClick = onNavigateToRiwayat
        )
    }
}

/**
 * Helper composable untuk tombol di halaman utama.
 */
@Composable
private fun HomeScreenButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.8f) // Lebar 80%
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFB300) // Oranye
        )
    ) {
        Text(text, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Bold)
    }
}