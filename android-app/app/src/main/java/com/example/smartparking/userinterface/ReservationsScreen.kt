package com.example.smartparking.userinterface



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReservationsScreen(
    viewModel: ReservationsViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Mes réservations", style = MaterialTheme.typography.headlineSmall)

        if (viewModel.loading) CircularProgressIndicator()
        if (viewModel.message.isNotEmpty()) Text(viewModel.message)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.reservations) { r ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${r.parkingName} — place ${r.spotNumber}", style = MaterialTheme.typography.titleMedium)
                        Text("Du ${r.startTime.replace("T", " ")}")
                        Text("Au ${r.endTime.replace("T", " ")}")
                        Text("Statut : ${r.status}")
                        if (r.status == "ACTIVATE") {
                            TextButton(onClick = { viewModel.cancel(r.id) }) { Text("Annuler") }
                        }
                    }
                }
            }
        }
    }
}