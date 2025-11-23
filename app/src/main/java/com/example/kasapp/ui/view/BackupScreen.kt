package com.example.kasapp.ui.view

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.R
import com.example.kasapp.ui.viewmodel.BackupViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BackupScreen(
    onBackClick: () -> Unit,
    viewModel: BackupViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Backup Data",
                        fontSize = 22.sp,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(45.dp)
                            .padding(4.dp)
                            .clickable { onBackClick() },
                        contentScale = ContentScale.Fit
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                windowInsets = WindowInsets(0.dp)
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7EEDB))
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(
                    bottomEnd = 30.dp,
                    bottomStart = 30.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Color.White),
//                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Backup database akan disimpan ke Google Drive akun yang Anda gunakan untuk login.",
                        color = Color(0xFF5C4A27),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),  // padding untuk bagian tombol
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(40.dp))

                BackupMenuButton(
                    iconRes = R.drawable.cloudupload,
                    text = "Backup Data Baru",
                    onClick = {
                        viewModel.backupToDrive { success, msg ->
                            Toast.makeText(
                                context,
                                if (success) "Backup berhasil" else "Backup gagal: $msg",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )

                Spacer(Modifier.height(20.dp))

                BackupMenuButton(
                    iconRes = R.drawable.download,
                    text = "Restore Dari Drive",
                    onClick = {
                        viewModel.restoreFromDrive { success, msg ->
                            Toast.makeText(
                                context,
                                if (success) "Restore berhasil" else "Restore gagal: $msg",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )

                Spacer(Modifier.height(80.dp))

                Icon(
                    painter = painterResource(id = R.drawable.cloudupload),
                    contentDescription = null,
                    modifier = Modifier.size(180.dp),
                    tint = Color(0xFF6B6B6B)
                )
            }
        }
    }
}


@Composable
private fun BackupMenuButton(
    iconRes: Int,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(vertical = 15.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(32.dp),
        )
        Spacer(Modifier.width(15.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
        )
    }
}
