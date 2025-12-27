package com.example.kasapp.ui.view.kasir

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SuccessViewKasir(
    onNavigateBack: () -> Unit
) {
    val backgroundColor = Color(0xFFFFB300) // Warna kuning sesuai design

    // State untuk mengontrol animasi fade-in
    var isVisible by remember { mutableStateOf(false) }

    // Animasi alpha untuk fade-in effect
    val alphaAnimation by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "successAlpha"
    )

    // Efek agar kembali otomatis setelah beberapa detik
    LaunchedEffect(Unit) {
        // Tampilkan content dengan animasi
        isVisible = true
        delay(2500) // Tunggu 2.5 detik (600ms animasi + 1900ms ditampilkan)

        withContext(Dispatchers.Main) {
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnimation)
                .fillMaxWidth()
        ) {
            // Logo Success
            Image(
                painter = painterResource(id = R.drawable.successs),
                contentDescription = "Sukses",
                modifier = Modifier
                    .size(130.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Text "Transaksi Berhasil !"
            Text(
                text = "Transaksi Berhasil !",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}