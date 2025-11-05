package com.example.kasapp.ui.view.kasir



import androidx.compose.foundation.BorderStroke // <-- Import untuk BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border // <-- Import untuk border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
// --- IMPORT BARU ---
import androidx.compose.ui.platform.LocalLifecycleOwner
// --------------------
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
// --- IMPORT BARU ---
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
// --------------------
import com.example.kasapp.R
import com.example.kasapp.data.entity.MenuMakanan
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale
// Import Modifier.shadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset


@Composable
fun HomeKasirView(
    viewModel: KasirViewModel,
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // State untuk melacak tab yang dipilih di Bottom Bar
    var selectedTab by remember { mutableStateOf("Home") }

    // --- TAMBAHAN UNTUK MEMBERSIHKAN KERANJANG ---
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        // Ulangi setiap kali lifecycle masuk state RESUMED
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Panggil ini setiap kali halaman Kasir muncul kembali
            viewModel.onScreenResumed()
        }
    }
    // ---------------------------------------------

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            topBar = {
                KasirTopBar(onBackClick = onBackClick)
            },

            // Bottom Navigation Bar
            bottomBar = {
                KasirBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { tab ->
                        selectedTab = tab
                        // Di sini Anda bisa menambahkan navigasi sesuai kebutuhan
                        // Misalnya: when(tab) { "Riwayat" -> navigateToRiwayat() }
                    }
                )
            },

            floatingActionButton = {
                if (uiState.totalCartItems > 0) {
                    HomeKasirCheckoutButton(
                        totalHarga = uiState.totalCartPrice,
                        totalItem = uiState.totalCartItems,
                        onClick = onCheckoutClick
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = Color.White
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Filter Kategori
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    color = Color.White,
                    shadowElevation = 0.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val filterOptions = listOf("Semua", "Makanan", "Minuman")
                        filterOptions.forEach { filterName ->
                            KasirFilterChip(
                                text = filterName,
                                selected = uiState.selectedFilter == filterName,
                                onClick = { viewModel.changeFilter(filterName) }
                            )
                            if (filterName != filterOptions.last()) {
                                Spacer(modifier = Modifier.width(45.dp))
                            }
                        }
                    }
                }

                // Daftar Menu
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.listMenu.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada menu yang ditambahkan.\nSilakan pergi ke 'Kelola Menu' untuk menambah menu.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(25.dp)
                    ) {
                        items(uiState.listMenu, key = { it.idMenu }) { menu ->
                            val cartItem = uiState.cart.find { it.menu.idMenu == menu.idMenu }
                            val itemCount = cartItem?.quantity ?: 0

                            KasirMenuItem(
                                menu = menu,
                                itemCount = itemCount,
                                onPilih = { viewModel.addItemToCart(menu) },
                                onTambah = { viewModel.addItemToCart(menu) },
                                onKurang = {
                                    if (cartItem != null) {
                                        viewModel.decreaseItemInCart(cartItem)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// === BOTTOM NAVIGATION BAR ===
@Composable
private fun KasirBottomBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab Riwayat
            BottomBarItem(
                iconRes = R.drawable.riwayat, // Ganti dengan nama resource icon Anda
                label = "Riwayat",
                isSelected = selectedTab == "Riwayat",
                onClick = { onTabSelected("Riwayat") }
            )

            // Tab Home
            BottomBarItem(
                iconRes = R.drawable.home, // Ganti dengan nama resource icon Anda
                label = "Home",
                isSelected = selectedTab == "Home",
                onClick = { onTabSelected("Home") }
            )

            // Tab Backup
            BottomBarItem(
                iconRes = R.drawable.backup, // Ganti dengan nama resource icon Anda
                label = "Backup",
                isSelected = selectedTab == "Backup",
                onClick = { onTabSelected("Backup") }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Penanda di atas (Line Indicator)
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(3.dp)
                .background(
                    color = if (isSelected) Color(0xFFFFB300) else Color.Transparent,
                    shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp)
                )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Icon
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            colorFilter = if (isSelected) {
                ColorFilter.tint(Color(0xFFFFB300)) // Warna oranye saat dipilih
            } else {
                ColorFilter.tint(Color.Gray) // Warna abu-abu saat tidak dipilih
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Label Text
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFFB300) else Color.Gray
        )
    }
}
// === END BOTTOM NAVIGATION BAR ===

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KasirTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Kasir",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .size(55.dp)
                    .padding(4.dp)
                    .clickable { onBackClick() },
                contentScale = ContentScale.Fit
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        windowInsets = WindowInsets(0.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KasirFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        label = {
            Text(
                text,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color.White else Color.Black
            )
        },
        shape = RoundedCornerShape(90),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.White,
            labelColor = Color.Black,
            selectedContainerColor = Color(0xFFFFB300),
            selectedLabelColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = Color.Gray,
            selectedBorderColor = Color.Transparent,
            borderWidth = 1.dp
        )
    )
}

@Composable
private fun KasirMenuItem(
    menu: MenuMakanan,
    itemCount: Int,
    onPilih: () -> Unit,
    onTambah: () -> Unit,
    onKurang: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = menu.namaMenu,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = menu.jenisMenu,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = formatRupiah(menu.hargaMenu),
                    fontSize = 16.sp,
                    color = Color(0xFFFF9800),
                    fontWeight = FontWeight.Bold
                )
            }

            if (itemCount == 0) {
                Button(
                    onClick = onPilih,
                    modifier = Modifier
                        .height(35.dp)
                        .width(85.dp),
                    shape = RoundedCornerShape(30),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFB300)
                    )
                ) {
                    Text("Pilih", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                QuantityControl(
                    itemCount = itemCount,
                    onTambah = onTambah,
                    onKurang = onKurang
                )
            }
        }
    }
}

@Composable
private fun QuantityControl(
    itemCount: Int,
    onTambah: () -> Unit,
    onKurang: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .background(
                    Color(0xFFFFF0F0),
                    RoundedCornerShape(12.dp)
                )
                .clickable { onKurang() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "-",
                fontSize = 22.sp,
                color = Color.Red
            )
        }

        Text(
            text = itemCount.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Box(
            modifier = Modifier
                .size(35.dp)
                .background(
                    Color(0xFFFFB300),
                    RoundedCornerShape(12.dp)
                )
                .clickable { onTambah() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 22.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun HomeKasirCheckoutButton(
    totalHarga: Double,
    totalItem: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(
                bottom = 40.dp,
                start = 230.dp,
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .height(56.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = formatRupiah(totalHarga),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.right),
                        contentDescription = "Checkout",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = -3.dp, y = (-3).dp),
            shape = CircleShape,
            color = Color(0xFFFE8235),
            border = BorderStroke(2.dp, Color.White),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 24.dp, minHeight = 24.dp)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = totalItem.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
}