package com.example.smartparking.userinterface



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ParkingsScreen(
    viewModel: ParkingsViewModel,
    onLogout: () -> Unit,
    onParkingClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Parkings", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = onLogout) { Text("Déconnexion") }
        }

        if (viewModel.loading) CircularProgressIndicator()
        viewModel.error?.let { Text("Erreur : $it") }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.parkings) { parking ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onParkingClick(parking.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(parking.name, style = MaterialTheme.typography.titleMedium)
                        Text(parking.address)
                        Text("${parking.availableSpots} place(s) disponible(s)")
                    }
                }
            }
        }
    }
}