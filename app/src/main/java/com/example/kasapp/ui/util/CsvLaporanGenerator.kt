package com.example.kasapp.ui.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.ui.view.laporan.JenisLaporan
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object CsvLaporanGenerator {

    fun generateCsvAndOpen(
        context: Context,
        jenis: JenisLaporan,
        transaksi: List<Transaksi>,
        totalPendapatan: Double,
        startTime: Long?,
        endTime: Long?
    ) {
        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "laporan"
        )
        if (!dir.exists()) dir.mkdirs()

        val fileName = buildFileName(jenis, startTime, endTime)
        val file = File(dir, fileName)

        file.bufferedWriter().use { writer ->
            writer.appendLine("LAPORAN ${jenis.name}")
            writer.appendLine("KasApp")

            startTime?.let {
                writer.appendLine(
                    "Periode,${format(it)} - ${format(endTime!!)}"
                )
            }

            writer.appendLine()
            writer.appendLine("Jumlah Transaksi,${transaksi.size}")
            writer.appendLine("Total Pendapatan,Rp ${formatRupiah(totalPendapatan)}")
            writer.appendLine()

            writer.appendLine("No,ID Transaksi,Tanggal,Total (Rp)")

            transaksi.forEachIndexed { index, trx ->
                writer.appendLine(
                    "${index + 1}," +
                            "TRX-${trx.idTransaksi}," +
                            format(trx.tglTransaksi) + "," +
                            formatRupiah(trx.jlhTransaksi)
                )
            }
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)
    }

    // ===== UTIL =====

    private fun format(time: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale("id")).format(Date(time))

    private fun formatRupiah(value: Double): String =
        "%,.0f".format(value)

    private fun buildFileName(
        jenis: JenisLaporan,
        start: Long?,
        end: Long?
    ): String {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.US)
        return when {
            start != null && end != null ->
                "Laporan_${jenis.name}_${sdf.format(Date(start))}_${sdf.format(Date(end))}.csv"
            else ->
                "Laporan_${jenis.name}_${System.currentTimeMillis()}.csv"
        }
    }
}
