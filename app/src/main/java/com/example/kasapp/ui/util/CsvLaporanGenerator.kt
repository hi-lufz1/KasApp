package com.example.kasapp.ui.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.provider.MediaStore
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.ui.view.laporan.JenisLaporan
import java.text.SimpleDateFormat
import java.util.*

object CsvLaporanGenerator {

    fun generateCsvAndOpen(
        context: Context,
        jenis: JenisLaporan,
        transaksi: List<Transaksi>,
        totalPendapatan: Int,
        startTime: Long?,
        endTime: Long?
    ) {
        val resolver = context.contentResolver
        val fileName = buildFileName(jenis, startTime, endTime)

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + "/Laporan"
            )
        }

        val uri = resolver.insert(
            MediaStore.Files.getContentUri("external"),
            values
        ) ?: return

        resolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->

            // Tambahkan BOM untuk UTF-8 agar Excel mengenali encoding dengan benar
            writer.write("\uFEFF")

            /* ================= HEADER METADATA ================= */

            writer.appendLine("LAPORAN ${jenis.name}")
            writer.appendLine("KasApp")
            writer.appendLine()

            startTime?.let {
                writer.appendLine("Periode:;${format(it)} - ${format(endTime!!)}")
            }

            writer.appendLine("Jumlah Transaksi:;${transaksi.size}")
            writer.appendLine("Total Pendapatan:;${formatRupiah(totalPendapatan)}")
            writer.appendLine()

            /* ================= REKAP JENIS PEMBAYARAN ================= */

            writer.appendLine("RINCIAN PEMBAYARAN")
            writer.appendLine("Jenis Pembayaran;Jumlah (Rp)")

            transaksi.groupBy { it.jenisPembayaran }
                .forEach { (jenisBayar, list) ->
                    val total = list.sumOf { it.jlhTransaksi }
                    writer.appendLine("$jenisBayar;${formatRupiah(total)}")
                }

            writer.appendLine()
            writer.appendLine()

            /* ================= DATA TRANSAKSI (FLAT TABLE) ================= */

            when (jenis) {

                JenisLaporan.HARIAN -> {
                    writer.appendLine("DETAIL TRANSAKSI")
                    writer.appendLine("No;ID Transaksi;Tanggal;Jenis Pembayaran;Total (Rp)")

                    transaksi.sortedBy { it.tglTransaksi }.forEachIndexed { index, trx ->
                        writer.appendLine(
                            "${index + 1};TRX-${trx.idTransaksi};" +
                                    "${format(trx.tglTransaksi)};" +
                                    "${trx.jenisPembayaran};" +
                                    formatRupiah(trx.jlhTransaksi)
                        )
                    }
                }

                JenisLaporan.BULANAN -> {
                    writer.appendLine("DETAIL TRANSAKSI")
                    writer.appendLine("No;ID Transaksi;Tanggal;Jenis Pembayaran;Total (Rp)")

                    transaksi.sortedBy { it.tglTransaksi }.forEachIndexed { index, trx ->
                        writer.appendLine(
                            "${index + 1};TRX-${trx.idTransaksi};" +
                                    "${format(trx.tglTransaksi)};" +
                                    "${trx.jenisPembayaran};" +
                                    formatRupiah(trx.jlhTransaksi)
                        )
                    }
                }

                JenisLaporan.TAHUNAN -> {
                    writer.appendLine("DETAIL TRANSAKSI")
                    writer.appendLine("No;ID Transaksi;Tanggal;Bulan;Jenis Pembayaran;Total (Rp)")

                    transaksi.sortedBy { it.tglTransaksi }.forEachIndexed { index, trx ->
                        writer.appendLine(
                            "${index + 1};TRX-${trx.idTransaksi};" +
                                    "${format(trx.tglTransaksi)};" +
                                    "${formatBulan(trx.tglTransaksi)};" +
                                    "${trx.jenisPembayaran};" +
                                    formatRupiah(trx.jlhTransaksi)
                        )
                    }
                }

                else -> {}
            }
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Buka laporan CSV"))
    }

    /* ================= UTIL ================= */

    private fun format(time: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale("id")).format(Date(time))

    private fun formatBulan(time: Long): String =
        SimpleDateFormat("MMMM yyyy", Locale("id")).format(Date(time))

    private fun formatRupiah(value: Int): String =
        "%,d".format(Locale("id"), value)

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