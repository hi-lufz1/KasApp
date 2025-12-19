package com.example.kasapp.ui.view.kasir

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.R
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotaPesananView(
    viewModel: KasirViewModel,
    onSelesaiClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val timestamp = uiState.lastTransactionTimestamp ?: System.currentTimeMillis()
    val tanggal = formatTanggalOnly(timestamp)
    val waktu = formatWaktuOnly(timestamp)
    val totalItem = uiState.totalCartItems
    val totalHarga = uiState.totalCartPrice
    val metodeBayar = uiState.selectedPayment

    Scaffold(
        containerColor = Color(0xFFFFB300)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.success),
                contentDescription = "Transaksi Berhasil !",
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Transaksi Berhasil !",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBC1))
            ) {
                if (uiState.isHistoryLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Receipt Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    TicketBackground(
                                        color = Color(0xFFFFFFFF),
                                        notchColor = Color(0xFFFFEBC1),
                                        modifier = Modifier.matchParentSize()
                                    )

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 32.dp, vertical = 32.dp)
                                    ) {
                                        // Daftar Item
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
                                                        textAlign = TextAlign.End
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(36.dp))
                                        DashedDivider()
                                        Spacer(modifier = Modifier.height(36.dp))

                                        // Total Item
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = totalItem.toString(),
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF888888)
                                            )
                                            Text(
                                                text = "Total Item",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2C2C2C)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Tanggal
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Tanggal",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color(0xFF888888)
                                            )
                                            Text(
                                                text = tanggal,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF2C2C2C)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Waktu
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Waktu",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color(0xFF888888)
                                            )
                                            Text(
                                                text = waktu,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color(0xFF2C2C2C)
                                            )
                                        }

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

                                        // Jumlah Total
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

                        // Tombol Selesai
                        Button(
                            onClick = onSelesaiClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFB300)
                            )
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
            }
        }
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
                rect = Rect(
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
                    rect = Rect(
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
                rect = Rect(
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

fun formatTanggalOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
    return sdf.format(Date(timestamp))
}

fun formatWaktuOnly(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm 'WIB'", Locale("in", "ID"))
    return sdf.format(Date(timestamp))
}

