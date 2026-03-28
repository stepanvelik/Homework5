package com.example.homework4.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.homework4.data.model.Country
import com.example.homework4.list.CountriesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(

    code: String,
    vm: CountriesListViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(code) {
        if (code.isNotBlank()) {
            vm.loadCountryByCode(code)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали страны") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        when (val state = vm.detailState.value) {
            CountryDetailUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            CountryDetailUiState.Empty -> {
                Text(text = "Страна не найдена", modifier = Modifier.padding(padding))
            }

            is CountryDetailUiState.Error -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(state.message)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { vm.loadCountryByCode(code) }) {
                        Text("Retry")
                    }
                }
            }

            is CountryDetailUiState.Success -> {
                val country: Country = state.country
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {

                    Image(
                        painter = rememberAsyncImagePainter(country.flagUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = country.name,
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Регион: ${country.region}")
                            Text("Столица: ${country.capital}")
                            Text("Население: ${country.population}")
                        }
                    }
                }
            }
        }
    }
}
