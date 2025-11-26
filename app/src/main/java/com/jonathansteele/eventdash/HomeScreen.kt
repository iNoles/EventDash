package com.jonathansteele.eventdash

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jonathansteele.eventdash.components.AddEventDialog
import com.jonathansteele.eventdash.components.EventCard
import com.jonathansteele.eventdash.data.Event
import com.jonathansteele.eventdash.data.Urgency
import com.jonathansteele.eventdash.ui.theme.EventDashTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val vm: EventViewModel = hiltViewModel()
    val uiState by vm.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onAddEvent = { name, date -> vm.addEvent(name, date) }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: EventViewModel.UiState,
    onAddEvent: (String, LocalDate) -> Unit = { _, _ -> }
) {
    val showAddDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Holiday & Countdown Dashboard") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog.value = true }) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
    ) { paddingValues ->

        if (uiState.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.events) { event ->
                    val cardHeight = when (event.urgency) {
                        Urgency.TODAY_OR_PASSED, Urgency.URGENT -> 180.dp
                        Urgency.SOON -> 150.dp
                        else -> 140.dp
                    }

                    EventCard(
                        event = event,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight)
                    )
                }
            }
        }
    }

    if (showAddDialog.value) {
        AddEventDialog(
            onAdd = { title, date ->
                onAddEvent(title, date)
                showAddDialog.value = false
            },
            onDismiss = { showAddDialog.value = false }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreviewLight() {
    EventDashTheme {
        val fakeUiState = EventViewModel.UiState(
            loading = false,
            events = listOf(
                Event(
                    id = 1,
                    title = "Christmas",
                    date = LocalDate.of(2025, 12, 25),
                    isHoliday = true,
                    emoji = "🎄"
                ),
                Event(
                    id = 2,
                    title = "New Year",
                    date = LocalDate.of(2026, 1, 1),
                    isHoliday = true,
                    emoji = "🎉"
                ),
                Event(
                    id = 3,
                    title = "Birthday",
                    date = LocalDate.of(2026, 1, 9),
                    isHoliday = false,
                    emoji = "🎂"
                ),
                Event(
                    id = 4,
                    title = "Project Deadline",
                    date = LocalDate.of(2025, 11, 30),
                    isHoliday = false,
                    emoji = "💻"
                )
            )
        )

        HomeScreenContent(uiState = fakeUiState)
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreviewDark() {
    EventDashTheme {
        val fakeUiState = EventViewModel.UiState(
            loading = false,
            events = listOf(
                Event(
                    id = 1,
                    title = "Christmas",
                    date = LocalDate.of(2025, 12, 25),
                    isHoliday = true,
                    emoji = "🎄"
                ),
                Event(
                    id = 2,
                    title = "New Year",
                    date = LocalDate.of(2026, 1, 1),
                    isHoliday = true,
                    emoji = "🎉"
                ),
                Event(
                    id = 3,
                    title = "Birthday",
                    date = LocalDate.of(2026, 1, 9),
                    isHoliday = false,
                    emoji = "🎂"
                ),
                Event(
                    id = 4,
                    title = "Project Deadline",
                    date = LocalDate.of(2025, 11, 30),
                    isHoliday = false,
                    emoji = "💻"
                )
            )
        )

        HomeScreenContent(uiState = fakeUiState)
    }
}
