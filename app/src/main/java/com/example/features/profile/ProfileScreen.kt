package com.example.features.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val currentName by viewModel.currentUserName.collectAsState()
    val currentEmail by viewModel.currentUserEmail.collectAsState()
    val healingProgressIndex by viewModel.healingProgressIndex.collectAsState()
    val mindfulnessCount by viewModel.mindfulnessHistory.collectAsState()
    val journalLogsCount by viewModel.moodEntries.collectAsState()

    var editingName by remember { mutableStateOf(currentName) }
    var isEditingMode by remember { mutableStateOf(false) }

    var notificationsEnabled by remember { mutableStateOf(true) }
    var privacyModeEnabled by remember { mutableStateOf(true) }

    var showEmergencyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        SoftTopBar(
            title = "My Sanctuary Profile",
            actions = {
                // Immediate Emergency Trigger Hotline
                Button(
                    onClick = { showEmergencyDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("hotline_trigger")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.PhoneInTalk, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "SOS Care", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 100.dp), // margin for bottom floating nav
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Card Details
            AftermaCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(CalmBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Female,
                            contentDescription = null,
                            tint = DeepPink,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditingMode) {
                        AftermaInput(
                            value = editingName,
                            onValueChange = { editingName = it },
                            placeholder = "Enter name",
                            label = "Pen Name",
                            textTag = "edit_profile_name"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                viewModel.loginUser(currentEmail, editingName)
                                isEditingMode = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Success),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Save Calming Alias")
                        }
                    } else {
                        Text(
                            text = currentName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = if (currentEmail.isBlank()) "mother@afterma.com" else currentEmail,
                            fontSize = 13.sp,
                            color = SecondaryText
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { isEditingMode = true },
                            border = BorderStroke(1.dp, Border),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Edit Calming Alias", fontSize = 11.sp, color = PrimaryPink)
                        }
                    }
                }
            }

            // Wellness statistics aggregations
            Text(text = "Overall Healing Analytics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // healing progress indicator
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Border, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "$healingProgressIndex%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DeepPink)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Core Healing factor", fontSize = 11.sp, color = SecondaryText, textAlign = TextAlign.Center)
                    }
                }

                // breaths index count
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Border, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "${mindfulnessCount.size} sessions", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PrimaryPink)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Guided breaths", fontSize = 11.sp, color = SecondaryText, textAlign = TextAlign.Center)
                    }
                }

                // journal log count
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, Border, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "${journalLogsCount.size} logs", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DeepPink)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Chronology entries", fontSize = 11.sp, color = SecondaryText, textAlign = TextAlign.Center)
                    }
                }
            }

            // App settings triggers
            Text(text = "Sanctuary Configuration", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryText)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Border, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Toggle notifications
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.NotificationsNone, contentDescription = null, tint = PrimaryPink)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "Restorative Reminders", fontSize = 14.sp, color = PrimaryText)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryPink, checkedTrackColor = SoftPink)
                        )
                    }

                    Divider(color = DividerColor)

                    // Toggle privacy data mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Security, contentDescription = null, tint = PrimaryPink)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = "HIPAA Secure Offline Lock", fontSize = 14.sp, color = PrimaryText)
                        }
                        Switch(
                            checked = privacyModeEnabled,
                            onCheckedChange = { privacyModeEnabled = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryPink, checkedTrackColor = SoftPink)
                        )
                    }

                    Divider(color = DividerColor)

                    // Logout trigger
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.logout() }
                            .padding(vertical = 14.dp)
                            .testTag("logout_setting_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Filled.ExitToApp, contentDescription = null, tint = ErrorColor)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Depart Sanctuary Session", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ErrorColor)
                    }
                }
            }
        }

        // Emergency Postpartum hotline dialogue overlay
        if (showEmergencyDialog) {
            AlertDialog(
                onDismissRequest = { showEmergencyDialog = false },
                shape = RoundedCornerShape(28.dp),
                containerColor = Color(0xFFFFF1F2), // Delicate rose background to highlight urgency but stay safe
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.EmergencyShare, contentDescription = null, tint = ErrorColor, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "SOS Care Hotline", fontWeight = FontWeight.Bold, color = ErrorColor)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Are you experiencing severe abdominal pain, persistent fever, localized breast tenderness accompanied by chills, or acute thoughts of distress?",
                            fontSize = 14.sp,
                            color = PrimaryText,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Connecting dials immediately to our 24/7 National Maternal Mental Health & Clinical Emergency Support Nurse Hotline (1-833-TLC-MAMA).",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            lineHeight = 18.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showEmergencyDialog = false
                            // Standard dialer intent representation
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:18338526262")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Dialer fallback logs
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("hotline_dial_confirm")
                    ) {
                        Text("Connect Instantly", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEmergencyDialog = false }
                    ) {
                        Text("Dismiss Safe", color = SecondaryText)
                    }
                }
            )
        }
    }
}
