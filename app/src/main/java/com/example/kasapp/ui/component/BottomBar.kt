package com.example.kasapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.kasapp.R


@Composable
fun BottomBar(selectedTab: String, onTabSelected: (String) -> Unit) {
    val selectedColor = Color(0xFFFF9800) // Oranye
    val unselectedColor = Color.Gray

    Box( // Gunakan Box agar bisa tambahkan shadow di belakang NavigationBar
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                clip = false, // supaya bayangan terlihat di luar clip
                ambientColor = Color.Black.copy(alpha = 0.15f),
                spotColor = Color.Black.copy(alpha = 0.15f)
            )
    ) {
        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
        ) {
            listOf("Riwayat", "Home", "Backup").forEach { tab ->
                val isSelected = selectedTab == tab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Rectangle indicator di atas ikon jika dipilih
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .width(24.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(1.5.dp))
                                        .background(selectedColor)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(3.dp))
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Icon(
                                painter = painterResource(
                                    id = when (tab) {
                                        "Riwayat" -> R.drawable.history
                                        "Home" -> R.drawable.home
                                        "Backup" -> R.drawable.backup
                                        else -> R.drawable.cutlery
                                    }
                                ),
                                modifier = Modifier.size(
                                    when (tab) {
                                        "Riwayat" -> 42.dp
                                        "Home" -> 26.dp
                                        "Backup" -> 42.dp
                                        else -> 22.dp
                                    }
                                ),
                                contentDescription = tab,
                                tint = if (isSelected) selectedColor else unselectedColor
                            )

                        }
                    },
                    label = {
                        Text(
                            text = tab,
                            color = if (isSelected) selectedColor else unselectedColor
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = selectedColor,
                        selectedTextColor = selectedColor,
                        unselectedIconColor = unselectedColor,
                        unselectedTextColor = unselectedColor,
                        indicatorColor = Color.Transparent // hilangkan indikator default
                    )
                )
            }
        }
    }
}
