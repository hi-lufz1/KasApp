package com.example.kasapp.ui.view.menu

import androidx.compose.foundation.Image
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertMenuView(
    viewModel: InsertMenuViewModel = viewModel(factory = ViewModelFactory.Factory),
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFB300))
    ) {

        // ================= TOP APP BAR =================
        TopAppBar(
            title = {
                Text(
                    text = "Tambah Menu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            },
            navigationIcon = {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .size(45.dp)
                        .padding(4.dp)
                        .clickable { onBackClick() },
                    contentScale = ContentScale.Fit
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFFB300)
            )
        )

        // ================= CONTENT =================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFFFF9EF))
        ) {

            // ================= FORM =================
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                // ===== NAMA MENU =====
                Text("Nama Menu", fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    value = uiState.namaMenu,
                    onValueChange = viewModel::onNamaChange,
                    placeholder = "Masukkan Nama Menu",
                    leadingIconRes = R.drawable.cutlery,
                    isError = uiState.namaMenuError != null
                )
                uiState.namaMenuError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== HARGA MENU =====
                Text("Harga Menu", fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                CustomTextField(
                    value = uiState.hargaMenu,
                    onValueChange = viewModel::onHargaChange,
                    placeholder = "Masukkan Harga Menu",
                    leadingIconText = "Rp",
                    keyboardType = KeyboardType.Number,
                    isError = uiState.hargaMenuError != null
                )
                uiState.hargaMenuError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ===== KATEGORI =====
                Text("Kategori", fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CategoryButton(
                        modifier = Modifier.weight(1f),
                        text = "Minuman",
                        iconRes = R.drawable.minuman,
                        selected = uiState.jenisMenu == "Minuman",
                        onClick = { viewModel.onJenisChange("Minuman") }
                    )
                    CategoryButton(
                        modifier = Modifier.weight(1f),
                        text = "Makanan",
                        iconRes = R.drawable.makanann,
                        selected = uiState.jenisMenu == "Makanan",
                        onClick = { viewModel.onJenisChange("Makanan") }
                    )
                }
                uiState.jenisMenuError?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }

            // ================= SAVE BUTTON =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                SaveButton(
                    onClick = {
                        scope.launch {
                            val success = viewModel.saveMenu()
                            if (success) {
                                withContext(Dispatchers.Main) {
                                    onSaveClick()
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SaveButton(
    onClick: () -> Unit
) {
    val mainColor = Color(0xFFFFB300)
    val iconBgColor = Color(0xFFFFE0B2)
    val textColor = Color.Black

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(48.dp),
        shape = RoundedCornerShape(20.dp),
        color = mainColor,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f),
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
                    .clip(RoundedCornerShape(20.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
        }
    }
}
