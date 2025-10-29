package com.example.kasapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.data.model.ChartData


@Composable
fun ChartSection(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    dataList: List<ChartData>
) {
    val maxValue = dataList.maxOfOrNull { it.value } ?: 1.0
    val maxHeight = 200.dp

    // 🔹 Tentukan kelipatan sumbu Y dinamis (6 level termasuk 0)
    val roundedMax = ((maxValue / 1_000_000).toInt() + 1) * 1_000_000 // pembulatan agar rapi
    val step = roundedMax / 5  // 5 interval = 6 titik
    val yAxisValues = (5 downTo 0).map { i -> step * i.toDouble() }



    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 🔹 Tab (Hari, Bulan, Tahun)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Hari", "Bulan", "Tahun").forEach { label ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selectedPeriod == label) Color(0xFFFFE8B2) else Color.Transparent)
                        .clickable { onPeriodChange(label) }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (selectedPeriod == label) Color(0xFF5C4A1A) else Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Sumbu Y + Chart Bar
        Row(modifier = Modifier.fillMaxWidth()) {
            // Sumbu Y
            Column(
                modifier = Modifier
                    .height(maxHeight)
                    .width(90.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                yAxisValues.forEach { value ->
                    Text(
                        text = "Rp %,d".format(value.toLong()).replace(",", "."),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }

            // Chart
            LazyRow(
                modifier = Modifier
                    .height(maxHeight + 40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                items(dataList) { data ->
                    val height = (data.value / maxValue) * maxHeight.value
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .height(maxHeight)
                                .width(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(height.dp)
                                    .align(Alignment.BottomCenter)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFC107))
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = data.label, color = Color.Gray)
                    }
                }
            }
        }
    }
}
