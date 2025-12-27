package com.example.kasapp.ui.view.menu.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun KelolaMenuTopBar(
    title: String,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column {

        // ================= TOP APP BAR (SAFE AREA) =================
        TopAppBar(
            title = {
                Text(
                    text = title,
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

        // ================= SEARCH BAR =================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFB300))
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center   // 🔥 biar search di tengah
        ) {

            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.8f)   // ✅ SEARCH DIPENDEKIN (80%)
                    .height(45.dp),
                shape = RoundedCornerShape(15.dp),
                color = Color(0xFFF2EFEA)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Search",
                        modifier = Modifier.size(24.dp),
                        tint = Color.Gray
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    BasicTextField(
                        value = searchText,
                        onValueChange = onSearchChange,
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (searchText.isEmpty()) {
                                    Text(
                                        text = "Cari Menu",
                                        fontSize = 15.sp,
                                        color = Color.Gray
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