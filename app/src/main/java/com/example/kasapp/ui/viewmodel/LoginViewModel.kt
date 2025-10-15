package com.example.kasapp.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _account = MutableStateFlow<GoogleSignInAccount?>(null)
    val account = _account.asStateFlow()

    fun checkSignedIn() {
        val account = GoogleSignIn.getLastSignedInAccount(getApplication())
        _account.value = account
    }

    fun setAccount(account: GoogleSignInAccount?) {
        _account.value = account
    }

    fun logout() {
        _account.value = null
    }
}
