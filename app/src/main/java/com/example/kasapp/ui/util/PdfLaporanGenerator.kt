package com.example.kasapp.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.ui.view.laporan.JenisLaporan
import java.io.File
import java.io.FileOutputStream
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
        val file = generatePdf(
            context,
            jenis,
            transaksi,
            totalPendapatan,
            startTime,
            endTime
        )

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

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
    ): File {

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

        // ===== HEADER =====
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

        // ===== FOOTER =====
        fun drawFooter() {
            paint.textSize = 9f
            canvas.drawText(
                "Dicetak: ${formatDateTime(System.currentTimeMillis())}",
                margin,
                pageHeight - 30f,
                paint
            )
        }

        // ===== HEADER TABEL =====
        fun drawTableHeader() {
            paint.isFakeBoldText = true
            paint.textSize = 11f

            canvas.drawText("No", margin, y, paint)
            canvas.drawText("ID Transaksi", margin + 40, y, paint)
            canvas.drawText("Tanggal", margin + 180, y, paint)
            canvas.drawText("Total (Rp)", margin + 340, y, paint)

            y += 14
            canvas.drawLine(margin, y, pageWidth - margin, y, paint)
            y += 16

            paint.isFakeBoldText = false
        }

        // ===== SUMMARY =====
        fun drawSummary() {
            paint.textSize = 11f
            canvas.drawText("Jumlah Transaksi : ${transaksi.size}", margin, y, paint)
            y += 16
            canvas.drawText(
                "Total Pendapatan : Rp %,.0f".format(totalPendapatan),
                margin,
                y,
                paint
            )
            y += 20
            canvas.drawLine(margin, y, pageWidth - margin, y, paint)
            y += 20
        }

        // ===== NEW PAGE =====
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
            drawTableHeader()
        }

        // ===== START DRAWING =====
        drawHeader()
        drawSummary()
        drawTableHeader()

        transaksi.forEachIndexed { index, trx ->
            if (y > pageHeight - 80) {
                newPage()
            }

            canvas.drawText("${index + 1}", margin, y, paint)
            canvas.drawText("TRX-${trx.idTransaksi}", margin + 40, y, paint)
            canvas.drawText(format(trx.tglTransaksi), margin + 180, y, paint)
            canvas.drawText(
                "%,.0f".format(trx.jlhTransaksi),
                margin + 340,
                y,
                paint
            )

            y += 18
        }

        drawFooter()
        pdf.finishPage(page)

        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "laporan"
        )
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "laporan_${System.currentTimeMillis()}.pdf")
        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        return file
    }

    private fun format(time: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale("id")).format(Date(time))

    private fun formatDateTime(time: Long): String =
        SimpleDateFormat("dd MMM yyyy HH:mm", Locale("id")).format(Date(time))
}
