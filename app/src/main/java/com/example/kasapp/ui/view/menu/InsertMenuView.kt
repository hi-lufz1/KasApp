package com.example.kasapp.ui.view.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.R
import com.example.kasapp.ui.view.menu.components.CategoryButton
import com.example.kasapp.ui.view.menu.components.CustomTextField
import com.example.kasapp.ui.viewmodel.Menu.InsertMenuViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun InsertMenuView(
    viewModel: InsertMenuViewModel = viewModel(factory = ViewModelFactory.Factory),
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit // Event ini akan diarahkan ke SuccessScreen oleh PengelolaHalaman
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFEBC2F)) // Latar oranye tua
    ) {
        InsertTopBar(onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFFFF9EF)) // Background cream
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // --- Nama Menu ---
                Text(
                    text = "Nama Menu",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    value = uiState.namaMenu,
                    onValueChange = { viewModel.onNamaChange(it) },
                    placeholder = "Masukkan Nama Menu",
                    leadingIconRes = R.drawable.makanan, // GANTI dengan ikon Anda
                    isError = uiState.namaMenuError != null
                )
                uiState.namaMenuError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Harga Menu ---
                Text(
                    text = "Harga Menu",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    value = uiState.hargaMenu,
                    onValueChange = { viewModel.onHargaChange(it) },
                    placeholder = "Masukkan Harga Menu", // Placeholder ditambahkan
                    leadingIconText = "Rp",
                    keyboardType = KeyboardType.Number,
                    isError = uiState.hargaMenuError != null
                )
                uiState.hargaMenuError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Kategori ---
                Text(
                    text = "Kategori",
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(), // Agar ke tengah
                    horizontalArrangement = Arrangement.Center // Agar ke tengah
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        CategoryButton(
                            text = "Minuman",
                            iconRes = R.drawable.minuman, // GANTI dengan ikon Anda
                            selected = uiState.jenisMenu == "Minuman",
                            onClick = { viewModel.onJenisChange("Minuman") }
                        )
                        CategoryButton(
                            text = "Makanan",
                            iconRes = R.drawable.makanann, // GANTI dengan ikon Anda
                            selected = uiState.jenisMenu == "Makanan",
                            onClick = { viewModel.onJenisChange("Makanan") }
                        )
                    }
                }
                uiState.jenisMenuError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

            } // Akhir Kolom Scrollable

            // --- Tombol Simpan ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                SaveButton(
                    enabled = true, // Selalu oranye
                    onClick = {
                        scope.launch {
                            val success = viewModel.saveMenu()
                            if (success) {
                                withContext(Dispatchers.Main) {
                                    onSaveClick() // Panggil navigasi ke success
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

// --- HELPER COMPOSABLE (TopBar, TextField, CategoryButton, SaveButton) ---

@Composable
fun InsertTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.back), // GANTI dengan ikon back Anda
            contentDescription = "Back",
            modifier = Modifier
                .size(55.dp)
                .padding(4.dp)
                .clickable { onBackClick() },
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Tambah Menu",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3A1D00)
        )
    }
}



@Composable
fun SaveButton(
    enabled: Boolean, // Tidak dipakai untuk warna
    onClick: () -> Unit
) {
    val mainColor = Color(0xFFFFB300) // Oranye Tua
    val iconBgColor = Color(0xFFFFE0B2) // Oranye Muda (Cream)
    val textColor = Color.Black // Selalu Hitam

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(48.dp),
        shape = RoundedCornerShape(20), // Bentuk Pill
        color = mainColor, // Warna utama
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
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tambah Menu",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp, topStart = 10.dp, bottomStart = 10.dp)) // Clip Pill
                    .background(iconBgColor), // Latar ikon
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    fontSize = 30.sp,

                    color = textColor
                )
            }
        }
    }
}


