package com.example.homework4

import com.example.homework4.detail.CountryDetailUiState
import com.example.homework4.fakes.FakeCountriesRepository
import com.example.homework4.list.CountriesListViewModel
import com.example.homework4.list.CountriesUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CountriesListViewModelTest {

    @Test
    fun load_success_emitsLoadingThenSuccessSequence() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha)
            }
            val vm = CountriesListViewModel(fake)
            assertTrue(vm.state.value is CountriesUiState.Loading)
            advanceUntilIdle()
            val success = vm.state.value as CountriesUiState.Success
            assertEquals(listOf(TestCountries.alpha), success.list)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun load_failure_setsErrorState() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = FakeCountriesRepository().apply {
                loadAllError = RuntimeException("network")
            }
            val vm = CountriesListViewModel(fake)
            advanceUntilIdle()
            val err = vm.state.value as CountriesUiState.Error
            assertTrue(err.message.contains("network"))
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun search_noMatches_yieldsEmpty_notSuccessWithEmptyList() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha)
            }
            val vm = CountriesListViewModel(fake)
            advanceUntilIdle()
            vm.search("zzz")
            advanceTimeBy(400)
            advanceUntilIdle()
            assertTrue(vm.state.value is CountriesUiState.Empty)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun loadCountryByCode_opensDetailForRequestedId() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta)
                getCountryByCodeHandler = { code ->
                    loadAllData.find { it.code == code }
                }
            }
            val vm = CountriesListViewModel(fake)
            advanceUntilIdle()
            vm.loadCountryByCode("BE")
            advanceUntilIdle()
            val detail = vm.detailState.value as CountryDetailUiState.Success
            assertEquals("BE", detail.country.code)
            assertEquals(TestCountries.beta.name, detail.country.name)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun refresh_afterLoadError_triggersNewLoadAndSuccess() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = FakeCountriesRepository().apply {
                loadAllError = RuntimeException("fail")
            }
            val vm = CountriesListViewModel(fake)
            advanceUntilIdle()
            assertTrue(vm.state.value is CountriesUiState.Error)

            fake.loadAllError = null
            fake.loadAllData = listOf(TestCountries.alpha)
            vm.refresh()
            advanceUntilIdle()
            assertTrue(vm.state.value is CountriesUiState.Success)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun search_secondQueryAfterDebounce_onlyLastQueryCallsRepository() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha, TestCountries.beta)
            }
            val vm = CountriesListViewModel(fake)
            advanceUntilIdle()
            fake.searchCalls.clear()
            vm.search("a")
            vm.search("al")
            advanceTimeBy(400)
            advanceUntilIdle()
            assertEquals(listOf("al"), fake.searchCalls)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun loadCountryByCode_retryAfterError_eventuallyLoadsCountry() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            var failOnce = true
            val fake = FakeCountriesRepository().apply {
                loadAllData = listOf(TestCountries.alpha)
                getCountryByCodeHandler = { code ->
                    if (failOnce) throw RuntimeException("io")
                    TestCountries.alpha.takeIf { it.code == code }
                }
            }
            val vm = CountriesListViewModel(fake)
            advanceUntilIdle()
            vm.loadCountryByCode("AL")
            advanceUntilIdle()
            assertTrue(vm.detailState.value is CountryDetailUiState.Error)
            failOnce = false
            vm.loadCountryByCode("AL")
            advanceUntilIdle()
            assertTrue(vm.detailState.value is CountryDetailUiState.Success)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
