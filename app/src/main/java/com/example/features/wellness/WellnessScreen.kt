package com.example.features.wellness

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.entities.MindfulnessSession
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun WellnessScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val completedSessions by viewModel.mindfulnessHistory.collectAsState()

    var isBreathingActive by remember { mutableStateOf(false) }
    var breathingType by remember { mutableStateOf("Mindful Breathing") }
    var breathingSecondsElapsed by remember { mutableIntStateOf(0) }
    val targetBreathingSeconds = 60 // 1 minute focus meditation

    // Infinite breathing guide oscillation
    val infiniteTransition = rememberInfiniteTransition(label = "breathe_pulse")
    val pulseFraction by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_scale"
    )

    // Derived Instruction State: 0-2s Inhale, 2-4s Exhale
    val promptText by remember {
        derivedStateOf {
            if (pulseFraction > 0.95f) {
                "Pause & Release..."
            } else if (pulseFraction < 0.65f) {
                "Ready..."
            } else if (pulseFraction > 0.8f) {
                "Inhale gently..."
            } else {
                "Exhale slowly..."
            }
        }
    }

    // Active timer loop
    LaunchedEffect(isBreathingActive) {
        if (isBreathingActive) {
            breathingSecondsElapsed = 0
            while (isBreathingActive && breathingSecondsElapsed < targetBreathingSeconds) {
                delay(1000)
                breathingSecondsElapsed++
            }
            if (breathingSecondsElapsed >= targetBreathingSeconds) {
                // Done! Automatically log session complete
                viewModel.logMindfulnessSession(
                    type = breathingType,
                    durationSeconds = targetBreathingSeconds,
                    feedback = "Felt lighter & deeply calm"
                )
                isBreathingActive = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        SoftTopBar(
            title = "Sanctuary Quiet Room",
            actions = {
                Icon(
                    imageVector = Icons.Filled.AirlineSeatReclineExtra,
                    contentDescription = null,
                    tint = PrimaryPink
                )
            }
        )

        if (isBreathingActive) {
            // Screen covers active meditation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = breathingType,
                        style = MaterialTheme.typography.titleLarge,
                        color = DeepPink,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calming countdown: ${targetBreathingSeconds - breathingSecondsElapsed}s remaining",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                }

                // Breathing expanding rings
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .scale(pulseFraction),
                    contentAlignment = Alignment.Center
                ) {
                    // Outermost rings
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(SoftPink.copy(alpha = 0.15f))
                    )
                    Box(
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(TherapyPurple.copy(alpha = 0.3f))
                    )
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(CalmBlue.copy(alpha = 0.45f))
                    )
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(PrimaryPink)
                            .shadow(elevation = 8.dp, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Spa,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Visual prompt instruction Text
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = promptText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = { isBreathingActive = false },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("end_breathing_session")
                    ) {
                        Text(text = "End Reflections", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Standard wellness control dashboards
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Text(
                        text = "Calm & Vaginal Vagus Regulation",
                        style = MaterialTheme.typography.headlineMedium,
                        color = PrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Vagus nerve stimulation exercises compiled by certified psychiatrists to restore postpartum autonomic harmony.",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                // Exercise Category Options Selection
                item {
                    AftermaCard(modifier = Modifier.testTag("breathing_card")) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(SoftPink),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Spa,
                                        contentDescription = null,
                                        tint = PrimaryPink,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Concentric Breath-Work",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryText
                                    )
                                    Text(
                                        text = "4-4 Inhalation-Exhalation cycle",
                                        fontSize = 12.sp,
                                        color = SecondaryText
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Slow deep cycles release natural oxytocin and soothe tightness inside perineal area.",
                                fontSize = 12.sp,
                                color = SecondaryText,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        breathingType = "Deep Parasympathetic Calm"
                                        isBreathingActive = true
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Anxiety Release", color = PrimaryText, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        breathingType = "Postpartum Mindful Breathing"
                                        isBreathingActive = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("start_breathing_button")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.PlayArrow,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Begin Cleanse", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Somatic therapy audios list mock
                item {
                    Text(
                        text = "Therapeutic Soundscapes",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AftermaCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(CalmBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.MusicNote,
                                            contentDescription = null,
                                            tint = DeepPink,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(text = "Infant Cozy Womb Heartbeats", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                                        Text(text = "Rhythms mimicking the vascular mother environment", fontSize = 11.sp, color = SecondaryText)
                                    }
                                }
                                IconButton(onClick = {}) {
                                    Icon(imageVector = Icons.Filled.PlayCircleFilled, contentDescription = "Play", tint = PrimaryPink, modifier = Modifier.size(32.dp))
                                }
                            }
                        }

                        AftermaCard {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(TherapyPurple),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.SelfImprovement,
                                            contentDescription = null,
                                            tint = DeepPink,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(text = "Tension Release Bone Scan", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                                        Text(text = "Somatic focus on pelvic realignment relaxing state", fontSize = 11.sp, color = SecondaryText)
                                    }
                                }
                                IconButton(onClick = {}) {
                                    Icon(imageVector = Icons.Filled.PlayCircleFilled, contentDescription = "Play", tint = PrimaryPink, modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    }
                }

                // History metrics list
                item {
                    Text(
                        text = "History of Mindful Space",
                        style = MaterialTheme.typography.titleLarge,
                        color = PrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (completedSessions.isEmpty()) {
                    item {
                        AftermaCard {
                            Text(
                                text = "Nurture your calm. Complete a 1-minute space cleanse above to log statistics.",
                                fontSize = 12.sp,
                                color = SecondaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                } else {
                    items(completedSessions) { item ->
                        AftermaCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = item.type, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                                    Text(text = "Duration: ${item.durationSeconds}s completed", fontSize = 11.sp, color = SecondaryText)
                                }
                                Text(
                                    text = item.feedback,
                                    fontSize = 11.sp,
                                    color = DeepPink,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(SoftPink)
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
