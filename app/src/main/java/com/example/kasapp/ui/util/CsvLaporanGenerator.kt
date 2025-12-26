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

            /* ================= HEADER ================= */

            writer.appendLine("LAPORAN ${jenis.name}")
            writer.appendLine("KasApp")

            startTime?.let {
                writer.appendLine("Periode;${format(it)} - ${format(endTime!!)}")
            }

            writer.appendLine()
            writer.appendLine("Jumlah Transaksi;${transaksi.size}")
            writer.appendLine("Total Pendapatan;Rp ${formatRupiah(totalPendapatan)}")
            writer.appendLine()

            /* ================= REKAP JENIS PEMBAYARAN ================= */

            writer.appendLine("RINCIAN PEMBAYARAN")
            transaksi.groupBy { it.jenisPembayaran }
                .forEach { (jenisBayar, list) ->
                    val total = list.sumOf { it.jlhTransaksi }
                    writer.appendLine("$jenisBayar;Rp ${formatRupiah(total)}")
                }

            writer.appendLine()
            writer.appendLine("================================")

            /* ================= ISI LAPORAN ================= */

            when (jenis) {

                JenisLaporan.HARIAN -> {
                    writer.appendLine()
                    writer.appendLine("No;ID Transaksi;Tanggal;Pembayaran;Total (Rp)")

                    transaksi.forEachIndexed { index, trx ->
                        writer.appendLine(
                            "${index + 1};TRX-${trx.idTransaksi};" +
                                    "${format(trx.tglTransaksi)};" +
                                    "${trx.jenisPembayaran};" +
                                    formatRupiah(trx.jlhTransaksi)
                        )
                    }
                }

                /* ========== BULANAN ========== */
                JenisLaporan.BULANAN -> {
                    val perHari = transaksi.groupBy { dayKey(it.tglTransaksi) }

                    perHari.forEach { (_, listHarian) ->
                        val tanggal = listHarian.first().tglTransaksi

                        writer.appendLine()
                        writer.appendLine(formatHari(tanggal))
                        writer.appendLine("No;ID Transaksi;Pembayaran;Total")

                        var totalHarian = 0

                        listHarian.forEachIndexed { index, trx ->
                            writer.appendLine(
                                "${index + 1};TRX-${trx.idTransaksi};${trx.jenisPembayaran};${formatRupiah(trx.jlhTransaksi)}"
                            )
                            totalHarian += trx.jlhTransaksi
                        }

                        writer.appendLine(";;TOTAL HARIAN;${formatRupiah(totalHarian)}")
                    }
                }

                /* ========== TAHUNAN ========== */
                JenisLaporan.TAHUNAN -> {
                    val perBulan = transaksi.groupBy { monthKey(it.tglTransaksi) }

                    perBulan.forEach { (_, listBulanan) ->
                        val bulan = listBulanan.first().tglTransaksi

                        writer.appendLine()
                        writer.appendLine(formatBulan(bulan).uppercase())

                        var totalBulanan = 0
                        val perHari = listBulanan.groupBy { dayKey(it.tglTransaksi) }

                        perHari.forEach { (_, listHarian) ->
                            val tanggal = listHarian.first().tglTransaksi

                            writer.appendLine(formatHari(tanggal))
                            writer.appendLine("No;ID Transaksi;Pembayaran;Total")

                            var totalHarian = 0

                            listHarian.forEachIndexed { index, trx ->
                                writer.appendLine(
                                    "${index + 1};TRX-${trx.idTransaksi};${trx.jenisPembayaran};${formatRupiah(trx.jlhTransaksi)}"
                                )
                                totalHarian += trx.jlhTransaksi
                            }

                            writer.appendLine(";;TOTAL HARIAN;${formatRupiah(totalHarian)}")
                            writer.appendLine()

                            totalBulanan += totalHarian
                        }

                        writer.appendLine(
                            ";;TOTAL BULAN ${formatBulan(bulan).uppercase()};${formatRupiah(totalBulanan)}"
                        )
                    }

                    writer.appendLine()
                    writer.appendLine("TOTAL TAHUN;${formatRupiah(totalPendapatan)}")
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

    private fun formatHari(time: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale("id")).format(Date(time))

    private fun formatBulan(time: Long): String =
        SimpleDateFormat("MMMM yyyy", Locale("id")).format(Date(time))

    private fun dayKey(time: Long): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(time))

    private fun monthKey(time: Long): String =
        SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date(time))

    private fun formatRupiah(value: Int): String =
        "%,d".format(value)

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
