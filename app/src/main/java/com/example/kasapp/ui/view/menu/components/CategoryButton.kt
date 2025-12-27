package com.example.kasapp.ui.view.menu.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryButton(
    modifier: Modifier = Modifier, // ✅ PENTING
    text: String,
    iconRes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconBgColor = Color(0xFFFFB300)
    val textColor = Color(0xFF4A2800)
    val borderColor = Color(0xFFFF9800)
    val textBgColor = if (selected) Color(0xFFFFE0B2) else Color.White

    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp), // tinggi tetap, lebar dari luar
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(), // 🔥 wajib agar weight bekerja
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ICON
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(iconBgColor)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = text,
                    tint = textColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            // TEXT
            Box(
                modifier = Modifier
                    .weight(1f) // 🔥 bikin teks fleksibel
                    .fillMaxHeight()
                    .background(textBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = textColor,
                    maxLines = 1
                )
            }
        }
    }
}
