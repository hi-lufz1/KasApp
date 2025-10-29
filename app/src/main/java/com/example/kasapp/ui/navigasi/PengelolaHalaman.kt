package com.example.kasapp.ui.navigasi

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.kasapp.ui.view.HomeScreen
import com.example.kasapp.ui.view.LoginScreen
import com.example.kasapp.ui.viewmodel.LoginViewModel

@Composable
fun PengelolaHalaman(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(viewModel = loginViewModel, navController = navController)
        }

        // 🔹 Tambahkan argumen name & email
        composable(
            route = "home/{name}/{email}",
            arguments = listOf(
                navArgument("name") { defaultValue = "Pengguna" },
                navArgument("email") { defaultValue = "-" }
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name")
            val email = backStackEntry.arguments?.getString("email")

            HomeScreen(name = name, email = email)
        }
    }
}
