package com.example.kasapp.ui.view.profile


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kasapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TentangKamiView(
    onBackClick: () -> Unit
) {
    // Font Adamina Regular
    val adaminaFamily = FontFamily(
        Font(R.font.adamina_regular, FontWeight.Normal)
    )

    val BackgroundCream = Color(0xFFFFE0A1)
    val TopBarYellow = Color(0xFFFFE0A1)
    val TextColorBrown = Color(0xFF744A00)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tentang Kami",
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
                    containerColor = Color(0xFFFFE0A1)
                )
            )
        },
        containerColor = BackgroundCream
    ) { paddingValues ->
        // Kolom Utama (Scrollable) - Tanpa Padding Horizontal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            // --- WRAPPER TEKS (Diberi Padding Kanan-Kiri) ---
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.awalterbentuk),
                    contentDescription = "Foto Kami",
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Kass App lahir dari project Capstone mahasiswa Program Studi Teknologi Informasi, Universitas Muhammadiyah Yogyakarta.",
                    fontFamily = adaminaFamily,
                    fontSize = 16.sp,
                    color = TextColorBrown,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextColorBrown)) {
                            append("Aplikasi ini dikembangkan melalui kerja sama dengan ")
                        }

                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF744A00), // Merah
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Warmindo")
                        }

                        append(" ")

                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF744A00), // Kuning/Gold
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Raja")
                        }

                        append(" ")

                        withStyle(
                            style = SpanStyle(
                                color = Color(0xFF744A00), // Hijau
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append("Vitamin 3")
                        }

                        withStyle(style = SpanStyle(color = TextColorBrown)) {
                            append(", berawal dari kebutuhan nyata pemilik usaha dalam mengelola transaksi tunai dan QRIS yang selama ini masih terpisah dan harus dihitung manual.")
                        }
                    },
                    fontFamily = adaminaFamily,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )


                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Melalui Kass App, pencatatan keuangan menjadi lebih sederhana dan terintegrasi — mulai dari transaksi harian, mingguan, bulanan, hingga riwayat dan pencadangan data. Tujuannya satu: membantu pemilik usaha memantau keuangan dengan mudah, cepat, dan rapi hanya melalui satu aplikasi.",
                    fontFamily = adaminaFamily,
                    fontSize = 16.sp,
                    color = TextColorBrown,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )

                // Jarak antara teks terakhir dan gambar
                Spacer(modifier = Modifier.height(32.dp))
            }
            Image(
                painter = painterResource(id = R.drawable.kaminew),
                contentDescription = "Foto Kami",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            // --- GAMBAR FULL (Di luar wrapper teks) ---
            Image(
                painter = painterResource(id = R.drawable.tim),
                contentDescription = "Foto Kami",
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}