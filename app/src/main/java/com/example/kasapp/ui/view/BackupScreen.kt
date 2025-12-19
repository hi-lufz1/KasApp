package com.example.kasapp.ui.view

import android.annotation.SuppressLint
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.R
import com.example.kasapp.ui.component.KasAppSnackbar
import com.example.kasapp.ui.viewmodel.BackupViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BackupScreen(
    onBackClick: () -> Unit,
    viewModel: BackupViewModel = viewModel(factory = ViewModelFactory.Factory)
) {

    var showForceRestoreDialog by remember { mutableStateOf(false) }
    var pendingRestoreMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()   // ✅ PENTING
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                KasAppSnackbar(snackbarData)
            }
        },
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

        Box(modifier = Modifier.fillMaxSize()) {

            // ===================== MAIN CONTENT =====================
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7EEDB))
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(
                        bottomStart = 30.dp,
                        bottomEnd = 30.dp
                    ),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
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
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(Modifier.height(40.dp))

                    // ================= BACKUP =================
                    BackupMenuButton(
                        iconRes = R.drawable.cloudupload,
                        text = "Backup Data Baru"
                    ) {
                        isLoading = true
                        viewModel.backupToDrive { success, msg ->
                            isLoading = false
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = if (success)
                                        "Backup berhasil"
                                    else
                                        "Backup gagal: $msg"
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // ================= RESTORE =================
                    BackupMenuButton(
                        iconRes = R.drawable.download,
                        text = "Restore Dari Drive"
                    ) {
                        isLoading = true
                        viewModel.restoreFromDrive { success, msg ->
                            isLoading = false

                            if (!success && msg == "LOCAL_NEWER") {
                                // 🔥 DATA LOKAL LEBIH BARU → TAMPILKAN DIALOG
                                pendingRestoreMessage = msg
                                showForceRestoreDialog = true
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (success)
                                            "Restore berhasil"
                                        else
                                            "Restore gagal: $msg"
                                    )
                                }
                            }
                        }
                    }


                    Spacer(Modifier.height(80.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.cloudupload),
                        contentDescription = null,
                        modifier = Modifier.size(180.dp),
                        tint = Color(0xFF6B6B6B)
                    )
                }
            }

            // ===================== LOADING OVERLAY =====================
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
                            text = "Memproses data...",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
        if (showForceRestoreDialog) {
            AlertDialog(
                onDismissRequest = {
                    showForceRestoreDialog = false
                },
                title = {
                    Text(
                        text = "Konfirmasi Restore",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = "Data lokal lebih baru dari backup di Drive.\n\n" +
                                "Apakah Anda yakin ingin menimpa data lokal?"
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showForceRestoreDialog = false
                            isLoading = true

                            viewModel.restoreFromDrive(force = true) { success, msg ->
                                isLoading = false
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = if (success)
                                            "Restore berhasil"
                                        else
                                            "Restore gagal: $msg"
                                    )
                                }
                            }
                        }
                    ) {
                        Text(
                            text = "Timpa",
                            color = Color(0xFFD32F2F)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showForceRestoreDialog = false
                        }
                    ) {
                        Text("Batal")
                    }
                }
            )
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
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(15.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color.Black)
        )
    }
}
