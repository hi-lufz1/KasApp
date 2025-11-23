package com.example.kasapp.repository

import android.util.Log
import com.example.kasapp.data.dao.TransaksiDao
import com.example.kasapp.data.model.ChartData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChartRepository(
    private val dao: TransaksiDao
) {

    // -------------------- HARIAN --------------------
    fun getDailyData(): Flow<List<ChartData>> {
        val cal = Calendar.getInstance()

        val end = cal.timeInMillis

        cal.add(Calendar.DAY_OF_YEAR, -6)
        val start = cal.timeInMillis

        val dayNames = listOf("Sen","Sel","Rab","Kam","Jum","Sab","Min")

        return dao.getTransaksiByDateRange(start, end).map { transaksiList ->

            Log.d("DEBUG", "Jumlah transaksi minggu ini: ${transaksiList.size}")
            transaksiList.forEach {
                Log.d("DEBUG", "Tgl: ${SimpleDateFormat("yyyy-MM-dd").format(it.tglTransaksi)}")
            }
            (0..6).map { i ->
                val target = Calendar.getInstance()
                target.timeInMillis = start
                target.add(Calendar.DAY_OF_YEAR, i)

                val targetStr = SimpleDateFormat("yyyyMMdd").format(target.time)

                val total = transaksiList
                    .filter {
                        val tgl = SimpleDateFormat("yyyyMMdd").format(it.tglTransaksi)
                        tgl == targetStr
                    }
                    .sumOf { it.jlhTransaksi }

                ChartData(label = dayNames[i], value = total)
            }
        }
    }


    // -------------------- BULANAN --------------------
    fun getMonthlyData(): Flow<List<ChartData>> {
        val cal = Calendar.getInstance()

        // Awal tahun
        cal.set(Calendar.DAY_OF_YEAR, 1)
        val start = cal.timeInMillis

        // Akhir tahun
        cal.add(Calendar.YEAR, 1)
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val end = cal.timeInMillis

        val monthLabels = listOf(
            "Jan","Feb","Mar","Apr","Mei","Jun",
            "Jul","Agu","Sep","Okt","Nov","Des"
        )

        return dao.getTransaksiByDateRange(start, end).map { transaksiList ->
            monthLabels.mapIndexed { index, bulan ->
                val total = transaksiList
                    .filter {
                        val m = Calendar.getInstance()
                        m.timeInMillis = it.tglTransaksi
                        m.get(Calendar.MONTH) == index
                    }
                    .sumOf { it.jlhTransaksi }

                ChartData(label = bulan, value = total)
            }
        }
    }

    // -------------------- TAHUNAN --------------------
    fun getYearlyData(): Flow<List<ChartData>> {
        return dao.getAllTransaksi().map { transaksiList ->
            transaksiList
                .groupBy {
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = it.tglTransaksi
                    cal.get(Calendar.YEAR).toString()
                }
                .map { (year, data) ->
                    ChartData(label = year, value = data.sumOf { it.jlhTransaksi })
                }
                .sortedBy { it.label }
        }
    }
}
