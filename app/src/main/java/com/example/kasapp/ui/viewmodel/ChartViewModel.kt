package com.example.kasapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.data.model.ChartData
import com.example.kasapp.data.repository.ChartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChartViewModel(
    private val repository: ChartRepository = ChartRepository()
) : ViewModel() {

    private val _chartData = MutableStateFlow<List<ChartData>>(emptyList())
    val chartData: StateFlow<List<ChartData>> = _chartData

    private val _selectedPeriod = MutableStateFlow("Hari")
    val selectedPeriod: StateFlow<String> = _selectedPeriod

    init {
        loadChartData("Hari")
    }

    fun loadChartData(period: String) {
        viewModelScope.launch {
            _selectedPeriod.value = period
            _chartData.value = when (period) {
                "Hari" -> repository.getDailyData()
                "Bulan" -> repository.getMonthlyData()
                "Tahun" -> repository.getYearlyData()
                else -> emptyList()
            }
        }
    }
}
