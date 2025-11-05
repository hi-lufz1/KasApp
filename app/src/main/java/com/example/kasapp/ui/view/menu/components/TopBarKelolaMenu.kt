package com.example.kasapp.ui.view.menu.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarKelolaMenu(
    modifier: Modifier = Modifier,
    searchText: String, // <-- Terima data dari ViewModel
    onSearchChange: (String) -> Unit, // <-- Kirim event ke ViewModel
    onBackClick: () -> Unit = {}
) {
    // var searchText by remember { mutableStateOf("") } // <-- Dihapus

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFEBC2F)) // Warna kuning solid
            .padding(bottom = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bagian atas (Back + Title)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(55.dp)
                        .padding(4.dp),
                    // .clickable { onBackClick() } // <-- Uncomment jika perlu
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kelola Menu",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3A1D00)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- Search Bar Kustom ---
            val textStyle = TextStyle(fontSize = 18.sp, color = Color.Black)

            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(45.dp),
                color = Color(0xFFF2EFEA),
                shape = RoundedCornerShape(15.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search Icon",
                        modifier = Modifier.size(26.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = searchText, // <-- Hubungkan ke parameter
                        onValueChange = onSearchChange, // <-- Hubungkan ke parameter
                        modifier = Modifier.weight(1f),
                        textStyle = textStyle,
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = "Cari Menu",
                                        style = textStyle.copy(color = Color.Gray),
                                        fontSize = 15.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}