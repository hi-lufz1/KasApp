package com.example.kasapp.ui.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.provider.MediaStore
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.ui.view.laporan.JenisLaporan
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object PdfLaporanGenerator {

    fun generatePdfAndOpen(
        context: Context,
        jenis: JenisLaporan,
        transaksi: List<Transaksi>,
        totalPendapatan: Double,
        startTime: Long?,
        endTime: Long?
    ) {
        val uri = generatePdf(context, jenis, transaksi, totalPendapatan, startTime, endTime)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    private fun generatePdf(
        context: Context,
        jenis: JenisLaporan,
        transaksi: List<Transaksi>,
        totalPendapatan: Double,
        startTime: Long?,
        endTime: Long?
    ): android.net.Uri {

        val pdf = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        var pageNumber = 1
        var page = pdf.startPage(
            PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        )
        var canvas = page.canvas
        var y = 40f

        /* ================= HEADER ================= */

        fun drawHeader() {
            paint.textSize = 20f
            paint.isFakeBoldText = true
            canvas.drawText("LAPORAN ${jenis.name}", margin, y, paint)

            y += 28
            paint.textSize = 12f
            paint.isFakeBoldText = false
            canvas.drawText("KasApp", margin, y, paint)

            y += 18
            startTime?.let {
                canvas.drawText(
                    "Periode: ${format(it)} - ${format(endTime!!)}",
                    margin,
                    y,
                    paint
                )
                y += 18
            }

            canvas.drawLine(margin, y, pageWidth - margin, y, paint)
            y += 20
        }

        fun drawFooter() {
            paint.textSize = 9f
            canvas.drawText(
                "Dicetak: ${formatDateTime(System.currentTimeMillis())}",
                margin,
                pageHeight - 30f,
                paint
            )
        }

        fun newPage() {
            drawFooter()
            pdf.finishPage(page)

            pageNumber++
            page = pdf.startPage(
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            )
            canvas = page.canvas
            y = 40f

            drawHeader()
        }

        /* ================= SUMMARY ================= */

        fun drawSummary() {
            paint.textSize = 11f

            canvas.drawText("Jumlah Transaksi : ${transaksi.size}", margin, y, paint)
            y += 16
            canvas.drawText("Total Pendapatan : Rp %,.0f".format(totalPendapatan), margin, y, paint)
            y += 20

            val totalPerJenis = transaksi
                .groupBy { it.jenisPembayaran }
                .mapValues { it.value.sumOf { t -> t.jlhTransaksi.toDouble() } }

            canvas.drawText("Rincian Pembayaran:", margin, y, paint)
            y += 16

            totalPerJenis.forEach { (jenis, total) ->
                canvas.drawText("• $jenis : Rp %,.0f".format(total), margin + 10, y, paint)
                y += 14
            }

            y += 10
            canvas.drawLine(margin, y, pageWidth - margin, y, paint)
            y += 20
        }

        /* ================= DRAW ================= */

        drawHeader()
        drawSummary()

        when (jenis) {

            /* ========== HARIAN (PER TRANSAKSI) ========== */
            JenisLaporan.HARIAN -> {

                paint.isFakeBoldText = true
                canvas.drawText("No", margin, y, paint)
                canvas.drawText("ID", margin + 40, y, paint)
                canvas.drawText("Tanggal", margin + 120, y, paint)
                canvas.drawText("Pembayaran", margin + 260, y, paint)
                canvas.drawText("Total (Rp)", margin + 400, y, paint)
                paint.isFakeBoldText = false

                y += 14
                canvas.drawLine(margin, y, pageWidth - margin, y, paint)
                y += 16

                transaksi.forEachIndexed { index, trx ->
                    if (y > pageHeight - 80) newPage()

                    canvas.drawText("${index + 1}", margin, y, paint)
                    canvas.drawText("TRX-${trx.idTransaksi}", margin + 40, y, paint)
                    canvas.drawText(format(trx.tglTransaksi), margin + 120, y, paint)
                    canvas.drawText(trx.jenisPembayaran, margin + 260, y, paint)
                    canvas.drawText("%,.0f".format(trx.jlhTransaksi.toDouble()), margin + 400, y, paint)

                    y += 18
                }
            }

            /* ========== BULANAN ========== */
            JenisLaporan.BULANAN -> {
                val perHari = transaksi.groupBy { dayKey(it.tglTransaksi) }

                perHari.forEach { (_, listHarian) ->
                    val tanggal = listHarian.first().tglTransaksi

                    paint.isFakeBoldText = true
                    canvas.drawText(formatHari(tanggal), margin, y, paint)
                    paint.isFakeBoldText = false
                    y += 16

                    var totalHarian = 0.0

                    listHarian.forEach {
                        if (y > pageHeight - 80) newPage()

                        canvas.drawText(
                            "• TRX-${it.idTransaksi} (${it.jenisPembayaran})",
                            margin + 10,
                            y,
                            paint
                        )
                        canvas.drawText("%,.0f".format(it.jlhTransaksi.toDouble()), margin + 380, y, paint)

                        totalHarian += it.jlhTransaksi.toDouble()
                        y += 14
                    }

                    paint.isFakeBoldText = true
                    canvas.drawText(
                        "Total ${formatHari(tanggal)} : Rp %,.0f".format(totalHarian),
                        margin + 10,
                        y,
                        paint
                    )
                    paint.isFakeBoldText = false
                    y += 26
                }
            }

            /* ========== TAHUNAN ========== */
            JenisLaporan.TAHUNAN -> {
                val perBulan = transaksi.groupBy { monthKey(it.tglTransaksi) }

                perBulan.forEach { (_, listBulanan) ->
                    val bulan = listBulanan.first().tglTransaksi

                    paint.textSize = 13f
                    paint.isFakeBoldText = true
                    canvas.drawText(formatBulan(bulan).uppercase(), margin, y, paint)
                    paint.isFakeBoldText = false
                    paint.textSize = 11f
                    y += 18

                    var totalBulanan = 0.0
                    val perHari = listBulanan.groupBy { dayKey(it.tglTransaksi) }

                    perHari.forEach { (_, listHarian) ->
                        val tanggal = listHarian.first().tglTransaksi
                        canvas.drawText(formatHari(tanggal), margin + 10, y, paint)
                        y += 14

                        var totalHarian = 0.0

                        listHarian.forEach {
                            if (y > pageHeight - 80) newPage()

                            canvas.drawText(
                                "• TRX-${it.idTransaksi} (${it.jenisPembayaran})",
                                margin + 20,
                                y,
                                paint
                            )
                            canvas.drawText("%,.0f".format(it.jlhTransaksi.toDouble()), margin + 380, y, paint)

                            totalHarian += it.jlhTransaksi.toDouble()
                            y += 14
                        }

                        canvas.drawText(
                            "Total ${formatHari(tanggal)} : Rp %,.0f".format(totalHarian),
                            margin + 20,
                            y,
                            paint
                        )
                        y += 18

                        totalBulanan += totalHarian
                    }

                    paint.isFakeBoldText = true
                    canvas.drawText(
                        "Total ${formatBulan(bulan)} : Rp %,.0f".format(totalBulanan),
                        margin + 10,
                        y,
                        paint
                    )
                    paint.isFakeBoldText = false
                    y += 30
                }
            }

            else -> {}
        }

        drawFooter()
        pdf.finishPage(page)

        /* ================= SAVE ================= */

        val resolver = context.contentResolver
        val fileName = "laporan_${System.currentTimeMillis()}.pdf"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Laporan")
        }

        val uri = resolver.insert(
            MediaStore.Files.getContentUri("external"),
            values
        ) ?: throw IOException("Gagal membuat PDF")

        resolver.openOutputStream(uri)?.use { pdf.writeTo(it) }
        pdf.close()
        return uri
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

    private fun formatDateTime(time: Long): String =
        SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id")).format(Date(time))
}
