package com.example.kasapp.data.repository

import com.example.kasapp.data.model.ChartData


class ChartRepository {
    fun getDailyData(): List<ChartData> = listOf(
        ChartData("Sen", 10000000.0),
        ChartData("Sel", 9000000.0),
        ChartData("Rab", 5000000.0),
        ChartData("Kam", 3000000.0),
        ChartData("Jum", 8000000.0),
        ChartData("Sab", 2000000.0),
        ChartData("Min", 4000000.0)
    )

    fun getMonthlyData(): List<ChartData> = listOf(
        ChartData("Jan", 15000000.0),
        ChartData("Feb", 18000000.0),
        ChartData("Mar", 12000000.0)
    )

    fun getYearlyData(): List<ChartData> = listOf(
        ChartData("2023", 120000000.0),
        ChartData("2024", 145000000.0)
    )
}
