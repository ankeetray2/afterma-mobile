package com.example.features.cycle

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CycleEntry
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun CycleTrackerScreen(
    viewModel: MainViewModel,
    onNavigateToSection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val cycleLogs by viewModel.cycleEntries.collectAsState()
    val scope = rememberCoroutineScope()

    // Screen State variables for the active logged day
    var selectedDate by remember { mutableStateOf("22-May-2026") }
    var selectedMood by remember { mutableStateOf("Balanced") }
    var selectedFlow by remember { mutableStateOf("Medium") }
    var selectedSleep by remember { mutableStateOf("7-9 HRS") }
    
    var isOvulationActive by remember { mutableStateOf(false) }
    var isMedicationsTaken by remember { mutableStateOf(true) }

    var painIntensity by remember { mutableFloatStateOf(3f) }
    var energyVitality by remember { mutableFloatStateOf(4f) }
    var crampsSeverity by remember { mutableFloatStateOf(2f) }

    var selectedWater by remember { mutableStateOf("2L") }
    val selectedSymptoms = remember { mutableStateListOf("Fatigue", "Cramps") }
    var notesText by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }

    // Whenever selected Date changes, load existing records if any
    LaunchedEffect(selectedDate, cycleLogs) {
        val entry = cycleLogs.find { it.date == selectedDate }
        if (entry != null) {
            selectedMood = entry.mood
            selectedFlow = entry.flow
            selectedSleep = entry.sleep
            isOvulationActive = entry.ovulation
            isMedicationsTaken = entry.medication
            painIntensity = entry.painIntensity.toFloat()
            energyVitality = entry.energyVitality.toFloat()
            crampsSeverity = entry.crampsSeverity.toFloat()
            selectedWater = entry.waterIntake
            selectedSymptoms.clear()
            selectedSymptoms.addAll(entry.symptoms)
            notesText = entry.notes
        } else {
            // reset to natural defaults for clean logging of unregistered days
            selectedMood = "Balanced"
            selectedFlow = "None"
            selectedSleep = "7-9 HRS"
            isOvulationActive = false
            isMedicationsTaken = false
            painIntensity = 0f
            energyVitality = 5f
            crampsSeverity = 0f
            selectedWater = "1L"
            selectedSymptoms.clear()
            notesText = ""
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        val isTablet = maxWidth > 680.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant Soft Top bar
            SoftTopBar(
                title = "Cycle Sentinel",
                actions = {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SoftPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = null,
                            tint = PrimaryPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            )

            // Inner body
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Main inputs container. If tablet side-by-side, otherwise stack.
                if (isTablet) {
                    // Left Column: Entry form
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 110.dp) // space for bottom floating navbar
                    ) {
                        CycleHeaderArea()
                        CycleTopTabs(onNavigate = onNavigateToSection)
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Entry components
                        CycleInputsArea(
                            selectedDate = selectedDate,
                            selectedMood = selectedMood,
                            onMoodSelected = { selectedMood = it },
                            selectedFlow = selectedFlow,
                            onFlowSelected = { selectedFlow = it },
                            selectedSleep = selectedSleep,
                            onSleepSelected = { selectedSleep = it },
                            isOvulationActive = isOvulationActive,
                            onOvulationToggled = { isOvulationActive = it },
                            isMedicationsTaken = isMedicationsTaken,
                            onMedicationsToggled = { isMedicationsTaken = it },
                            painIntensity = painIntensity,
                            onPainChanged = { painIntensity = it },
                            energyVitality = energyVitality,
                            onEnergyChanged = { energyVitality = it },
                            crampsSeverity = crampsSeverity,
                            onCrampsChanged = { crampsSeverity = it },
                            selectedWater = selectedWater,
                            onWaterSelected = { selectedWater = it },
                            selectedSymptoms = selectedSymptoms,
                            notesText = notesText,
                            onNotesChanged = { notesText = it },
                            onCommit = {
                                val entry = CycleEntry(
                                    date = selectedDate,
                                    mood = selectedMood,
                                    flow = selectedFlow,
                                    sleep = selectedSleep,
                                    ovulation = isOvulationActive,
                                    medication = isMedicationsTaken,
                                    painIntensity = painIntensity.toInt(),
                                    energyVitality = energyVitality.toInt(),
                                    crampsSeverity = crampsSeverity.toInt(),
                                    waterIntake = selectedWater,
                                    symptoms = selectedSymptoms.toList(),
                                    notes = notesText
                                )
                                viewModel.logCycleEntry(entry)
                                showSuccessDialog = true
                            }
                        )
                    }

                    // Right Column: Calendar & Insights Sidebar Panel
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 110.dp)
                    ) {
                        CalendarSidePanel(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it },
                            cycleLogs = cycleLogs
                        )
                    }
                } else {
                    // Mobile: single stacked vertical scroll
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 120.dp) // clean space above FloatingBottomNav
                    ) {
                        CycleHeaderArea()
                        CycleTopTabs(onNavigate = onNavigateToSection)
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // Render Calendar Card at top of mobile form to give direct clinical entry context!
                        CalendarSidePanel(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it },
                            cycleLogs = cycleLogs
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        CycleInputsArea(
                            selectedDate = selectedDate,
                            selectedMood = selectedMood,
                            onMoodSelected = { selectedMood = it },
                            selectedFlow = selectedFlow,
                            onFlowSelected = { selectedFlow = it },
                            selectedSleep = selectedSleep,
                            onSleepSelected = { selectedSleep = it },
                            isOvulationActive = isOvulationActive,
                            onOvulationToggled = { isOvulationActive = it },
                            isMedicationsTaken = isMedicationsTaken,
                            onMedicationsToggled = { isMedicationsTaken = it },
                            painIntensity = painIntensity,
                            onPainChanged = { painIntensity = it },
                            energyVitality = energyVitality,
                            onEnergyChanged = { energyVitality = it },
                            crampsSeverity = crampsSeverity,
                            onCrampsChanged = { crampsSeverity = it },
                            selectedWater = selectedWater,
                            onWaterSelected = { selectedWater = it },
                            selectedSymptoms = selectedSymptoms,
                            notesText = notesText,
                            onNotesChanged = { notesText = it },
                            onCommit = {
                                val entry = CycleEntry(
                                    date = selectedDate,
                                    mood = selectedMood,
                                    flow = selectedFlow,
                                    sleep = selectedSleep,
                                    ovulation = isOvulationActive,
                                    medication = isMedicationsTaken,
                                    painIntensity = painIntensity.toInt(),
                                    energyVitality = energyVitality.toInt(),
                                    crampsSeverity = crampsSeverity.toInt(),
                                    waterIntake = selectedWater,
                                    symptoms = selectedSymptoms.toList(),
                                    notes = notesText
                                )
                                viewModel.logCycleEntry(entry)
                                showSuccessDialog = true
                            }
                        )
                    }
                }
            }
        }

        // Commited Log Success Modal Alert
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = Color.White,
                tonalElevation = 2.dp,
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Success.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "Success",
                                tint = Success,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Cycle Log Registered",
                            color = PrimaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Text(
                        text = "Your daily observations have been recorded with clinical accuracy. This state will populate your medical insights dynamically.",
                        color = SecondaryText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showSuccessDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D1730)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Acknowledge", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun CycleHeaderArea() {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = "Log Your Cycle",
            style = MaterialTheme.typography.headlineMedium,
            color = PrimaryText,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.5).sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Record your daily observations for precise clinical tracking.",
            fontSize = 13.sp,
            color = SecondaryText,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun CycleTopTabs(
    onNavigate: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "Journey" to { onNavigate("care_journey") },
            "Period Log" to {},
            "Lactation Log" to { onNavigate("lactation_tracker") },
            "Health Summary" to {}
        ).forEach { (tab, onClick) ->
            val isActive = tab == "Period Log"
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
}

