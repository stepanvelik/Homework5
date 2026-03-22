package com.example.homework4.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.homework4.components.CountryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesListScreen(
    onCountryClick: (String) -> Unit,
    onFavouritesClick: () -> Unit,
    vm: CountriesListViewModel
) {

    var query by remember { mutableStateOf("") }

    Column {

        TopAppBar(
            title = { Text("Countries") },
            actions = {

                IconButton(onClick = { vm.refresh() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }

                IconButton(onClick = onFavouritesClick) {
                    Icon(Icons.Default.Favorite, contentDescription = "Favourites")
                }

            }
        )

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (it.isBlank()) {
                    vm.load()
                } else {
                    vm.search(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            placeholder = { Text("Поиск страны") }
        )

        when (val state = vm.state.value) {

            CountriesUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }

            CountriesUiState.Empty -> {
                Text(
                    "Ничего не найдено",
                    modifier = Modifier.padding(16.dp)
                )
            }

            is CountriesUiState.Error -> {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(state.message)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { vm.refresh() }) {
                        Text("Retry")
                    }
                }
            }

            is CountriesUiState.Success -> {

                LazyColumn {

                    items(state.list) { country ->

                        CountryItem(
                            country = country,
                            isFavourite = vm.favourites.any { it.code == country.code },
                            onClick = { onCountryClick(country.code) },
                            onFavourite = { vm.toggleFavourite(country) }
                        )

                    }

                }

            }
        }
    }
}