package com.example.kasapp.ui.view.Riwayat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.example.kasapp.R
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailRiwayatView(
    viewModel: KasirViewModel,
    onBackClick: () -> Unit,
    onDeleteSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) } // State untuk mengontrol loading screen
    val scope = rememberCoroutineScope()

    // Data Transaksi
    val timestamp = uiState.lastTransactionTimestamp ?: System.currentTimeMillis()
    val tanggal = formatTanggalOnly(timestamp)
    val waktu = formatWaktuOnly(timestamp)
    val totalItem = uiState.totalCartItems
    val totalHarga = uiState.totalCartPrice
    val metodeBayar = uiState.selectedPayment

    // Container Utama (Box) agar bisa menumpuk Layer Loading di atas UI
    Box(modifier = Modifier.fillMaxSize()) {

        // --- LAYER 1: UI UTAMA (Tiket & Scaffold) ---
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Detail Transaksi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    },
                    navigationIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier
                                .padding(start = 5.dp)
                                .size(45.dp)
                                .padding(4.dp)
                                .clickable(enabled = !isDeleting) { onBackClick() }, // Cegah klik saat loading
                            contentScale = ContentScale.Fit
                        )
                    },
                    actions = {
                        Box {
                            IconButton(onClick = { showMenu = true }, enabled = !isDeleting) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "Menu",
                                    tint = Color.Black
                                )
                            }

                            MaterialTheme(
                                shapes = MaterialTheme.shapes.copy(
                                    extraSmall = RoundedCornerShape(16.dp)
                                )
                            ) {
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false },
                                    offset = DpOffset(x = (-16).dp, y = 0.dp),
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surface)
                                        .width(120.dp)  // Atur lebar dropdown di sini
                                ) {
                                    DropdownMenuItem(
                                        modifier = Modifier
                                            .height(35.dp)
                                            .padding(horizontal = 12.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        text = {
                                            Text(
                                                text = "Hapus",
                                                color = Color.Black,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        },
                                        onClick = {
                                            showMenu = false
                                            showDeleteDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFFF9EF)
                    )
                )
            },
            containerColor = Color(0xFFFFF9EF)
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Background Tiket
                            TicketBackground(
                                color = Color(0xFFFFDE98),
                                notchColor = Color(0xFFFFF9EF),
                                modifier = Modifier.matchParentSize()
                            )

                            // Konten Dalam Tiket
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp, vertical = 32.dp)
                            ) {
                                // List Menu
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    uiState.cart.forEach { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${item.quantity}x",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color(0xFF4A4A4A),
                                                modifier = Modifier.width(35.dp)
                                            )
                                            Text(
                                                text = item.menu.namaMenu,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2C2C2C),
                                                modifier = Modifier.weight(1f),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(36.dp))
                                DashedDivider()
                                Spacer(modifier = Modifier.height(36.dp))

                                // Informasi Rincian (Menggunakan Helper Function biar rapi)
                                DetailRowItem(label = "Total Item", value = totalItem.toString(), isValueBold = true)
                                Spacer(modifier = Modifier.height(12.dp))
                                DetailRowItem(label = "Tanggal", value = tanggal, isValueBold = false, isValueSemiBold = true)
                                Spacer(modifier = Modifier.height(12.dp))
                                DetailRowItem(label = "Waktu", value = waktu, isValueBold = false, isValueSemiBold = true)
                                Spacer(modifier = Modifier.height(12.dp))

                                // Jenis Pembayaran
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Jenis Pembayaran",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF888888)
                                    )

                                    val warnaPembayaran = when (metodeBayar.lowercase()) {
                                        "tunai" -> Color(0xFF4CAF50)
                                        "qris" -> Color(0xFF2196F3)
                                        else -> Color(0xFF9E9E9E)
                                    }

                                    Surface(
                                        color = warnaPembayaran,
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = metodeBayar.uppercase(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                DashedDivider()
                                Spacer(modifier = Modifier.height(16.dp))

                                // Total Harga
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Jumlah",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF888888)
                                    )
                                    Text(
                                        text = formatRupiah(totalHarga),
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2C2C2C)
                                    )
                                }

                                Spacer(modifier = Modifier.height(52.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(50),
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5C542))
                ) {
                    Text(
                        text = "Selesai",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // --- LAYER 2: LOADING SCREEN (SOLID COVER) ---
        // Ini akan menutupi UI Layer 1 sepenuhnya saat isDeleting = true
        if (isDeleting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFFF9EF)) // Background SOLID (Cream) menyesuaikan tema
                    .clickable(enabled = false) {}, // Block semua sentuhan ke layer bawah
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFF5C542),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Menghapus Transaksi...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF555555)
                    )
                }
            }
        }
    }

    // --- DIALOG KONFIRMASI ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Hapus Transaksi",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Apakah Anda yakin ingin menghapus transaksi ini dari riwayat?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        isDeleting = true // 1. Nyalakan Loading Overlay (SOLID)

                        scope.launch {
                            // 2. Hapus data di database
                            viewModel.deleteCurrentTransaction()

                            // 3. Delay sedikit (800ms) agar user sadar ada proses
                            delay(800)

                            // 4. Navigasi kembali ke Riwayat
                            onDeleteSuccess()

                            // HAPUS BARIS INI:
                            // isDeleting = false  <--- JANGAN DIMATIKAN!

                            // Biarkan overlay tetap menutupi layar sampai Composable ini
                            // benar-benar hilang dari layar (destroyed) oleh navigasi.
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun DetailRowItem(label: String, value: String, isValueBold: Boolean = false, isValueSemiBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF888888)
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = if (isValueBold) FontWeight.Bold else if (isValueSemiBold) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isValueBold || isValueSemiBold) Color(0xFF2C2C2C) else Color(0xFF888888)
        )
    }
}