@Composable
fun CycleInputsArea(
    selectedDate: String,
    selectedMood: String,
    onMoodSelected: (String) -> Unit,
    selectedFlow: String,
    onFlowSelected: (String) -> Unit,
    selectedSleep: String,
    onSleepSelected: (String) -> Unit,
    isOvulationActive: Boolean,
    onOvulationToggled: (Boolean) -> Unit,
    isMedicationsTaken: Boolean,
    onMedicationsToggled: (Boolean) -> Unit,
    painIntensity: Float,
    onPainChanged: (Float) -> Unit,
    energyVitality: Float,
    onEnergyChanged: (Float) -> Unit,
    crampsSeverity: Float,
    onCrampsChanged: (Float) -> Unit,
    selectedWater: String,
    onWaterSelected: (String) -> Unit,
    selectedSymptoms: MutableList<String>,
    notesText: String,
    onNotesChanged: (String) -> Unit,
    onCommit: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {

        // 3. Observation Date Input field
        Column {
            Text(
                text = "Observation Date",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = PrimaryPink,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = selectedDate,
                        color = PrimaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Active Choice",
                    color = PrimaryPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SoftPink)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // 4. Mood Profile Grid
        Column {
            Text(
                text = "Mood State Profile",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Select the dominant emotional theme matching your current endocrine balance.",
                fontSize = 11.sp,
                color = SecondaryText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val moodItems = listOf(
                    Triple("Very Low", "😔", HealingGreen),
                    Triple("Low", "🥺", CalmBlue),
                    Triple("Balanced", "😐", TherapyPurple),
                    Triple("Good", "😌", WarmPeach),
                    Triple("Radiant", "🌸", SoftPink)
                )
                moodItems.forEach { (moodName, emoji, activeColor) ->
                    val isSelected = selectedMood == moodName
                    MoodCardItem(
                        mood = moodName,
                        emoji = emoji,
                        isSelected = isSelected,
                        activeBgColor = activeColor,
                        onClick = { onMoodSelected(moodName) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 5. Flow Intensity Selector
        Column {
            Text(
                text = "Flow Level Intensity",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .border(1.dp, Border, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val flows = listOf("None", "Spotting", "Light", "Medium", "Heavy")
                flows.forEach { flow ->
                    val isSelected = selectedFlow == flow
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PrimaryPink else Color.Transparent)
                            .clickable { onFlowSelected(flow) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = flow,
                            color = if (isSelected) Color.White else SecondaryText,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 6. Sleep Pattern Chips
        Column {
            Text(
                text = "Sleep Rest Duration",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sleepPatterns = listOf("4-5 HRS", "5-7 HRS", "7-9 HRS", "8-12 HRS", "12-15 HRS", "15+ HRS")
                sleepPatterns.forEach { p ->
                    val isSelected = selectedSleep == p
                    Box(
                        modifier = Modifier
                            .shadow(elevation = if (isSelected) 3.dp else 0.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(if (isSelected) SoftPink else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PrimaryPink else Border,
                                shape = CircleShape
                            )
                            .clickable { onSleepSelected(p) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = p,
                            color = if (isSelected) DeepPink else SecondaryText,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 7. Wellness Toggle Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WellnessToggleCard(
                title = "Ovulation Window",
                subText = if (isOvulationActive) "Peak Fertility" else "Not predicted",
                icon = Icons.Filled.Whatshot,
                isChecked = isOvulationActive,
                onCheckedChange = onOvulationToggled,
                modifier = Modifier.weight(1f)
            )
            WellnessToggleCard(
                title = "Medications",
                subText = if (isMedicationsTaken) "Taken as ordered" else "Missed logs",
                icon = Icons.Filled.Medication,
                isChecked = isMedicationsTaken,
                onCheckedChange = onMedicationsToggled,
                modifier = Modifier.weight(1f)
            )
        }

        // 8. Custom Sliders: Pain Intensity, Energy Vitality, Cramps Severity
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            CustomHorizontalSlider(
                title = "Pain Intensity Score",
                value = painIntensity,
                onValueChange = onPainChanged,
                leftLabel = "NONE",
                rightLabel = "SEVERE"
            )
            CustomHorizontalSlider(
                title = "Energy Vitality Coefficient",
                value = energyVitality,
                onValueChange = onEnergyChanged,
                leftLabel = "DRAINED",
                rightLabel = "VIBRANT"
            )
            CustomHorizontalSlider(
                title = "Pelvic Floor Cramps Severity",
                value = crampsSeverity,
                onValueChange = onCrampsChanged,
                leftLabel = "NONE",
                rightLabel = "SEVERE"
            )
        }

        // 10. Water Intake Chips
        Column {
            Text(
                text = "Hydration Intake",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val waterIntakes = listOf("250 ML", "500 ML", "750 ML", "1L", "1.5L", "2L", "3L", "4L", "5L+")
                waterIntakes.forEach { w ->
                    val isSelected = selectedWater == w
                    Box(
                        modifier = Modifier
                            .shadow(elevation = if (isSelected) 2.dp else 0.dp, shape = CircleShape)
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryPink else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) PrimaryPink else Border,
                                shape = CircleShape
                            )
                            .clickable { onWaterSelected(w) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = w,
                            color = if (isSelected) Color.White else SecondaryText,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // 11. Symptom Cluster Chips (Wrapping dynamically)
        Column {
            Text(
                text = "Symptom Observation Cluster",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Select all maternal or physical sensations currently detectable.",
                fontSize = 11.sp,
                color = SecondaryText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val symptomsList = listOf(
                "Nausea", "Aching", "Swelling", "Insomnia", "Cramps",
                "Bloating", "Headache", "Fatigue", "Spotting", "Tender Breasts"
            )

            com.example.features.carejourney.FlowRow(
                modifier = Modifier.fillMaxWidth(),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp
            ) {
                symptomsList.forEach { s ->
                    val isChecked = selectedSymptoms.contains(s)
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
                                if (isChecked) selectedSymptoms.remove(s)
                                else selectedSymptoms.add(s)
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
                                text = s,
                                fontSize = 12.sp,
                                color = if (isChecked) DeepPink else SecondaryText,
                                fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // 13. Medical Notes Area
        Column {
            Text(
                text = "Clinical Observations Notice Board",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Record notes to discuss with your obstetrician or therapist.",
                fontSize = 11.sp,
                color = SecondaryText,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = notesText,
                onValueChange = onNotesChanged,
                placeholder = { Text("Any specific observations for your doctor…", color = MutedText, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = PrimaryPink,
                    unfocusedBorderColor = Border
                )
            )
        }

        // 14. Commit Entry CTA Button (White on Navy shape matching prompt)
        Button(
            onClick = onCommit,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0D1730), // Deep Navy
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(4.dp, shape = RoundedCornerShape(32.dp))
        ) {
            Icon(
                imageVector = Icons.Filled.Security,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "COMMIT ENTRY",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun MoodCardItem(
    mood: String,
    emoji: String,
    isSelected: Boolean,
    activeBgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (isSelected) 1.08f else 1.0f, label = "mood_scale")
    val col = if (isSelected) activeBgColor.copy(alpha = 0.4f) else Color.White
    val borderCol = if (isSelected) PrimaryPink else Border
    val shadowElev = if (isSelected) 4.dp else 1.dp

    Column(
        modifier = modifier
            .scale(scale)
            .shadow(shadowElev, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(col)
            .border(width = if (isSelected) 2.dp else 1.dp, color = borderCol, shape = RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = mood,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PrimaryText else SecondaryText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WellnessToggleCard(
    title: String,
    subText: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(22.dp))
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .border(1.dp, Border, RoundedCornerShape(22.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SoftPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = DeepPink,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryPink,
                        uncheckedThumbColor = MutedText,
                        uncheckedTrackColor = Color(0xFFF3F4F6)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subText,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}

@Composable
fun CustomHorizontalSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    leftLabel: String,
    rightLabel: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Text(
                text = "${value.toInt()}/10",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryPink
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Custom High-fidelity slider
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..10f,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryPink,
                activeTrackColor = PrimaryPink,
                inactiveTrackColor = Color(0xFFE5E7EB),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = leftLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText
            )
            Text(
                text = "clinical self observation parameter",
                fontSize = 9.sp,
                color = MutedText.copy(alpha = 0.8f)
            )
            Text(
                text = rightLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MutedText
            )
        }
    }
}

// 15. Calendar Side Panel
@Composable
fun CalendarSidePanel(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    cycleLogs: List<CycleEntry>
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        
        // 1. Mini Clinical Calendar Widget
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 1.dp, color = Border, shape = RoundedCornerShape(26.dp))
                .shadow(2.dp, shape = RoundedCornerShape(26.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Calendar navigation top bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "May 2026",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = "Postpartum Rest Cycle map",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SoftPink)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Clinical Sync", fontSize = 11.sp, color = DeepPink, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Calendar week labels
                Row(modifier = Modifier.fillMaxWidth()) {
                    val days = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
                    days.forEach { d ->
                        Text(
                            text = d,
                            color = MutedText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar numerical days setup representing May 2026!
                // May 1st was a Friday. Friday index is 5.
                val offset = 5
                val totalDays = 31

                val totalCells = offset + totalDays
                val rows = (totalCells + 6) / 7

                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (c in 0..6) {
                            val cellIndex = r * 7 + c
                            val dayNum = cellIndex - offset + 1

                            if (cellIndex in offset until totalCells) {
                                val dateStr = "$dayNum-May-2026"
                                val isSelected = selectedDate == dateStr

                                // Custom indications
                                // Active Period Days: 19th - 23rd May 2026
                                // Ovulation Window Days: 10th - 14th May 2026
                                val isPeriodActive = dayNum in 19..23
                                val isOvulationActive = dayNum in 10..14

                                val hasLog = cycleLogs.any { it.date == dateStr }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> Color(0xFF0D1730) // Deep Navy
                                                isPeriodActive -> SoftPink // Light Pink Period
                                                isOvulationActive -> Color(0xFFFEF3C7) // Pastel Yellow Ovulation (#FEF3C7)
                                                else -> Color.Transparent
                                            }
                                        )
                                        .border(
                                            width = if (isSelected) 0.dp else if (hasLog) 1.5.dp else 0.dp,
                                            color = if (hasLog) PrimaryPink else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { onDateSelected(dateStr) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dayNum.toString(),
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected || isPeriodActive || isOvulationActive) FontWeight.Bold else FontWeight.Medium,
                                            color = when {
                                                isSelected -> Color.White
                                                isPeriodActive -> DeepPink
                                                isOvulationActive -> Color(0xFFD97706) // Deep orange/yellow
                                                else -> PrimaryText
                                            }
                                        )
                                        if (hasLog && !isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .clip(CircleShape)
                                                    .background(PrimaryPink)
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom legends representation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(SoftPink)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Period active", fontSize = 11.sp, color = SecondaryText)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF3C7))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Ovulation prediction", fontSize = 11.sp, color = SecondaryText)
                    }
                }
            }
        }

        // 2. Cycle Insights Analytics Section
        Text(
            text = "Cycle Insights Map",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card 1: Cycle Variance Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Border, RoundedCornerShape(20.dp))
                    .shadow(1.dp, shape = RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CalmBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timeline,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Cycle Variance",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "28 ± 1.5 Days",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Extremely regular. Safe core variance threshold.",
                        fontSize = 10.sp,
                        color = MutedText,
                        lineHeight = 14.sp
                    )
                }
            }

            // Card 2: Next Predicted Cycle / Ovulation Target
            Card(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Border, RoundedCornerShape(20.dp))
                    .shadow(1.dp, shape = RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SoftPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Whatshot,
                            contentDescription = null,
                            tint = PrimaryPink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Next predicted",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "08-June-2026",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPink
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "17 Days remaining. Soft hormone preparation.",
                        fontSize = 10.sp,
                        color = MutedText,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
