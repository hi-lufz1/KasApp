package com.example.kasapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kasapp.data.model.ChartData
import com.example.kasapp.repository.ChartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChartViewModel(
    private val repository: ChartRepository
) : ViewModel() {

    private val _chartData = MutableStateFlow<List<ChartData>>(emptyList())
    val chartData: StateFlow<List<ChartData>> = _chartData

    private val _selectedPeriod = MutableStateFlow("Hari")
    val selectedPeriod: StateFlow<String> = _selectedPeriod

    fun loadChartData(period: String) {
        _selectedPeriod.value = period

        viewModelScope.launch {
            when (period) {
                "Hari" -> repository.getDailyData().collect { _chartData.value = it }
                "Bulan" -> repository.getMonthlyData().collect { _chartData.value = it }
                "Tahun" -> repository.getYearlyData().collect { _chartData.value = it }
            }
        }
    }
}
