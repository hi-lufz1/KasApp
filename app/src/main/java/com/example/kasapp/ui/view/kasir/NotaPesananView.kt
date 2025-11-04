package com.example.kasapp.ui.view.kasir

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.R
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Halaman Nota Pesanan (Struk)
 * Muncul setelah SuccessView
 */
@Composable
fun NotaPesananView(
    viewModel: KasirViewModel, // Terima ViewModel yang sama
    onSelesaiClick: () -> Unit  // Aksi untuk kembali ke Home
) {
    val uiState by viewModel.uiState.collectAsState()

    // Ambil tanggal dari ViewModel (akan null jika dari riwayat, tapi ter-update)
    val tanggalTransaksi = formatTanggal(uiState.lastTransactionTimestamp ?: System.currentTimeMillis())

    Scaffold(
        containerColor = Color(0xFFFFB300) // Latar oranye
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. Ikon Sukses (Ganti dengan R.drawable.nama_ikon_sukses_anda)
            Image(
                painter = painterResource(id = R.drawable.success), // Ganti dengan ikon Anda
                contentDescription = "Transaksi Berhasil",
                modifier = Modifier.size(130.dp) // Sesuaikan ukuran
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Teks "Transaksi Berhasil"
            Text(
                text = "Transaksi Berhasil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Card Putih (Bagian Nota)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ambil sisa ruang
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9EF)) // Cream
            ) {
                // --- PERBAIKAN: Tambahkan cek loading ---
                if (uiState.isHistoryLoading) {
                    // Tampilkan loading jika sedang mengambil data riwayat
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Tampilkan nota jika data siap
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Rincian Pesanan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color.LightGray)

                        // Daftar Item
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp)
                        ) {
                            items(uiState.cart) { item ->
                                NotaItemRow(
                                    namaMenu = item.menu.namaMenu,
                                    jumlah = item.quantity,
                                    totalHarga = item.subtotal
                                )
                            }
                        }

                        Divider(color = Color.LightGray)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Detail Pembayaran
                        NotaInfoRow(label = "Tanggal", value = tanggalTransaksi)
                        Spacer(modifier = Modifier.height(8.dp))
                        NotaInfoRow(label = "Metode Pembayaran", value = uiState.selectedPayment)
                        Spacer(modifier = Modifier.height(8.dp))
                        NotaInfoRow(
                            label = "Total Pembayaran",
                            value = formatRupiah(uiState.totalCartPrice),
                            isTotal = true
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Tombol Selesai
                        Button(
                            onClick = onSelesaiClick, // Panggil aksi (clear cart & navigasi)
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300) // Oranye
                            )
                        ) {
                            Text(
                                text = "Selesai",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
                // --- AKHIR PERBAIKAN ---
            }
        }
    }
}


// --- Helper Composable untuk Nota ---

@Composable
private fun NotaItemRow(namaMenu: String, jumlah: Int, totalHarga: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$jumlah x",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.width(40.dp)
        )
        Text(
            text = namaMenu,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = formatRupiah(totalHarga),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
private fun NotaInfoRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium,
            color = Color.Black
        )
    }
}

private fun formatTanggal(timestamp: Long): String {
    return try {
        // Format: Selasa, 4 November 2025
        val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("in", "ID"))
        val netDate = Date(timestamp)
        sdf.format(netDate)
    } catch (e: Exception) {
        "Invalid Date"
    }
}