@Composable
fun DashedDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
    ) {
        drawLine(
            color = Color(0xFFA0A0A0),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(20f, 10f),
                0f
            ),
            strokeWidth = 3f
        )
    }
}

@Composable
fun TicketBackground(
    color: Color,
    notchColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cornerRadius = 16.dp.toPx()
        val toothRadius = 10.dp.toPx()
        val toothSpacing = 20.dp.toPx()
        val notchRadius = 12.dp.toPx()
        val width = size.width
        val height = size.height
        val centerY = height / 2

        val path = Path().apply {
            moveTo(0f, cornerRadius)
            quadraticBezierTo(0f, 0f, cornerRadius, 0f)
            lineTo(width - cornerRadius, 0f)
            quadraticBezierTo(width, 0f, width, cornerRadius)
            lineTo(width, centerY - notchRadius)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    left = width - notchRadius,
                    top = centerY - notchRadius,
                    right = width + notchRadius,
                    bottom = centerY + notchRadius
                ),
                startAngleDegrees = -90f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(width, height - toothRadius)
            var currentX = width
            val numberOfTeeth = (width / toothSpacing).toInt()
            for (i in 0 until numberOfTeeth) {
                val centerX = currentX - (toothSpacing / 2)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(
                        left = centerX - toothRadius,
                        top = height - toothRadius,
                        right = centerX + toothRadius,
                        bottom = height + toothRadius
                    ),
                    startAngleDegrees = 0f,
                    sweepAngleDegrees = -180f,
                    forceMoveTo = false
                )
                currentX -= toothSpacing
                if (currentX < toothRadius) break
            }
            lineTo(0f, height - toothRadius)
            lineTo(0f, centerY + notchRadius)
            arcTo(
                rect = androidx.compose.ui.geometry.Rect(
                    left = -notchRadius,
                    top = centerY - notchRadius,
                    right = notchRadius,
                    bottom = centerY + notchRadius
                ),
                startAngleDegrees = 90f,
                sweepAngleDegrees = 180f,
                forceMoveTo = false
            )
            lineTo(0f, cornerRadius)
            close()
        }
        drawPath(path, color)

        drawCircle(
            color = notchColor,
            radius = notchRadius,
            center = Offset(0f, centerY)
        )
        drawCircle(
            color = notchColor,
            radius = notchRadius,
            center = Offset(width, centerY)
        )
    }
}

// --- HELPER FUNCTIONS ---
fun formatTanggalOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
    return sdf.format(Date(timestamp))
}

fun formatWaktuOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm 'WIB'", Locale("in", "ID"))
    return sdf.format(Date(timestamp))
}

private fun formatRupiah(amount: Int): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0
    format.minimumFractionDigits = 0
    return format.format(amount)
}