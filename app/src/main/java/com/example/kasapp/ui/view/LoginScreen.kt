package com.example.kasapp.ui.view

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.kasapp.R
import com.example.kasapp.ui.viewmodel.LoginViewModel
import com.example.kasapp.ui.viewmodel.ViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kasapp.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController
) {
    val viewModel: LoginViewModel = viewModel(
        factory = ViewModelFactory.Factory
    )

    val context = LocalContext.current
    val activity = context as Activity
    val account by viewModel.account.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()


    // 🔹 Konfigurasi Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(
            Scope(DriveScopes.DRIVE_FILE),
            Scope(DriveScopes.DRIVE_APPDATA)
        )
        .build()

    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val accountResult = task.getResult(ApiException::class.java)
            viewModel.setAccount(accountResult)
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    val drivePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val acc = GoogleSignIn.getLastSignedInAccount(context)
            if (acc != null) {
                viewModel.autoRestore {
                    navController.navigate("home/${acc.displayName}/${acc.email}") {
                        popUpTo("login") { inclusive = true }
                    }
                    viewModel.scheduleBackupIfAllowed(acc)
                }
            }
        }
    }


    val driveScopes = arrayOf(
        Scope(DriveScopes.DRIVE_FILE),
        Scope(DriveScopes.DRIVE_APPDATA)
    )

    LaunchedEffect(Unit) {
        viewModel.checkSignedIn()
    }

    LaunchedEffect(account) {
        val acc = account ?: return@LaunchedEffect

        // 1️⃣ Jika izin Drive belum diberikan → tampilkan popup
        if (!GoogleSignIn.hasPermissions(acc, *driveScopes)) {
            val permissionIntent = GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestScopes(driveScopes[0], *driveScopes) // wajib semua scope
                    .setAccountName(acc.email.toString())
                    .build()
            ).signInIntent

            viewModel.setLoading(false)
            drivePermissionLauncher.launch(permissionIntent)
            return@LaunchedEffect // ⛔ stop supaya tidak lanjut ke nav / Worker
        }

        viewModel.autoRestore {
            (activity as MainActivity).recreate() // ⬅ paksa refresh DB + ViewModel

            navController.navigate("home/${acc.displayName}/${acc.email}") {
                popUpTo("login") { inclusive = true }
            }

            viewModel.scheduleBackupIfAllowed(acc)
        }

    }

    Scaffold {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            // 🔹 Bagian atas (krem dengan logo)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 40.dp,
                                bottomEnd = 40.dp
                            )
                        )
                        .background(Color(0xFFFFF1D2))
                ) {
                    Spacer(modifier = Modifier.padding(72.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo KasApp",
                        modifier = Modifier
                            .width(320.dp)
                            .height(320.dp)
                            .padding(start = 20.dp)
                    )
                }
                // 🔹 Card bawah (login)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.3f)
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Selamat Datang Di KasApp!",
                        fontSize = 16.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Button(
                        onClick = {
                            val signInIntent = googleSignInClient.signInIntent
                            launcher.launch(signInIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(50)
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google Icon",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Login Dengan Google",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
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
                        color = Color(0xFFFF6B00), // ✅ WARNA LOADING
                        strokeWidth = 4.dp        // (opsional) lebih tebal
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Menyiapkan akun...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
