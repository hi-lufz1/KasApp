package com.example.kasapp.ui.view

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * Halaman Profile Pengguna
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeProfileView(
    name: String?,
    email: String?,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onTentangKamiClick: () -> Unit = {},
    onKeluarAkunClick: () -> Unit = {}
) {
    // 1. Definisikan Font Sorts Mill Goudy di sini
    // Pastikan nama file di folder res/font adalah 'sorts_mill_goudy.ttf'
    val sortsMillGoudyFamily = FontFamily(
        Font(R.font.sorts_mill_goudy, FontWeight.Normal)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFB300),
                        Color(0xFFFFB300),
                        Color(0xFFFFF9EF)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar Manual
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                Text(
                    text = "Pengaturan",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Avatar Profile
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Icon",
                modifier = Modifier.size(130.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = name ?: "Pengguna",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = sortsMillGoudyFamily,
                    color = Color.White
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = email ?: "",
                        fontSize = 13.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Badge Pemilik
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(2.dp, Color(0xFFFFB300))
            ) {
                Text(
                    text = "Pemilik",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            // Card Content dengan Rounded Corner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9EF))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(34.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Menu Items di atas
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Menu Item: Tentang Kami
                        ProfileMenuItem(
                            iconRes = R.drawable.tentang_kami,
                            text = "Tentang Kami",
                            onClick = onTentangKamiClick,
                        )
                    }

                    // Menu Item: Keluar Akun di bawah
                    ProfileMenuItem(
                        iconRes = R.drawable.keluar,
                        text = "Keluar Akun",
                        onClick = onKeluarAkunClick,
                        isLogout = true
                    )
                }
            }
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    color = Color(0xFFFF6B00),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Keluar Akun...",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
    isLogout: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // BACKGROUND
        Box(
            modifier = Modifier
                .then(
                    if (isLogout) {
                        Modifier
                            .width(230.dp)
                            .align(Alignment.Center)
                    } else {
                        Modifier.fillMaxWidth()
                    }
                )
                .height(44.dp)
        ) {
            // KIRI
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Color(0xFFFFDC85)
                    )
            )

            // KANAN
            if (!isLogout) {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
                        .background(Color(0xFFFEBC2F))
                )
            } else {
                Box(
                    modifier = Modifier
                        .width(60.dp)
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp))
                        .background(Color(0xFFFFB300))
                )
            }
        }

        // BORDER (HANYA JIKA BUKAN LOGOUT)
        if (!isLogout) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                drawRoundRect(
                    color = Color(0xFFFFB300),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx()),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }
        }

        // CONTENT
        Row(
            modifier = Modifier
                .then(
                    if (isLogout) {
                        Modifier
                            .width(230.dp)
                            .align(Alignment.Center)
                    } else {
                        Modifier.fillMaxWidth()
                    }
                )
                .height(44.dp)
                .padding(start = if (isLogout) 0.dp else 20.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isLogout) Arrangement.Center else Arrangement.SpaceBetween
        ) {
            if (isLogout) {
                Text(
                    text = text,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Box(
                    modifier = Modifier.size(44.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.keluar),
                        contentDescription = "Keluar",
                        modifier = Modifier.size(38.dp)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = text,
                        modifier = Modifier.size(46.dp)
                    )

                    Text(
                        text = text,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                // Divider
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawLine(
                            color = Color(0xFFFFB300),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }

                // Arrow
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .offset(x = (-7).dp),
                    shape = CircleShape,
                    color = Color.White
                )
                {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.right),
                            contentDescription = "Arrow",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }
            }
        }
    }
}