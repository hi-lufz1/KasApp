package com.example.kasapp.ui.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.R
import com.example.kasapp.ui.component.BottomBar
import com.example.kasapp.ui.component.ChartSection
import com.example.kasapp.ui.viewmodel.ChartViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    name: String?,
    email: String?,
    onNavigateToKelolaMenu: () -> Unit,
    onNavigateToKasir: () -> Unit,
    onNavigateToRiwayat: () -> Unit,
    onNavigateToLaporan: () -> Unit,
    onNavigateToBackup: () -> Unit,
    onNavigateToProfile: (String?, String?) -> Unit,
    viewModel: ChartViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    var selectedTab by remember { mutableStateOf("Home") }
    val data by viewModel.chartData.collectAsState()
    val period by viewModel.selectedPeriod.collectAsState()
    val totalPendapatan by viewModel.totalPendapatan.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.loadChartData("Hari")
        viewModel.loadTotalPendapatan("Hari ini")
    }

    Scaffold(bottomBar = {
        BottomBar(selectedTab = selectedTab) { tab ->
            selectedTab = tab
            when (tab) {
                "Riwayat" -> onNavigateToRiwayat()
                "Home" -> { /* tetap di halaman ini */
                }

                "Backup" ->
                    onNavigateToBackup()
            }
        }
    }
    )
    { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                // 🔹 Header
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { onNavigateToProfile(name, email) }
                    )
                    {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Selamat Datang,",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = name ?: "Pengguna",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🔹 Card Total Pendapatan
                // 🔹 Card Total Pendapatan dengan Dropdown
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFFFF4E0))
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedPeriodText by remember { mutableStateOf("Hari ini") }
                    val options = listOf("Hari ini", "Bulan ini", "Tahun ini")

                    Column {
                        // Bagian atas: judul dan dropdown
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { expanded = !expanded }
                                ) {
                                    Text(
                                        text = "Total Pendapatan",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF5C4A1A)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.arrow_down),
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { expanded = !expanded }
                                ) {
                                    Text(
                                        text = selectedPeriodText,
                                        fontSize = 12.sp,
                                        color = Color(0xFF5C4A1A)
                                    )

                                }

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    options.forEach { option ->
                                        DropdownMenuItem(
                                            text = { Text(option) },
                                            onClick = {
                                                selectedPeriodText = option
                                                expanded = false
                                                viewModel.loadTotalPendapatan(option)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Rp " + "%,d".format(totalPendapatan).replace(",", "."),
                            color = Color(0xFFFF6B00),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                Spacer(modifier = Modifier.height(24.dp))

                ChartSection(
                    selectedPeriod = period,
                    onPeriodChange = { viewModel.loadChartData(it) },
                    dataList = data
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 🔹 Menu List
                MenuItem(
                    title = "Kasir",
                    icon = R.drawable.receipt,
                    onClick = onNavigateToKasir
                )
                Spacer(modifier = Modifier.height(12.dp))
                MenuItem(
                    title = "Kelola Menu",
                    icon = R.drawable.food,
                    onClick = onNavigateToKelolaMenu
                )
                Spacer(modifier = Modifier.height(12.dp))
                MenuItem(
                    title = "Laporan Keuangan",
                    icon = R.drawable.result,
                    onClick = onNavigateToLaporan
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

    }
}

@Composable
fun MenuItem(title: String, icon: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFC107))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}
