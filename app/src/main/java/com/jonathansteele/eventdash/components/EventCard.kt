package com.jonathansteele.eventdash.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jonathansteele.eventdash.data.Event
import com.jonathansteele.eventdash.data.Urgency

@Composable
fun EventCard(event: Event, modifier: Modifier = Modifier) {
    // Expressive colors based on urgency
    val containerColor = when (event.urgency) {
        Urgency.TODAY_OR_PASSED, Urgency.URGENT -> MaterialTheme.colorScheme.errorContainer
        Urgency.SOON -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when (event.urgency) {
        Urgency.TODAY_OR_PASSED, Urgency.URGENT -> MaterialTheme.colorScheme.onErrorContainer
        Urgency.SOON -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(containerColor)
                .padding(12.dp)
        ) {
            // Title with emoji
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = event.emoji ?: "📅",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            Text(
                text = event.date.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Countdown badge
            Surface(
                color = contentColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "${event.daysLeft} days left",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
