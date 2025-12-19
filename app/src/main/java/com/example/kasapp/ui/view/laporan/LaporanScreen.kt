package com.example.kasapp.ui.view.laporan

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.R
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.ui.util.PdfLaporanGenerator
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import com.example.kasapp.ui.viewmodel.laporan.LaporanViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class JenisLaporan {
    HARIAN, BULANAN, SEMUA_TRANSAKSI
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LaporanScreen(
    onBackClick: () -> Unit
) {
    val viewModel: LaporanViewModel =
        viewModel(factory = ViewModelFactory.Factory)

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showJenis by remember { mutableStateOf<JenisLaporan?>(null) }
    var infoTanggal by remember { mutableStateOf("") }
    var showHarianPicker by remember { mutableStateOf(false) }
    var showBulananPicker by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan",
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(45.dp)
                            .padding(4.dp)
                            .clickable { onBackClick() },
                        contentScale = ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7EEDB))
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ===== HEADER CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomStart = 30.dp,
                    bottomEnd = 30.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Cetak laporan transaksi kas berdasarkan periode yang Anda pilih.",
                        color = Color(0xFF5C4A27),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (infoTanggal.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = infoTanggal,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // ===== MENU =====
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(40.dp))

                LaporanMenuButton(
                    icon = R.drawable.calendar,
                    text = "Laporan Harian"
                ) {
                    showJenis = JenisLaporan.HARIAN
                    showHarianPicker = true
                }

                Spacer(Modifier.height(20.dp))

                LaporanMenuButton(
                    icon = R.drawable.cloudupload,
                    text = "Laporan Bulanan"
                ) {
                    showJenis = JenisLaporan.BULANAN
                    showBulananPicker = true
                }

                Spacer(Modifier.height(20.dp))

                LaporanMenuButton(
                    icon = R.drawable.receipt,
                    text = "Semua Transaksi"
                ) {
                    showJenis = JenisLaporan.SEMUA_TRANSAKSI
                    infoTanggal = "Semua transaksi"
                   viewModel.loadAll()
                }

                if (showJenis != null) {
                    Spacer(Modifier.height(30.dp))

                    if (uiState.transaksiList.isNotEmpty()) {
                        Button(
                            onClick = { showPreview = true },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFC107)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Preview Laporan", color = Color.Black)
                        }

                    } else {
                        Text(
                            text = "Tidak ada transaksi pada periode yang dipilih",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    // ===== DATE PICKER =====
    if (showHarianPicker) {
        HarianDatePicker(
            onDismiss = { showHarianPicker = false },
            onConfirm = { date ->
                viewModel.loadLaporan(
                    date.atStartOfDay(),
                    date.atEndOfDay()
                )
                infoTanggal = "Harian: ${formatTanggal(date)}"
                showHarianPicker = false
            }
        )
    }

    if (showBulananPicker) {
        MonthYearPicker(
            onDismiss = { showBulananPicker = false },
            onConfirm = { month, year ->
                val (start, end) = getStartEndOfMonth(month, year)
                viewModel.loadLaporan(start, end)
                infoTanggal = "Bulanan: ${namaBulan[month]} $year"
                showBulananPicker = false
            }
        )
    }

    // ===== PREVIEW =====
    if (showPreview) {
        PreviewLaporanDialog(
            jenis = showJenis!!,
            infoTanggal = infoTanggal,
            data = uiState.transaksiList,
            totalPendapatan = uiState.totalPendapatan,
            onGeneratePdf = {
                showPreview = false
                PdfLaporanGenerator.generatePdfAndOpen(
                    context,
                    showJenis!!,
                    uiState.transaksiList,
                    uiState.totalPendapatan,
                    uiState.startTime,
                    uiState.endTime
                )
            },
            onDismiss = { showPreview = false }
        )
    }
}

/* ================= MENU BUTTON ================= */

@Composable
fun LaporanMenuButton(
    icon: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 15.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(15.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
        )
    }
}

/* ================= DATE PICKER ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HarianDatePicker(
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
    val state = rememberDatePickerState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text("Pilih Tanggal", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            DatePicker(state = state, showModeToggle = false)
            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Batal") }
                Spacer(Modifier.width(8.dp))
                Button(
                    enabled = state.selectedDateMillis != null,
                    onClick = { state.selectedDateMillis?.let(onConfirm) }
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthYearPicker(
    onDismiss: () -> Unit,
    onConfirm: (month: Int, year: Int) -> Unit
) {
    val cal = Calendar.getInstance()
    var month by remember { mutableStateOf(cal.get(Calendar.MONTH)) }
    var year by remember { mutableStateOf(cal.get(Calendar.YEAR)) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(Modifier.padding(16.dp)) {
            Text("Pilih Bulan & Tahun", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            DropdownSelector("Bulan", namaBulan[month], namaBulan) {
                month = it
            }

            Spacer(Modifier.height(12.dp))

            DropdownSelector(
                "Tahun",
                year.toString(),
                (year - 5..year + 5).map { it.toString() }
            ) {
                year = it.toInt()
            }

            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text("Batal") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onConfirm(month, year) }) {
                    Text("OK")
                }
            }
        }
    }
}

/* ================= DROPDOWN ================= */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    value: String,
    items: List<String>,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )

        ExposedDropdownMenu(expanded, { expanded = false }) {
            items.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    }
                )
            }
        }
    }
}

/* ================= PREVIEW ================= */

@Composable
fun PreviewLaporanDialog(
    jenis: JenisLaporan,
    infoTanggal: String,
    data: List<Transaksi>,
    totalPendapatan: Double,
    onGeneratePdf: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Preview Laporan") },
        confirmButton = {
            Button(
                onClick = onGeneratePdf,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFC107)
                )
            ) {
                Text("Generate PDF")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal",color = Color(0xFFFFC107))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                Text("LAPORAN ${jenis.name}")
                Text("KasApp")
                Spacer(Modifier.height(8.dp))
                Text(infoTanggal)
                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(8.dp))
                Text("Jumlah Transaksi : ${data.size}")
                Text("Total Pendapatan : Rp $totalPendapatan")
            }
        }
    )
}

/* ================= UTIL ================= */

val namaBulan = listOf(
    "Januari","Februari","Maret","April","Mei","Juni",
    "Juli","Agustus","September","Oktober","November","Desember"
)

fun formatTanggal(ts: Long): String =
    SimpleDateFormat("dd MMM yyyy", Locale("id")).format(Date(ts))

fun getStartEndOfMonth(month: Int, year: Int): Pair<Long, Long> {
    val cal = Calendar.getInstance().apply {
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        clear(Calendar.MINUTE)
        clear(Calendar.SECOND)
        clear(Calendar.MILLISECOND)
    }
    val start = cal.timeInMillis
    cal.add(Calendar.MONTH, 1)
    cal.add(Calendar.MILLISECOND, -1)
    return start to cal.timeInMillis
}

fun Long.atStartOfDay(): Long = Calendar.getInstance().apply {
    timeInMillis = this@atStartOfDay
    set(Calendar.HOUR_OF_DAY, 0)
    clear(Calendar.MINUTE)
    clear(Calendar.SECOND)
    clear(Calendar.MILLISECOND)
}.timeInMillis

fun Long.atEndOfDay(): Long = Calendar.getInstance().apply {
    timeInMillis = this@atEndOfDay
    set(Calendar.HOUR_OF_DAY, 23)
    set(Calendar.MINUTE, 59)
    set(Calendar.SECOND, 59)
    set(Calendar.MILLISECOND, 999)
}.timeInMillis
