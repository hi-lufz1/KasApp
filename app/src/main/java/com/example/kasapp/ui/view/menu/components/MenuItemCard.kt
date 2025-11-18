package com.example.kasapp.ui.view.menu.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.R
import com.example.kasapp.data.entity.MenuMakanan // <-- Import MenuMakanan
import java.text.NumberFormat
import java.util.Locale

// Fungsi helper untuk format mata uang
fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0 // Hilangkan desimal
    return format.format(amount).replace("Rp", "Rp ") // Beri spasi
}

@Composable
fun MenuItemCard(
    menu: MenuMakanan,
    onEditClick: () -> Unit = {},
    // Terima objek MenuMakanan saat delete diklik
    onDeleteClick: (MenuMakanan) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp)
            .height(100.dp),
    ) {

        // --- LAYER 3: PALING BELAKANG (Tombol Hapus / Pink) ---
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD6D6)),
            // Kirim objek 'menu' saat Card diklik
            onClick = { onDeleteClick(menu) }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sampah), // Ganti dengan ikon Anda
                    contentDescription = "Hapus",
                    modifier = Modifier
                        .size(45.dp)
                        .padding(end = 17.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // --- LAYER 2: TENGAH (Tombol Edit / Biru) ---
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD6E3FF)),
            onClick = onEditClick // Panggil onEditClick saat Card diklik
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Image(
                    painter = painterResource(id = R.drawable.edit), // Ganti dengan ikon Anda
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(45.dp)
                        .padding(end = 17.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }

        // --- LAYER 1: PALING DEPAN (Info Menu / Putih) ---
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 120.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.spacedBy(
                    space = 6.dp, // Jarak antar teks
                    alignment = Alignment.CenterVertically
                )
            ) {
                Text(
                    text = menu.namaMenu,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = menu.jenisMenu,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatRupiah(menu.hargaMenu), // Format harga
                    fontSize = 16.sp,
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

