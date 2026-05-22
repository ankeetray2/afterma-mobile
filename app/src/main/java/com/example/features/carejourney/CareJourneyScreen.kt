package com.example.features.carejourney

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.entities.MoodEntry
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CareJourneyScreen(
    viewModel: MainViewModel,
    onNavigateToSection: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val logs by viewModel.moodEntries.collectAsState()

    // Interactive Log Form State
    var selectedMood by remember { mutableIntStateOf(4) } // Okay, Good, Excellent
    var energyScore by remember { mutableFloatStateOf(4.0f) }
    var recoveryPercent by remember { mutableFloatStateOf(70.0f) }
    var noteText by remember { mutableStateOf("") }

    val symptomOptions = listOf(
        "Fatigue",
        "Abdominal Cramping",
        "Lactation Tightness",
        "Lower Back Soreness",
        "Emotional Fluctuations",
        "Anxiety Nodes"
    )
    val selectedSymptoms = remember { mutableStateListOf<String>() }

    var isAddingLog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        SoftTopBar(
            title = "Healing Timeline",
            actions = {
                // Toggle "Add Entry" versus "History List"
                TextButton(
                    onClick = { isAddingLog = !isAddingLog },
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryPink)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isAddingLog) Icons.Filled.List else Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAddingLog) "View History" else "Log Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        )

        // Unified AFTERMA Top Navigation Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Journey" to {},
                "Period Log" to { onNavigateToSection("cycle_tracker") },
                "Lactation Log" to { onNavigateToSection("lactation_tracker") },
                "Health Summary" to {}
            ).forEach { (tab, onClick) ->
                val isActive = tab == "Journey"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isActive) Color(0xFF0D1730) else Color.White)
                        .border(
                            width = 1.dp,
                            color = if (isActive) Color(0xFF0D1730) else Color(0xFFE9EAF0),
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable { onClick() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isActive) Color.White else Color(0xFF7B8496),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        if (isAddingLog) {
            // Check-in journal form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 100.dp) // breathing room for navbar
            ) {
                Text(
                    text = "Daily Maternal Check-in",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Record your emotional state, cellular healing progression, and active symptoms.",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // 1. Selector for Mood Score (Interactive MoodCard custom components)
                Text(
                    text = "Select Maternal Temperament",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val moodDetails = listOf(
                        Triple(1, "Awful", "😔"),
                        Triple(2, "Hard", "🥺"),
                        Triple(3, "Okay", "😐"),
                        Triple(4, "Good", "😌"),
                        Triple(5, "Radiant", "🌸")
                    )
                    moodDetails.forEach { (score, label, emoji) ->
                        MoodCard(
                            score = score,
                            label = label,
                            emoji = emoji,
                            isSelected = selectedMood == score,
                            onClick = { selectedMood = score },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 2. Transverse Sliders (Energy & Core Healing)
                Text(
                    text = "Lactation & Energy Coefficient: ${energyScore.toInt()}/5",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Slider(
                    value = energyScore,
                    onValueChange = { energyScore = it },
                    valueRange = 1f..5f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryPink,
                        activeTrackColor = GradientPink,
                        inactiveTrackColor = Border
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Physical Postpartum Healing Factor: ${recoveryPercent.toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Slider(
                    value = recoveryPercent,
                    onValueChange = { recoveryPercent = it },
                    valueRange = 10f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = DeepPink,
                        activeTrackColor = PrimaryPink,
                        inactiveTrackColor = Border
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // 3. Postpartum Clinical symptoms checkboxes
                Text(
                    text = "Observe Present Symptoms",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    symptomOptions.forEach { symptom ->
                        val isChecked = selectedSymptoms.contains(symptom)
                        Box(
                            modifier = Modifier
                                .shadow(elevation = if (isChecked) 1.dp else 0.dp, shape = CircleShape)
                                .clip(CircleShape)
                                .background(if (isChecked) SoftPink else Color.White)
                                .border(
                                    width = 1.dp,
                                    color = if (isChecked) DeepPink else Border,
                                    shape = CircleShape
                                )
                                .clickable {
                                    if (isChecked) selectedSymptoms.remove(symptom)
                                    else selectedSymptoms.add(symptom)
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isChecked) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = DeepPink,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = symptom,
                                    fontSize = 12.sp,
                                    color = if (isChecked) DeepPink else SecondaryText,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // 4. Personal note
                AftermaInput(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = "Describe physical sensations, emotional milestones, or baby sleep bonds today...",
                    label = "Supportive Journal Note",
                    leadingIcon = Icons.Filled.EditNote,
                    textTag = "maternal_note_input",
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // 5. Submit Log
                AftermaButton(
                    text = "Commit to Healing Timeline",
                    onClick = {
                        viewModel.logDailyStatus(
                            moodScore = selectedMood,
                            energyLevel = energyScore.toInt(),
                            recoveryProgress = recoveryPercent.toInt(),
                            note = noteText,
                            symptoms = selectedSymptoms.toList()
                        )
                        // reset form & close
                        noteText = ""
                        selectedSymptoms.clear()
                        isAddingLog = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "submit_daily_log"
                )
            }
        } else {
            // Prior logs vertical scroll timeline representation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Historical Healing Diary",
                            style = MaterialTheme.typography.headlineMedium,
                            color = PrimaryText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Your postpartum entries compiled into a serene therapeutic map.",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (logs.isEmpty()) {
                    // Comfort empty state (As required by Frontend design guidelines)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(bottom = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(SoftPink),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Eco,
                                    contentDescription = null,
                                    tint = PrimaryPink,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Your Healing Chart is Quiet",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap 'Log Status' at the top right to write down your first entry.",
                                fontSize = 12.sp,
                                color = SecondaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 100.dp), // Clear bottom floating tab navigation
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(logs) { entry ->
                            TimelineCard(entry = entry)
                        }
                    }
                }
            }
        }
    }
}

// Simple flowing Timeline wrapper element
@Composable
fun TimelineCard(entry: MoodEntry) {
    val dateString = remember(entry.timestamp) {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
        dateFormat.format(Date(entry.timestamp))
    }

    val emoji = when (entry.moodScore) {
        1 -> "😔"
        2 -> "🥺"
        3 -> "😐"
        4 -> "😌"
        else -> "🌸"
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        // Timeline dot connectors indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(SoftPink)
                    .border(2.dp, PrimaryPink, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 16.sp)
            }
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(90.dp)
                    .background(Border)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Diary Card info
        AftermaCard(
            modifier = Modifier
                .weight(1f)
                .testTag("timeline_diary_entry")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateString,
                        fontSize = 11.sp,
                        color = MutedText,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Recovery ${entry.recoveryProgress}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepPink,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SoftPink)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (entry.emotionalNote.isNotBlank()) {
                    Text(
                        text = "\"${entry.emotionalNote}\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PrimaryText,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (entry.symptoms.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Observed: ${entry.symptoms}",
                        fontSize = 11.sp,
                        color = SecondaryText,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.FlashOn,
                        contentDescription = null,
                        tint = Warning,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Energy Index: ${entry.energyLevel}/5",
                        fontSize = 11.sp,
                        color = MutedText
                    )
                }
            }
        }
    }
}

// Simple Custom FlowRow for wrapping symptoms neatly
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val layoutWidth = constraints.maxWidth

        var rowHeight = 0
        var totalHeight = 0
        var currentWidth = 0

        val rowData = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()

        placeables.forEach { placeable ->
            val spacing = mainAxisSpacing.roundToPx()
            if (currentWidth + placeable.width + spacing > layoutWidth && currentRow.isNotEmpty()) {
                rowData.add(currentRow)
                totalHeight += rowHeight + crossAxisSpacing.roundToPx()
                currentRow = mutableListOf()
                currentWidth = 0
                rowHeight = 0
            }
            currentRow.add(placeable)
            currentWidth += placeable.width + spacing
            rowHeight = maxOf(rowHeight, placeable.height)
        }
        if (currentRow.isNotEmpty()) {
            rowData.add(currentRow)
            totalHeight += rowHeight
        }

        layout(layoutWidth, totalHeight) {
            var yPosition = 0
            rowData.forEach { row ->
                var xPosition = 0
                row.forEach { placeable ->
                    placeable.placeRelative(xPosition, yPosition)
                    xPosition += placeable.width + mainAxisSpacing.roundToPx()
                }
                yPosition += row.maxOf { it.height } + crossAxisSpacing.roundToPx()
            }
        }
    }
}
