package com.example.kasapp.ui.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.kasapp.data.entity.Transaksi
import com.example.kasapp.ui.view.laporan.JenisLaporan
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExcelLaporanGenerator {

    fun generateExcelAndOpen(
        context: Context,
        jenis: JenisLaporan,
        transaksi: List<Transaksi>,
        totalPendapatan: Double,
        startTime: Long?,
        endTime: Long?
    ) {
        val file = generateExcel(
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
            setDataAndType(
                uri,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(intent)
    }

    private fun generateExcel(
        context: Context,
        jenis: JenisLaporan,
        transaksi: List<Transaksi>,
        totalPendapatan: Double,
        startTime: Long?,
        endTime: Long?
    ): File {

        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Laporan")

        var rowIndex = 0

        // ===== STYLE =====
        val headerStyle = workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                bold = true
                fontHeightInPoints = 12
            })
        }

        val boldStyle = workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                bold = true
            })
        }

        // ===== JUDUL =====
        sheet.createRow(rowIndex).apply {
            createCell(0).apply {
                setCellValue("LAPORAN ${jenis.name}")
                cellStyle = headerStyle
            }
        }
        rowIndex++

        sheet.createRow(rowIndex).createCell(0).setCellValue("KasApp")
        rowIndex++

        startTime?.let {
            sheet.createRow(rowIndex).createCell(0).setCellValue(
                "Periode: ${format(it)} - ${format(endTime!!)}"
            )
            rowIndex++
        }

        rowIndex++ // spasi

        // ===== SUMMARY =====
        sheet.createRow(rowIndex).apply {
            createCell(0).setCellValue("Jumlah Transaksi")
            createCell(1).setCellValue(transaksi.size.toDouble())
        }
        rowIndex++

        sheet.createRow(rowIndex).apply {
            createCell(0).setCellValue("Total Pendapatan")
            createCell(1).setCellValue(totalPendapatan)
        }
        rowIndex += 2

        // ===== HEADER TABEL =====
        sheet.createRow(rowIndex).apply {
            createCell(0).apply {
                setCellValue("No")
                cellStyle = boldStyle
            }
            createCell(1).apply {
                setCellValue("ID Transaksi")
                cellStyle = boldStyle
            }
            createCell(2).apply {
                setCellValue("Tanggal")
                cellStyle = boldStyle
            }
            createCell(3).apply {
                setCellValue("Total (Rp)")
                cellStyle = boldStyle
            }
        }
        rowIndex++

        // ===== DATA =====
        transaksi.forEachIndexed { index, trx ->
            sheet.createRow(rowIndex).apply {
                createCell(0).setCellValue((index + 1).toDouble())
                createCell(1).setCellValue("TRX-${trx.idTransaksi}")
                createCell(2).setCellValue(format(trx.tglTransaksi))
                createCell(3).setCellValue(trx.jlhTransaksi)
            }
            rowIndex++
        }

        // ===== AUTO SIZE =====
        for (i in 0..3) {
            sheet.autoSizeColumn(i)
        }

        val dir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "laporan"
        )
        if (!dir.exists()) dir.mkdirs()

        val file = File(dir, "laporan_${System.currentTimeMillis()}.xlsx")
        val fos = FileOutputStream(file)
        workbook.write(fos)
        fos.close()
        workbook.close()

        return file
    }

    private fun format(time: Long): String =
        SimpleDateFormat("dd MMM yyyy", Locale("id")).format(Date(time))
}
