package com.example.kasapp.ui.view.kasir

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.example.kasapp.R
import com.example.kasapp.data.entity.MenuMakanan
import com.example.kasapp.ui.viewmodel.Kasir.KasirViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import kotlin.math.roundToInt

@Composable
fun HomeKasirView(
    viewModel: KasirViewModel,
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf("Home") }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onScreenResumed()
        }
    }

    // State untuk posisi FAB yang bisa digeser
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var screenWidth by remember { mutableStateOf(0) }
    var screenHeight by remember { mutableStateOf(0) }
    var fabWidth by remember { mutableStateOf(0) }
    var isInitialized by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Scaffold(
            topBar = {
                KasirTopBar(onBackClick = onBackClick)
            },
//            bottomBar = {
//                KasirBottomBar(
//                    selectedTab = selectedTab,
//                    onTabSelected = { tab ->
//                        selectedTab = tab
//                    }
//                )
//            },
            containerColor = Color.White
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .onSizeChanged { size ->
                        screenWidth = size.width
                        screenHeight = size.height
                        // Set posisi awal FAB di kanan bawah (agak naik) jika belum diinisialisasi
                        if (!isInitialized && screenWidth > 0) {
                            // Posisi awal di kanan bawah, agak naik dari bottom bar
                            offsetX = (screenWidth - 220).toFloat() // Kanan dengan estimasi lebar FAB
                            offsetY = (screenHeight - 250).toFloat() // Naik lebih tinggi dari sebelumnya
                            isInitialized = true
                        }
                    }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
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
                            // Tambah spacing di bawah agar item terakhir tidak tertutup FAB
                            item {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    }
                }

                // Draggable Floating Action Button
                if (uiState.totalCartItems > 0 && isInitialized) {
                    DraggableCheckoutButton(
                        totalHarga = uiState.totalCartPrice,
                        totalItem = uiState.totalCartItems,
                        onClick = onCheckoutClick,
                        offsetX = offsetX,
                        offsetY = offsetY,
                        screenWidth = screenWidth,
                        screenHeight = screenHeight,
                        onDrag = { dragX, dragY ->
                            // Batas kanan disesuaikan dengan lebar FAB yang sebenarnya
                            val maxX = if (fabWidth > 0) {
                                (screenWidth - fabWidth - 16).toFloat()
                            } else {
                                (screenWidth - 220).toFloat()
                            }
                            offsetX = (offsetX + dragX).coerceIn(16f, maxX)
                            offsetY = (offsetY + dragY).coerceIn(0f, (screenHeight - 100).toFloat())
                        },
                        onFabSizeChanged = { width ->
                            fabWidth = width
                            // Update posisi jika FAB keluar dari layar setelah ukuran berubah
                            if (offsetX + width > screenWidth - 16) {
                                offsetX = (screenWidth - width - 16).toFloat()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DraggableCheckoutButton(
    totalHarga: Double,
    totalItem: Int,
    onClick: () -> Unit,
    offsetX: Float,
    offsetY: Float,
    screenWidth: Int,
    screenHeight: Int,
    onDrag: (Float, Float) -> Unit,
    onFabSizeChanged: (Int) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .onSizeChanged { size ->
                onFabSizeChanged(size.width)
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
    ) {
        Card(
            modifier = Modifier
                .height(56.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(50),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.75f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = formatRupiah(totalHarga),
                    color = Color.White,
                    fontSize = 16.sp,
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
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        // Badge untuk jumlah item
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 2.dp, y = (-6).dp),
            shape = CircleShape,
            color = Color(0xFFFE8235),
            border = BorderStroke(2.dp, Color.White),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .defaultMinSize(minWidth = 24.dp, minHeight = 24.dp)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
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
            BottomBarItem(
                iconRes = R.drawable.history,
                label = "Riwayat",
                isSelected = selectedTab == "Riwayat",
                onClick = { onTabSelected("Riwayat") }
            )

            BottomBarItem(
                iconRes = R.drawable.home,
                label = "Home",
                isSelected = selectedTab == "Home",
                onClick = { onTabSelected("Home") }
            )

            BottomBarItem(
                iconRes = R.drawable.backup,
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

        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            colorFilter = if (isSelected) {
                ColorFilter.tint(Color(0xFFFFB300))
            } else {
                ColorFilter.tint(Color.Gray)
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFFFB300) else Color.Gray
        )
    }
}

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

fun formatRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
}