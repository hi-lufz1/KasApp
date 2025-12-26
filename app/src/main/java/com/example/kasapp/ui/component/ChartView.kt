package com.example.kasapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

@Composable
fun ChartSection(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit,
    dataList: List<ChartData>
) {

    val maxValue = dataList.maxOfOrNull { it.value } ?: 1.0
    val maxHeight = 200.dp

    // ---------- Sumbu Y Dinamis ----------
    fun calculateNiceStep(max: Double): Double {
        val raw = max / 5
        val pow = 10.0.pow(floor(log10(raw)))
        val normalized = raw / pow

        val nice = when {
            normalized < 2 -> 2
            normalized < 5 -> 5
            else -> 10
        }
        return nice * pow
    }

    val step = calculateNiceStep(maxValue)
    val roundedMax = step * 5
    val yAxisValues = (5 downTo 0).map { it * step }

    // ---------- State Scroll ----------
    val scrollState = rememberLazyListState()
    val showScrollIndicator = dataList.size > 6

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        // ---------- Tab Filter ----------
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

        // ---------- Chart + Sumbu Y ----------
        Row(modifier = Modifier.fillMaxWidth()) {

            // ---- Sumbu Y ----
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

            // ---- Chart Bar ----
            LazyRow(
                state = scrollState,
                modifier = Modifier
                    .height(maxHeight + 40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                items(dataList) { data ->
                    val normalizedHeight = (data.value / roundedMax) * maxHeight.value

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Box(
                            modifier = Modifier
                                .height(maxHeight)
//                                .width(if (dataList.size > 8) 32.dp else 40.dp)
                                .width(16.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(Color.White)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(normalizedHeight.dp)
                                    .align(Alignment.BottomCenter)
                                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                    .background(Color(0xFFFFC107))
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = data.label,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // ---------- Scroll Indicator ----------
        if (showScrollIndicator) {

            // ukuran indikator mengikuti posisi scroll
            val maxScroll = scrollState.layoutInfo.totalItemsCount -
                    scrollState.layoutInfo.visibleItemsInfo.size

            val progress = if (maxScroll > 0)
                scrollState.firstVisibleItemIndex.toFloat() / maxScroll
            else 0f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE0E0E0))
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(5.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFFFC107))
                )
            }
        }
        else {
            Spacer(Modifier.padding(top = 11.dp))
        }
    }
}
