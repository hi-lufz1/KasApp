package com.example.kasapp.ui.view.laporan

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.ui.util.PdfLaporanGenerator
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import com.example.kasapp.ui.viewmodel.laporan.LaporanViewModel
import java.text.SimpleDateFormat
import java.util.*

enum class JenisLaporan {
    HARIAN, BULANAN, PER_TRANSAKSI
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaporanScreen() {

    val viewModel: LaporanViewModel =
        viewModel(factory = ViewModelFactory.Factory)

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showJenisSheet by remember { mutableStateOf(false) }
    var showHarianPicker by remember { mutableStateOf(false) }
    var showBulananPicker by remember { mutableStateOf(false) }

    var selectedJenis by remember { mutableStateOf<JenisLaporan?>(null) }
    var infoTanggal by remember { mutableStateOf("") }

    var showPreview by remember { mutableStateOf(false) }


    Scaffold(
        topBar = { TopAppBar(title = { Text("Laporan") }) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showJenisSheet = true }
            ) {
                Text("Cetak Laporan")
            }

            if (infoTanggal.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(infoTanggal)
            }

            if (uiState.transaksiList.isNotEmpty() && selectedJenis != null) {
                Spacer(Modifier.height(20.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showPreview = true }
                ) {
                    Text("Preview Laporan")
                }
            }
        }
    }

    /* ================= PILIH JENIS ================= */
    if (showJenisSheet) {
        ModalBottomSheet(onDismissRequest = { showJenisSheet = false }) {
            Column(Modifier.padding(16.dp)) {

                Text("Pilih Jenis Laporan", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))

                LaporanItem("📅 Harian") {
                    selectedJenis = JenisLaporan.HARIAN
                    showHarianPicker = true
                    showJenisSheet = false
                }

                LaporanItem("🗓 Bulanan") {
                    selectedJenis = JenisLaporan.BULANAN
                    showBulananPicker = true
                    showJenisSheet = false
                }

                LaporanItem("🧾 Per Transaksi") {
                    selectedJenis = JenisLaporan.PER_TRANSAKSI
                    infoTanggal = "Semua transaksi"
                    showJenisSheet = false
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    /* ================= HARIAN ================= */
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

    /* ================= BULANAN ================= */
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

    if (showPreview) {
        PreviewLaporanDialog(
            jenis = selectedJenis!!,
            infoTanggal = infoTanggal,
            data = uiState.transaksiList,
            totalPendapatan = uiState.totalPendapatan,
            onGeneratePdf = {
                showPreview = false
                PdfLaporanGenerator.generatePdfAndOpen(
                    context,
                    selectedJenis!!,
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

/* ================= KOMPONEN ================= */

@Composable
fun LaporanItem(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, Modifier.padding(16.dp))
    }
}

/* ================= HARIAN PICKER ================= */

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
                    onClick = {
                        state.selectedDateMillis?.let(onConfirm)
                    }
                ) { Text("OK") }
            }
        }
    }
}

/* ================= BULANAN PICKER ================= */

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
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
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
            Button(onClick = onGeneratePdf) {
                Text("Generate PDF")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {

                // ===== HEADER =====
                Text(
                    text = "LAPORAN ${jenis.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text("KasApp", style = MaterialTheme.typography.labelMedium)

                Spacer(Modifier.height(6.dp))
                Text(infoTanggal)

                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                // ===== SUMMARY =====
                Text("Jumlah Transaksi : ${data.size}")
                Text("Total Pendapatan : Rp $totalPendapatan")

                Spacer(Modifier.height(12.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                // ===== LIST TRANSAKSI =====
                data.forEachIndexed { index, trx ->
                    Text(
                        text = "${index + 1}. TRX-${trx.idTransaksi} | " +
                                "${formatTanggal(trx.tglTransaksi)} | " +
                                "Rp ${trx.jlhTransaksi}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    )
}

