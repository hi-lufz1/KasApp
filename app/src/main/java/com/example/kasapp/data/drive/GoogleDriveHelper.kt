package com.example.kasapp.data.drive

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes

object GoogleDriveHelper {

    fun getSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(
                Scope(DriveScopes.DRIVE_FILE),
                Scope(DriveScopes.DRIVE_APPDATA)
            )
            .build()
    }

    fun getDriveService(context: Context): Drive {
        val account = GoogleSignIn.getLastSignedInAccount(context)
            ?: throw Exception("User belum login Google")

        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(
                DriveScopes.DRIVE_FILE,
                DriveScopes.DRIVE_APPDATA
            )
        )

        credential.selectedAccount = account.account

        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("KasApp Backup")
            .build()
    }

}
