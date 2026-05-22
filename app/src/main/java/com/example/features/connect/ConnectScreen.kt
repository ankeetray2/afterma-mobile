package com.example.features.connect

import androidx.compose.animation.*
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
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.database.entities.TherapySession
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ConnectScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val providers by viewModel.therapySessions.collectAsState()

    var showConfirmationDialog by remember { mutableStateOf<TherapySession?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Browse Advisors, 1: Booked Consultations

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        SoftTopBar(
            title = "Maternal Consultation",
            actions = {
                IconButton(onClick = {}) {
                    Icon(imageVector = Icons.Filled.Verified, contentDescription = "Trust verified", tint = Success)
                }
            }
        )

        // Clean double tabs controller
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = PrimaryPink,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PrimaryPink
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Consult Specialists", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Your Consults (${providers.filter { it.isBooked }.size})", fontSize = 14.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Browse Therapists list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        Text(
                            text = "Licensed Maternal Experts",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Consult certified perinatal psychotherapists, physical restoration specialists, and Ayurvedic nutritionists.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }

                items(providers) { provider ->
                    TherapyCard(
                        name = provider.providerName,
                        title = provider.providerTitle,
                        specialty = provider.specialty,
                        time = "${provider.appointmentDate} at ${provider.appointmentTime} - (${provider.durationMinutes} mins)",
                        isBooked = provider.isBooked,
                        onBookClick = {
                            if (provider.isBooked) {
                                viewModel.cancelAppointment(provider.id)
                            } else {
                                showConfirmationDialog = provider
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        } else {
            // Booked history/upcoming lists
            val bookedList = providers.filter { it.isBooked }

            if (bookedList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
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
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                tint = PrimaryPink,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No active consults booked",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Browse certified advisors in the first tab to schedule a session.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(bookedList) { visit ->
                        TherapyCard(
                            name = visit.providerName,
                            title = visit.providerTitle,
                            specialty = visit.specialty,
                            time = "${visit.appointmentDate} - ${visit.appointmentTime} (${visit.durationMinutes}m)",
                            isBooked = true,
                            onBookClick = { viewModel.cancelAppointment(visit.id) },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }

        // Standard booking confirm dialogue
        if (showConfirmationDialog != null) {
            val provider = showConfirmationDialog!!
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = null },
                shape = RoundedCornerShape(24.dp),
                containerColor = Color.White,
                title = {
                    Text(
                        text = "Schedule Consultation",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText,
                        fontSize = 20.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Are you ready to book an appointment with ${provider.providerName}?",
                            fontSize = 14.sp,
                            color = SecondaryText
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Filled.CalendarToday, contentDescription = null, tint = PrimaryPink, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${provider.appointmentDate} at ${provider.appointmentTime}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.bookAppointment(provider.id)
                            showConfirmationDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm Appointment", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showConfirmationDialog = null }
                    ) {
                        Text("Cancel", color = SecondaryText)
                    }
                }
            )
        }
    }
}
