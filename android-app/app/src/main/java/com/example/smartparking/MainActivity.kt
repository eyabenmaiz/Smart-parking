package com.example.smartparking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.smartparking.data.ApiClient
import com.example.smartparking.data.TokenStore
import com.example.smartparking.userinterface.LoginViewModel
import com.example.smartparking.userinterface.ParkingDetailScreen
import com.example.smartparking.userinterface.ParkingsScreen
import com.example.smartparking.userinterface.ParkingsViewModel
import com.example.smartparking.userinterface.ReservationsScreen
import com.example.smartparking.userinterface.ReservationsViewModel
import com.example.smartparking.ui.theme.SmartParkingTheme
import androidx.compose.material3.TextButton
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val parkingsViewModel: ParkingsViewModel by viewModels()
    private val reservationsViewModel: ReservationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ApiClient.tokenProvider = { TokenStore(applicationContext).get() }
        ApiClient.onUnauthorized = { runOnUiThread { loginViewModel.logout() } }

        setContent {
            SmartParkingTheme {
                if (loginViewModel.loggedIn) {
                    val navController = rememberNavController()
                    var tab by remember { mutableStateOf("parkings") }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = tab == "parkings",
                                    onClick = {
                                        tab = "parkings"
                                        navController.navigate("parkings") {
                                            popUpTo("parkings") { inclusive = true }
                                        }
                                    },
                                    icon = {},
                                    label = { Text("Parkings") }
                                )
                                NavigationBarItem(
                                    selected = tab == "reservations",
                                    onClick = {
                                        tab = "reservations"
                                        navController.navigate("reservations")
                                    },
                                    icon = {},
                                    label = { Text("Mes réservations") }
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "parkings"
                        ) {
                            composable("parkings") {
                                ParkingsScreen(
                                    viewModel = parkingsViewModel,
                                    onLogout = { loginViewModel.logout() },
                                    onParkingClick = { id -> navController.navigate("parking/$id") },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            composable(
                                route = "parking/{id}",
                                arguments = listOf(navArgument("id") { type = NavType.LongType })
                            ) { entry ->
                                val id = entry.arguments?.getLong("id") ?: 0L
                                ParkingDetailScreen(
                                    parkingId = id,
                                    onBack = { navController.popBackStack() },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                            composable("reservations") {
                                ReservationsScreen(
                                    viewModel = reservationsViewModel,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                } else {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        LoginScreen(
                            viewModel = loginViewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel, modifier: Modifier = Modifier) {
    var registerMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text(if (registerMode) "Créer un compte" else "SmartParking")

        if (registerMode) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (registerMode) "Mot de passe (8 caractères min.)" else "Mot de passe") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (registerMode) viewModel.register(name, email, password)
                else viewModel.login(email, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (registerMode) "S'inscrire" else "Se connecter") }

        TextButton(
            onClick = {
                registerMode = !registerMode
                viewModel.clearMessage()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (registerMode) "J'ai déjà un compte" else "Pas de compte ? S'inscrire")
        }

        Text(viewModel.message)
    }
}
