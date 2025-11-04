package com.example.kasapp.ui.view.Riwayat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.R
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.ui.viewmodel.Riwayat.RiwayatViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Halaman Riwayat Transaksi
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatView(
    // Inject ViewModel
    viewModel: RiwayatViewModel = viewModel(factory = ViewModelFactory.Factory),
    onBackClick: () -> Unit,
    onNotaClick: (Int) -> Unit // Aksi saat card diklik, kirim ID Transaksi
) {
    val uiState by viewModel.uiState.collectAsState()

    // State untuk mengontrol DatePickerDialog
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    // State untuk DatePicker
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.tanggalDipilih
    )

    // (Warna sesuai gambar Riwayat Transaksi.png)
    val backgroundColor = Color(0xFFFFF9EF) // Cream
    val filterBackgroundColor = Color(0xFFF0F0F0) // Abu-abu muda
    val filterSelectedColor = Color(0xFFFEBC2F)   // Oranye
    val filterSelectedTextColor = Color.White
    val filterUnselectedTextColor = Color.Gray

    Scaffold(
        topBar = {
            RiwayatTopBar(
                onBackClick = onBackClick,
                onDateClick = {
                    showDatePicker = true // Buka kalender
                },
                backgroundColor = backgroundColor // <-- Kirim warna cream ke TopBar
            )
        },
        containerColor = backgroundColor // Background cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // --- Filter (Semua, QRIS, Tunai) ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(40.dp), // Tinggi filter lebih kecil
                shape = RoundedCornerShape(40.dp), // Border radius lebih kecil
                color = filterBackgroundColor,
                tonalElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp) // Padding lebih kecil
                ) {
                    val filterOptions = listOf("Semua", "QRIS", "Tunai")
                    filterOptions.forEach { option ->
                        RiwayatFilterChip(
                            text = option,
                            selected = uiState.filterJenis == option,
                            onClick = { viewModel.onFilterJenisChange(option) },
                            selectedColor = filterSelectedColor,
                            selectedTextColor = filterSelectedTextColor,
                            unselectedTextColor = filterUnselectedTextColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // --- Daftar Riwayat ---
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.listTransaksi.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada riwayat transaksi", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.listTransaksi, key = { it.idTransaksi }) { transaksi ->
                        RiwayatItemCard(
                            transaksi = transaksi,
                            // Panggil aksi navigasi ke NotaPesananView
                            onClick = { onNotaClick(transaksi.idTransaksi) }
                        )
                    }
                }
            }
        }

        // --- Tampilkan Date Picker Dialog ---
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            // Ambil tanggal yang dipilih (pastikan tidak null)
                            val selectedTimestamp = datePickerState.selectedDateMillis
                            if (selectedTimestamp != null) {
                                viewModel.onTanggalDipilihChange(selectedTimestamp)
                            }
                        }
                    ) {
                        Text("Pilih")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Batal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}


// --- Helper Composable ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RiwayatTopBar(
    onBackClick: () -> Unit,
    onDateClick: () -> Unit,
    backgroundColor: Color // <-- Terima parameter warna
) {
    TopAppBar(
        title = {
            Text(
                text = "Riwayat Transaksi",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.back), // Ikon back Anda
                contentDescription = "Back",
                modifier = Modifier
                    .size(55.dp)
                    .padding(4.dp)
                    .clickable { onBackClick() },
                contentScale = ContentScale.Fit
            )
        },
        actions = {
            IconButton(onClick = onDateClick) {
                Icon(
                    painter = painterResource(id = R.drawable.calendar), // Ikon kalender Anda
                    contentDescription = "Pilih Tanggal",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        // --- PERBAIKAN: Gunakan warna cream ---
        colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
        windowInsets = WindowInsets(0.dp) // Hapus divider
    )
}

@Composable
private fun RiwayatFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    selectedTextColor: Color,
    unselectedTextColor: Color,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) selectedColor else Color.Transparent
    val textColor = if (selected) selectedTextColor else unselectedTextColor

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(40.dp), // Border radius lebih kecil
        color = backgroundColor,
        modifier = modifier.fillMaxHeight() // Ikuti tinggi parent
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp // Font lebih kecil
            )
        }
    }
}

@Composable
private fun RiwayatItemCard(
    transaksi: Transaksi,
    onClick: () -> Unit
) {
    // Tentukan ikon dan warna berdasarkan jenis pembayaran
    val iconRes = if (transaksi.jenisPembayaran == "QRIS") R.drawable.qris else R.drawable.tunai
    val iconBgColor = if (transaksi.jenisPembayaran == "QRIS") Color(0xFFD6E3FF) else Color(0xFFD6FFD7)
    val priceColor = if (transaksi.jenisPembayaran == "QRIS") Color(0xFF007FFF) else Color(0xFF32C05B)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp), // Tinggi card lebih kecil
        shape = RoundedCornerShape(40.dp), // Border radius lebih kecil
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp), // Padding lebih kecil
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ikon
            Box(
                modifier = Modifier
                    .size(50.dp) // Ikon lebih kecil
                    .clip(RoundedCornerShape(32.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = transaksi.jenisPembayaran,
                    modifier = Modifier.size(30.dp) // Ukuran gambar ikon lebih kecil
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info (Jenis dan Tanggal)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp) // Jarak antar teks
            ) {
                Text(
                    text = transaksi.jenisPembayaran,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C3F68) // Warna baru
                )
                Text(
                    text = formatTanggalSingkat(transaksi.tglTransaksi),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W600,
                    color = Color(0xFF1C3F68) // Warna baru
                )
            }

            // Harga
            Text(
                text = formatRupiah(transaksi.jlhTransaksi),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = priceColor
            )
        }
    }
}


// --- Helper Functions (Wajib ada) ---

private fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
}

// Format tanggal pendek (12-02-2025)
private fun formatTanggalSingkat(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale("in", "ID"))
        val netDate = Date(timestamp)
        sdf.format(netDate)
    } catch (e: Exception) {
        "Invalid Date"
    }
}