package com.example.features.lactation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BabyChangingStation
import androidx.compose.material.icons.outlined.WaterDrop
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.LactationLog
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LactationTrackerScreen(
    viewModel: MainViewModel,
    onNavigateToSection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lactationLogs by viewModel.lactationLogs.collectAsState()

    // State holding the active input values
    var selectedType by remember { mutableStateOf("Breast") } // "Breast" or "Pump"
    var selectedSide by remember { mutableStateOf("Left") }   // "Left", "Right", "Both"
    var milkQuantityStr by remember { mutableStateOf("") }
    var durationStr by remember { mutableStateOf("") }
    var selectedResponse by remember { mutableStateOf("Happy") } // "Happy", "Sleepy", "Fussy", "Refused"

    var showSuccessDialog by remember { mutableStateOf(false) }

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
                title = "Lactation Sentinel",
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

            // Inner body Scroll view
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isTablet) {
                    // LEFT COLUMN: Forms, inputs, and Feeding History
                    Column(
                        modifier = Modifier
                            .weight(1.1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 110.dp)
                    ) {
                        LactationHeaderArea()
                        LactationTopTabs(onNavigate = onNavigateToSection)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lactation Input Form Card
                        MainLactationFormCard(
                            selectedType = selectedType,
                            onTypeChange = { selectedType = it },
                            selectedSide = selectedSide,
                            onSideChange = { selectedSide = it },
                            milkQuantityStr = milkQuantityStr,
                            onQuantityChange = { if (it.length <= 4) milkQuantityStr = it },
                            durationStr = durationStr,
                            onDurationChange = { if (it.length <= 3) durationStr = it },
                            selectedResponse = selectedResponse,
                            onResponseChange = { selectedResponse = it },
                            onCommitLog = {
                                val quantity = milkQuantityStr.toIntOrNull() ?: 0
                                val duration = durationStr.toIntOrNull() ?: 0
                                val formatMinutes = "%.0f".format(System.currentTimeMillis() / 60000.0) // fallback format block
                                viewModel.logLactationLog(
                                    LactationLog(
                                        timestamp = "22-May-2026, 06:12 pm",
                                        type = selectedType,
                                        side = selectedSide,
                                        quantityMl = quantity,
                                        durationMin = duration,
                                        response = selectedResponse
                                    )
                                )
                                showSuccessDialog = true
                                // reset fields (except default preferences)
                                milkQuantityStr = ""
                                durationStr = ""
                                selectedResponse = "Happy"
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Feeding History List
                        FeedingHistorySection(logs = lactationLogs)
                    }

                    // RIGHT COLUMN: Analytics, references, and guide summaries
                    Column(
                        modifier = Modifier
                            .weight(0.9f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 110.dp)
                    ) {
                        HydrationSupplyAnalyticsCard()
                        Spacer(modifier = Modifier.height(16.dp))
                        ReferenceChartsCard()
                        Spacer(modifier = Modifier.height(24.dp))
                        ClinicalWarningFooterView()
                    }
                } else {
                    // Mobile Scroll view stacking everything vertically
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 120.dp)
                    ) {
                        LactationHeaderArea()
                        LactationTopTabs(onNavigate = onNavigateToSection)

                        Spacer(modifier = Modifier.height(16.dp))

                        MainLactationFormCard(
                            selectedType = selectedType,
                            onTypeChange = { selectedType = it },
                            selectedSide = selectedSide,
                            onSideChange = { selectedSide = it },
                            milkQuantityStr = milkQuantityStr,
                            onQuantityChange = { if (it.length <= 4) milkQuantityStr = it },
                            durationStr = durationStr,
                            onDurationChange = { if (it.length <= 3) durationStr = it },
                            selectedResponse = selectedResponse,
                            onResponseChange = { selectedResponse = it },
                            onCommitLog = {
                                val quantity = milkQuantityStr.toIntOrNull() ?: 0
                                val duration = durationStr.toIntOrNull() ?: 0
                                viewModel.logLactationLog(
                                    LactationLog(
                                        timestamp = "22-May-2026, 06:01 pm",
                                        type = selectedType,
                                        side = selectedSide,
                                        quantityMl = quantity,
                                        durationMin = duration,
                                        response = selectedResponse
                                    )
                                )
                                showSuccessDialog = true
                                milkQuantityStr = ""
                                durationStr = ""
                                selectedResponse = "Happy"
                            }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        HydrationSupplyAnalyticsCard()

                        Spacer(modifier = Modifier.height(20.dp))

                        ReferenceChartsCard()

                        Spacer(modifier = Modifier.height(20.dp))

                        FeedingHistorySection(logs = lactationLogs)

                        Spacer(modifier = Modifier.height(24.dp))

                        ClinicalWarningFooterView()
                    }
                }
            }
        }

        // Commited Feedback Dialog Alert
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
                            text = "Feeding Log Registered",
                            color = PrimaryText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                text = {
                    Text(
                        text = "The lactation observations have been saved successfully to your clinical analytics feed.",
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B1430)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ) {
                        Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun LactationHeaderArea() {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.BabyChangingStation,
                contentDescription = null,
                tint = PrimaryPink,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Lactation Log",
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryText,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Track your feeding sessions & milk supply progress with medical guidelines.",
            fontSize = 13.sp,
            color = SecondaryText,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun LactationTopTabs(
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
            "Period Log" to { onNavigate("cycle_tracker") },
            "Lactation Log" to {},
            "Health Summary" to {}
        ).forEach { (tab, onClick) ->
            val isActive = tab == "Lactation Log"
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (isActive) Color(0xFF0B1430) else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isActive) Color(0xFF0B1430) else Color(0xFFE8EAF0),
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
fun MainLactationFormCard(
    selectedType: String,
    onTypeChange: (String) -> Unit,
    selectedSide: String,
    onSideChange: (String) -> Unit,
    milkQuantityStr: String,
    onQuantityChange: (String) -> Unit,
    durationStr: String,
    onDurationChange: (String) -> Unit,
    selectedResponse: String,
    onResponseChange: (String) -> Unit,
    onCommitLog: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(28.dp))
            .border(width = 1.dp, color = Color(0xFFE8EAF0), shape = RoundedCornerShape(28.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            // Feeding Type Selector (Segmented Row)
            Column {
                Text(
                    text = "Feeding Type",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F2F5))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Breast", "Pump").forEach { t ->
                        val isSel = selectedType == t
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(0xFF0B1430) else Color.Transparent)
                                .clickable { onTypeChange(t) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = t,
                                color = if (isSel) Color.White else SecondaryText,
                                fontSize = 13.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Side/Source Selector
            Column {
                Text(
                    text = "Side used / Breast Source",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F2F5))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Left", "Right", "Both").forEach { s ->
                        val isSel = selectedSide == s
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) Color(0xFF0B1430) else Color.Transparent)
                                .clickable { onSideChange(s) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = s,
                                color = if (isSel) Color.White else SecondaryText,
                                fontSize = 13.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Milk Quantity & Duration parameters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Milk Quantity Input
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Milk Quantity (ml)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = milkQuantityStr,
                        onValueChange = onQuantityChange,
                        placeholder = { Text("e.g. 120", color = MutedText, fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryPink,
                            unfocusedBorderColor = Color(0xFFE8EAF0)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Duration Input
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Duration (min)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = durationStr,
                        onValueChange = onDurationChange,
                        placeholder = { Text("e.g. 20", color = MutedText, fontSize = 14.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = PrimaryPink,
                            unfocusedBorderColor = Color(0xFFE8EAF0)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Baby Response Selector
            Column {
                Text(
                    text = "Baby Response & Nesting Comfort",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Log postpartum emotional connection, latching behavior, or restlessness.",
                    fontSize = 11.sp,
                    color = SecondaryText,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 2x2 grid for baby response selector cards
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val firstRow = listOf(
                        Triple("Happy", "😊", "Content & Calm"),
                        Triple("Sleepy", "😴", "Fell asleep quickly")
                    )
                    val secondRow = listOf(
                        Triple("Fussy", "😢", "Restless / crying state"),
                        Triple("Refused", "🙅‍♀️", "Did not latch")
                    )

                    ResponseRow(
                        rowItems = firstRow,
                        selectedResponse = selectedResponse,
                        onSelected = onResponseChange
                    )
                    ResponseRow(
                        rowItems = secondRow,
                        selectedResponse = selectedResponse,
                        onSelected = onResponseChange
                    )
                }
            }

            // Commit Feeding CTA Button (White on Navy shape matching guides)
            Button(
                onClick = {
                    if (milkQuantityStr.isNotBlank() && durationStr.isNotBlank()) {
                        onCommitLog()
                    }
                },
                enabled = milkQuantityStr.isNotBlank() && durationStr.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B1430),
                    disabledContainerColor = Color(0xFFD7DCE5)
                ),
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(32.dp))
            ) {
                Icon(
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COMMIT FEEDING LOG",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun ResponseRow(
    rowItems: List<Triple<String, String, String>>,
    selectedResponse: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rowItems.forEach { (resName, emoji, desc) ->
            val isSelected = selectedResponse == resName
            val bgColor = if (isSelected) SoftPink.copy(alpha = 0.4f) else Color.White
            val borderCol = if (isSelected) PrimaryPink else Color(0xFFE8EAF0)
            val scale by animateFloatAsState(if (isSelected) 1.03f else 1.0f, label = "card_scale")

            Card(
                modifier = Modifier
                    .weight(1f)
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .border(width = if (isSelected) 2.dp else 1.dp, color = borderCol, shape = RoundedCornerShape(16.dp))
                    .clickable { onSelected(resName) },
                colors = CardDefaults.cardColors(containerColor = bgColor)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = resName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PrimaryText
                        )
                        Text(
                            text = desc,
                            fontSize = 11.sp,
                            color = SecondaryText,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeedingHistorySection(logs: List<LactationLog>) {
    Column {
        Text(
            text = "Feeding Session Logs",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            logs.forEach { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(1.dp, shape = RoundedCornerShape(18.dp))
                        .border(1.dp, Color(0xFFE8EAF0), RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(SoftPink),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (log.type == "Breast") Icons.Filled.ChildCare else Icons.Filled.LocalActivity,
                                    contentDescription = null,
                                    tint = PrimaryPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${log.type.uppercase()} • ${log.side.uppercase()} • ${log.quantityMl} ml",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Duration: ${log.durationMin} mins • ${log.timestamp}",
                                    fontSize = 11.sp,
                                    color = SecondaryText
                                )
                            }
                        }

                        // Response indicator badge
                        val badgeBg = when (log.response) {
                            "Happy" -> Color(0xFFB8F2D6)
                            "Sleepy" -> Color(0xFFD8F0FF)
                            "Fussy" -> Color(0xFFFFD7C2)
                            else -> Color(0xFFFFE5F0)
                        }
                        val badgeColor = when (log.response) {
                            "Happy" -> Color(0xFF047857)
                            "Sleepy" -> Color(0xFF0369A1)
                            "Fussy" -> Color(0xFFC2410C)
                            else -> Color(0xFFC93C78)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(badgeBg)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = log.response.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HydrationSupplyAnalyticsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(26.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1430)),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HYDRATION & SUPPLY INDEX",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5FA2),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Optimal Milk Index",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null,
                        tint = Color(0xFFFF5FA2),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Neon supply metrics row representation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Daily Intake", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    Text(text = "3.2 Liters", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Optimal Supply",
                        color = Color(0xFF34D399),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Pump Volume (24h)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    Text(text = "340 ml total", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFF5FA2))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+12% from yesterday",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar simulation
            LinearProgressIndicator(
                progress = { 0.8f },
                color = Color(0xFFFF5FA2),
                trackColor = Color.White.copy(alpha = 0.15f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "1-2 Liters -> Low Supply", fontSize = 9.sp, color = Color.White.copy(alpha = 0.5f))
                Text(text = "3-4 Liters -> Optimal", fontSize = 9.sp, color = Color(0xFF34D399), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ReferenceChartsCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color(0xFFE8EAF0), shape = RoundedCornerShape(26.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Clinical Reference Parameters",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 1. Reference Chart
            Column {
                Text(text = "MILK QUANTITY GUIDANCE", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                listOf(
                    "Low Output" to "0 – 60 ml",
                    "Normal Range" to "60 – 100 ml",
                    "High Supply" to "120 – 160 ml",
                    "Hyper-Lactation" to "160+ ml"
                ).forEach { (label, range) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = label, fontSize = 12.sp, color = SecondaryText)
                        Text(text = range, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F2F5))

            // 2. Feeding Duration Card
            Column {
                Text(text = "FEEDING DURATION STANDARDS", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                listOf(
                    "Short / Efficient" to "5 – 10 min",
                    "Normal Standard" to "10 – 25 min",
                    "Prolonged session" to "25 – 40 min"
                ).forEach { (label, min) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = label, fontSize = 12.sp, color = SecondaryText)
                        Text(text = min, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F2F5))

            // 3. Condition Index Card
            Column {
                Text(text = "MATERNAL SENSATION INDEX", fontSize = 10.sp, color = MutedText, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                listOf(
                    "Satisfied Connection" to "Calm & complete empty",
                    "Hungry / Insufficient" to "Immediate crying / seeking",
                    "Early Mastitis Warning" to "Redness & hot flash parameters",
                    "Blocked Duct" to "Painful local swelling nodules"
                ).forEach { (label, state) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = label, fontSize = 12.sp, color = SecondaryText)
                        Text(
                            text = state,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryPink,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClinicalWarningFooterView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFEF2F2))
            .border(width = 1.dp, color = Color(0xFFFCA5A5), shape = RoundedCornerShape(16.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "CLINICAL WARNING SENSITIVITY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF4444)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "HARD LUMPS OR SEVERE SWELLING REQUIRE IMMEDIATE CLINICAL CONSULTATION.",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF991B1B),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}
