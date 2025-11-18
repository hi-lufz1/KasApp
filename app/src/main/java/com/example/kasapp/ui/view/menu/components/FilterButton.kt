package com.example.kasapp.ui.view.menu.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterButton(
    text: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val darkText = Color(0xFF4A2800)
    val whiteText = Color.White
    val selectedBg = Color(0xFFFFE0B2)
    val unselectedBg = Color.White
    val borderColor = Color(0xFFFF9800)
    val selectedBorderColor = Color(0xFFFF9800) // tetap sama agar border tidak hilang
    val badgeAreaBg = Color(0xFFFFB300)
    val badgeCircleBg = Color(0xFFFE8235)

    Surface(
        onClick = onClick,
        modifier = Modifier.height(30.dp),
        shape = RoundedCornerShape(50),
        color = if (selected) selectedBg else unselectedBg,
        border = BorderStroke(
            1.dp,
            if (selected) selectedBorderColor else borderColor
        ),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = darkText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 22.dp, end = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .background(badgeAreaBg)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(RoundedCornerShape(50))
                        .background(badgeCircleBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        color = whiteText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}