package com.example.homework4.favourites

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.homework4.components.CountryItem
import com.example.homework4.list.CountriesListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    vm: CountriesListViewModel,
    onCountryClick: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (vm.favourites.isEmpty()) {
            Text(
                text = "Избранных стран пока нет",
                modifier = Modifier.padding(padding)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(vm.favourites) { country ->
                    CountryItem(
                        country = country,
                        isFavourite = true,
                        onClick = { onCountryClick(country.code) },
                        onFavourite = { vm.toggleFavourite(country) }
                    )
                }
            }
        }
    }
}