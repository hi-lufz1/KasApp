package com.example.kasapp.ui.view.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.kasapp.R
import kotlinx.coroutines.delay
// --- TAMBAHAN IMPORT DISPATCHERS ---
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// ----------------------------------

@Composable
fun SuccessView(
    onNavigateBack: () -> Unit // Fungsi untuk kembali ke halaman sebelumnya
) {
    val backgroundColor = Color(0xFFFEBC2F) // Warna cream

    // Efek agar kembali otomatis setelah beberapa detik
    LaunchedEffect(Unit) {
        delay(1000) // Tunggu 2 detik
        // --- PERBAIKAN: Pindah ke Main Thread ---
        withContext(Dispatchers.Main) {
            onNavigateBack() // Panggil fungsi navigasi kembali
        }
        // --------------------------------------
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.successs), // Panggil gambar Anda
                contentDescription = "Sukses",
                modifier = Modifier
                    .fillMaxWidth(0.5f) // Atur lebar gambar (misal 80% lebar layar)
                    .aspectRatio(1f), // Jaga rasio aspek gambar (jika perlu)
                contentScale = ContentScale.Fit // Sesuaikan skala gambar
            )
            Text(text = "Sukses!", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
        }
    }
}

