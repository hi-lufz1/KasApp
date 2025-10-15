package com.example.kasapp.ui.view

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kasapp.ui.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val context = LocalContext.current
    val activity = context as Activity
    val account by viewModel.account.collectAsState()

    // Konfigurasi Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope("https://www.googleapis.com/auth/drive.file"))
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (account == null) {
            Button(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                }
            ) {
                Text("Login dengan Google")
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Selamat datang, ${account?.displayName ?: "Pengguna"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Email: ${account?.email ?: "-"}")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    googleSignInClient.signOut().addOnCompleteListener {
                        viewModel.logout()
                    }
                }) {
                    Text("Logout")
                }
            }
        }
    }
}
