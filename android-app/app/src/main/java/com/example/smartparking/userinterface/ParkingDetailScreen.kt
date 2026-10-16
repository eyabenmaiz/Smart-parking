package com.example.smartparking.userinterface



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ParkingDetailScreen(
    parkingId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ParkingDetailViewModel = viewModel()
) {
    LaunchedEffect(parkingId) { viewModel.load(parkingId) }

    var selectedSpotId by remember { mutableStateOf<Long?>(null) }
    var start by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("2") }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { TextButton(onClick = onBack) { Text("← Retour") } }

        item {
            Column {
                viewModel.parking?.let { parking ->
                    Text(parking.name, style = MaterialTheme.typography.headlineSmall)
                    Text(parking.address)
                }
            }
        }

        item { Text("Places", style = MaterialTheme.typography.titleMedium) }

        items(viewModel.spots) { spot ->
            val selected = spot.id == selectedSpotId
            Card(
                modifier = Modifier.fillMaxWidth().clickable { selectedSpotId = spot.id },
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Place ${spot.number}", style = MaterialTheme.typography.titleSmall)
                    Text(if (spot.available) "Libre en ce moment" else "Occupée en ce moment")
                }
            }
        }

        item { Text("Réserver", style = MaterialTheme.typography.titleMedium) }

        item {
            OutlinedTextField(
                value = start,
                onValueChange = { start = it },
                label = { Text("Début (ex. 2027-01-20 10:00)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it },
                label = { Text("Durée en heures") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Button(
                onClick = { viewModel.reserve(parkingId, selectedSpotId, start, duration) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Réserver") }
        }

        item { Text(viewModel.message) }
    }
}