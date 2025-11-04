package com.example.kasapp.ui.view.menu

// Import dari HomeMenuView lama
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.ui.viewmodel.Menu.HomeMenuViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory

// Import dari KelolaMenuScreen
import com.example.kasapp.ui.view.menu.components.FilterButton
import com.example.kasapp.ui.view.menu.components.TopBarKelolaMenu
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.data.entity.MenuMakanan
import com.example.kasapp.ui.view.menu.components.MenuItemCard

@Composable
fun HomeMenuView(
    // Parameter ini diterima dari PengelolaHalaman (NavHost)
    onTambahMenuClick: () -> Unit,
    onEditMenuClick: (Int) -> Unit
) {
    // 1. Panggil ViewModel (dari HomeMenuView lama)
    val viewModel: HomeMenuViewModel = viewModel(factory = ViewModelFactory.Factory)

    // 2. Ambil data UI (dari KelolaMenuScreen lama)
    val uiState by viewModel.uiState.collectAsState()
    val filteredMenus: List<MenuMakanan> = uiState.listMenu
    val selectedFilter: String = uiState.selectedFilter

    // State untuk Dialog Konfirmasi Hapus
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var menuToDelete by remember { mutableStateOf<MenuMakanan?>(null) }


    // 3. Tampilkan UI (dari KelolaMenuScreen lama)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFB300)) // Warna latar root (oranye)
    ) {
        // 🔶 Top bar
        TopBarKelolaMenu(
            searchText = uiState.searchQuery,
            onSearchChange = { viewModel.onSearchChange(it) }
        )

        // 2. Column Konten (Area dengan "Lekukan")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFFFF9EF)) // Background cream
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // 🔘 Tombol filter kategori
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton (
                    text = "Semua",
                    count = uiState.countSemua,
                    selected = selectedFilter == "Semua",
                    onClick = { viewModel.onFilterChange("Semua") }
                )
                FilterButton(
                    text = "Makanan",
                    count = uiState.countMakanan,
                    selected = selectedFilter == "Makanan",
                    onClick = { viewModel.onFilterChange("Makanan") }
                )
                FilterButton(
                    text = "Minuman",
                    count = uiState.countMinuman,
                    selected = selectedFilter == "Minuman",
                    onClick = { viewModel.onFilterChange("Minuman") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 🍜 Daftar menu (scrollable) atau Tampilan Kosong/Loading
            Box(modifier = Modifier.weight(1f)) {
                when {
                    uiState.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    filteredMenus.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Belum ada menu.\nSilakan tambahkan menu baru.",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp)
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(filteredMenus, key = { it.idMenu }) { menu ->
                                MenuItemCard(
                                    menu = menu,
                                    onEditClick = { onEditMenuClick(menu.idMenu) },
                                    onDeleteClick = { selectedMenu ->
                                        menuToDelete = selectedMenu
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            } // Akhir Box Daftar Menu

            // ➕ Tombol tambah menu di bawah
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 35.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    onClick = onTambahMenuClick, // Panggil event navigasi
                    modifier = Modifier
                        .width(180.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEEEEEE),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Tambah Menu",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 24.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(42.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFFB300)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        } // --- Akhir Column Konten
    } // --- Akhir Column Root

    // Tampilkan Dialog Konfirmasi Hapus jika state true
    if (showDeleteDialog && menuToDelete != null) {
        DeleteConfirmationDialog(
            menuName = menuToDelete!!.namaMenu,
            onConfirm = {
                viewModel.deleteMenu(menuToDelete!!)
                showDeleteDialog = false
                menuToDelete = null
            },
            onDismiss = {
                showDeleteDialog = false
                menuToDelete = null
            }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    menuName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Hapus") },
        text = { Text("Apakah Anda yakin ingin menghapus menu \"$menuName\"?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Ya", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tidak")
            }
        }
    )
}