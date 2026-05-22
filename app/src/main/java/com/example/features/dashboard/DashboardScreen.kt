package com.example.features.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.entities.RecipeEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import kotlin.math.sin

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToSection: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.currentUserName.collectAsState()
    val healingIndex by viewModel.healingProgressIndex.collectAsState()
    val recipesList by viewModel.recipes.collectAsState()
    val sessionsList by viewModel.therapySessions.collectAsState()
    val activeBooked = sessionsList.filter { it.isBooked }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(bottom = 100.dp) // Leave abundant breathing room for FloatingBottomNav bar
    ) {
        // Soft Top Bar matching HTML exactly
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WELCOME BACK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryText,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Hello, $userName",
                    color = PrimaryPink,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
            }
            // Double border avatar with background layout matching HTML
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(TherapyPurple)
                    .border(2.dp, SoftPink, CircleShape)
                    .clickable { onNavigateToSection("profile") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (userName.isNotEmpty()) userName.take(1).uppercase() else "S",
                    color = DeepPink,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {

            // 1. Premium Gradient Hero Greeting Card (Phase 4 Card)
            GradientHeroCard(
                title = "Welcome, $userName",
                subtitle = "Week 3 of Postpartum Nesting. Rest your body fully today. Your cells are reconstructing beautifully.",
                illustration = {
                    Icon(
                        imageVector = Icons.Filled.CloudQueue,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier
                            .size(72.dp)
                            .align(Alignment.CenterEnd)
                    )
                },
                modifier = Modifier.testTag("dashboard_hero_card")
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Healing Progress Overview
            Text(
                text = "Your Healing Metrics",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Progress Stat Circle (Left)
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(28.dp))
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White)
                        .border(1.dp, Border, RoundedCornerShape(28.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Healing Flow",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { healingIndex / 100f },
                                modifier = Modifier.size(84.dp),
                                color = PrimaryPink,
                                strokeWidth = 10.dp,
                                trackColor = SoftPink,
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$healingIndex%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepPink
                                )
                                Text(text = "Restored", fontSize = 9.sp, color = MutedText)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Stable Core Flow",
                            fontSize = 11.sp,
                            color = HealingGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(HealingGreen.copy(alpha = 0.25f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                // Grid widgets (Right column inside Wellness overview)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    WellnessStatCard(
                        title = "Uterine Rest",
                        value = "Stage 3",
                        unit = "/5",
                        icon = Icons.Filled.Favorite,
                        iconColor = PrimaryPink,
                        containerColor = SoftPink,
                        subText = "Calming tone"
                    )
                    WellnessStatCard(
                        title = "Daily Sleep",
                        value = "7.5",
                        unit = "hrs",
                        icon = Icons.Filled.Bedtime,
                        iconColor = DeepPink,
                        containerColor = TherapyPurple,
                        subText = "Inc. restorative"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Quick Daily Wellness Logging Trigger (Phase 5 calendar anchor / shortcut)
            AftermaCard(modifier = Modifier.testTag("logging_shortcut_card")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Log Daily Symptoms & Mood",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Record energy levels, healing stage, and emotional states.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            lineHeight = 16.sp
                        )
                    }
                    IconButton(
                        onClick = { onNavigateToSection("care_journey") },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SoftPink)
                            .testTag("dashboard_navigate_journey")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Quick Check-in Log",
                            tint = DeepPink
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dedicated Period Log & Menstrual Cycle Tracking Shortcut
            AftermaCard(modifier = Modifier.testTag("cycle_logging_shortcut_card")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryPink)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                    text = "MENSTRUAL CYCLE SENTINEL",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryPink,
                                    letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Period Log & Cycle Tracking",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Track hormone phases, pelvic floor discomfort, fluid intake, and sleep bonds.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            lineHeight = 16.sp
                        )
                    }
                    IconButton(
                        onClick = { onNavigateToSection("cycle_tracker") },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0D1730)) // Deep Navy Matching Visual Guides
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CalendarMonth,
                            contentDescription = "Log Cycle",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dedicated Lactation Tracking Shortcut
            AftermaCard(modifier = Modifier.testTag("lactation_logging_shortcut_card")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFF5FA2))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                    text = "LACTATION & SUPPLY ANALYTICS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF5FA2),
                                    letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lactation Log & Milk Index",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Log feed quality, nursing side, pump volumes, and hydration guidelines.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            lineHeight = 16.sp
                        )
                    }
                    IconButton(
                        onClick = { onNavigateToSection("lactation_tracker") },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B1430))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChildCare,
                            contentDescription = "Log Feeding",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Healing Pulse Waveform (Using Canvas vector drawing)
            Text(
                text = "Your Healing Resonance",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "A calming visual sinusoid mapping your parasympathetic rest levels.",
                fontSize = 12.sp,
                color = SecondaryText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(28.dp))
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .border(1.dp, Border, RoundedCornerShape(28.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val centerY = height / 2f

                    val path = Path()
                    path.moveTo(0f, centerY)

                    // Draw a gorgeous slow therapeutic sine curve wave representing calm breathing state
                    for (x in 0..width.toInt()) {
                        val radians = (x / width) * 2f * Math.PI * 2f
                        val y = centerY + sin(radians).toFloat() * 32f * sin(radians / 4f).toFloat()
                        path.lineTo(x.toFloat(), y)
                    }

                    drawPath(
                        path = path,
                        color = PrimaryPink,
                        style = Stroke(width = 4f)
                    )

                    // Draw healing nodes (peaceful sparkles)
                    drawCircle(
                        color = DeepPink,
                        radius = 6f,
                        center = Offset(width * 0.25f, centerY + sin(0.25f * 2f * Math.PI * 2f).toFloat() * 15f)
                    )
                    drawCircle(
                        color = Success,
                        radius = 8f,
                        center = Offset(width * 0.65f, centerY + sin(0.65f * 2f * Math.PI * 2f).toFloat() * -10f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(start = 12.dp, end = 12.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Deep Vagus Activation", fontSize = 10.sp, color = MutedText)
                    Text(text = "Calm Resting Rate", fontSize = 10.sp, color = MutedText)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 5. Nourishing Maternal Nutrition Preview (Ayurvedic Recipes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommended Postpartum Eats",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryPink,
                    modifier = Modifier
                        .clickable { onNavigateToSection("recipes") }
                        .padding(8.dp)
                        .testTag("dashboard_see_recipes")
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(recipesList.take(3)) { recipe ->
                    DashboardRecipeItem(
                        recipe = recipe,
                        onClick = { onNavigateToSection("recipes") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Care Connect Booking status (Phase 9 reminder)
            if (activeBooked.isNotEmpty()) {
                Text(
                    text = "Soothing Appointments Today",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                activeBooked.forEach { visit ->
                    AftermaCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(TherapyPurple),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DateRange,
                                    contentDescription = null,
                                    tint = PrimaryPink,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = visit.providerName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryText
                                )
                                Text(
                                    text = "${visit.appointmentDate} at ${visit.appointmentTime}",
                                    fontSize = 12.sp,
                                    color = SecondaryText
                                )
                            }
                            Text(
                                text = "Confirmed",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Success,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Success.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            } else {
                // Friendly recommendation callout to therapist
                AftermaCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSection("connect") }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TherapyPurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SupportAgent,
                                contentDescription = null,
                                tint = DeepPink,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ready to Consult guidance?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Text(
                                text = "Specialists in physical pelvic restorative therapy are available.",
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = PrimaryPink
                        )
                    }
                }
            }
        }
    }
}

// Compact horizontally scrolled Recipe asset card
@Composable
fun DashboardRecipeItem(
    recipe: RecipeEntity,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .border(1.dp, Border, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SoftPink),
                contentAlignment = Alignment.Center
            ) {
                // Delicate soothing graphic placeholders
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.RestaurantMenu,
                        contentDescription = null,
                        tint = PrimaryPink,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = recipe.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepPink
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = recipe.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = recipe.prepTime, fontSize = 11.sp, color = SecondaryText)
                Text(
                    text = recipe.calories,
                    fontSize = 11.sp,
                    color = DeepPink,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
