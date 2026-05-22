package com.example.features.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.BorderStroke
import com.example.ui.components.AftermaButton
import com.example.ui.components.AftermaInput
import com.example.ui.theme.*
import kotlinx.coroutines.delay

// --- Splash Screen ---
@Composable
fun SplashScreen(
    onSplashCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.85f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "splash_scale"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000) // Tranquil 2.0s hold
        onSplashCompleted()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AftermaGradients.SoftWellness),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale)
        ) {
            // Sacred lotus floral emblem
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Spa,
                    contentDescription = null,
                    tint = PrimaryPink,
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "AFTERMA",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = DeepPink,
                letterSpacing = 4.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Maternal Recovery & Emotional Sanctuary",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SecondaryText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

// --- Onboarding Screen ---
@Composable
fun OnboardingScreen(
    onOnboardingCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val pages = listOf(
        OnboardingPageData(
            title = "A Safe Therapeutic Space",
            subtitle = "Welcome to your postpartum harbor. Afterma combines maternal healing, clinical guidance, and emotional trackers to cushion your transition into motherhood.",
            icon = Icons.Filled.FavoriteBorder,
            color = SoftPink,
            tint = PrimaryPink
        ),
        OnboardingPageData(
            title = "Ayurvedic Nutrition",
            subtitle = "Nourish your depleted body with warm, restorative postpartum catalog recipes designed specially for hormonal calibration and lactation boost.",
            icon = Icons.Filled.Restaurant,
            color = CalmBlue,
            tint = DeepPink
        ),
        OnboardingPageData(
            title = "Connect with Trust",
            subtitle = "Safely consult certified perinatal psychotherapists, pediatric advisors, and lactation mentors who understand the delicate nuances of motherhood.",
            icon = Icons.Filled.VerifiedUser,
            color = TherapyPurple,
            tint = PrimaryPink
        )
    )

    val page = pages[currentPage]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "Skip",
                    color = SecondaryText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable { onOnboardingCompleted() }
                        .padding(8.dp)
                        .testTag("skip_onboarding")
                )
            }

            // Carousel Slide
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    },
                    label = "onboarding_slider"
                ) { targetPage ->
                    val pageData = pages[targetPage]
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(pageData.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = pageData.icon,
                                contentDescription = null,
                                tint = pageData.tint,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = pageData.title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = PrimaryText,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = pageData.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryText,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }

            // Footer navigation with Indicators
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (index == currentPage) 24.dp else 8.dp, height = 8.dp)
                                .clip(CircleShape)
                                .background(if (index == currentPage) PrimaryPink else Border)
                        )
                    }
                }

                AftermaButton(
                    text = if (currentPage == pages.size - 1) "Begin Your Journey" else "Next Steps",
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onOnboardingCompleted()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "onboarding_next"
                )
            }
        }
    }
}

// --- Login Screen ---
@Composable
fun LoginScreen(
    onLoginSuccess: (email: String, name: String) -> Unit,
    onNavigateToSignup: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Calm Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SoftPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = null,
                        tint = PrimaryPink,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome Back, Mama",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Slide into your therapeutic calm zone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                )
            }

            // Input Fields
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AftermaInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Enter your first name",
                    label = "Your First Name (Optional)",
                    leadingIcon = Icons.Filled.Person,
                    textTag = "login_name_input"
                )

                AftermaInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "yourname@domain.com",
                    label = "Supportive Contact Email",
                    leadingIcon = Icons.Filled.Email,
                    textTag = "login_email_input"
                )

                AftermaInput(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Your private password",
                    label = "Secure Password Key",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    textTag = "login_password_input"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Forgotten Key?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPink,
                        modifier = Modifier.clickable { /* Simulate recovery popup */ }
                    )
                }
            }

            // Buttons
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AftermaButton(
                    text = "Access Sanctuary",
                    onClick = {
                        val displayEmail = if (email.isBlank()) "mother@afterma.com" else email
                        val displayName = if (name.isBlank()) "Mama" else name
                        onLoginSuccess(displayEmail, displayName)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "login_button"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "New Here? ",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                    Text(
                        text = "Co-Create a Sanctuary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPink,
                        modifier = Modifier
                            .clickable { onNavigateToSignup() }
                            .testTag("navigate_signup_button")
                    )
                }
            }
        }
    }
}

// --- Signup Screen ---
@Composable
fun SignupScreen(
    onSignupSuccess: (email: String, name: String) -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var postpartumPeriod by remember { mutableStateOf("3 Weeks Postpartum") }
    var termsAccepted by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    val options = listOf(
        "Expecting Mama",
        "Recent Mother (Weeks 1-4)",
        "3 Weeks Postpartum",
        "6 Weeks Postpartum",
        "3+ Months Recovery"
    )
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SoftPink),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Eco,
                        contentDescription = null,
                        tint = PrimaryPink,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Begin Postpartum Care",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tailoring nutrition, therapeutic recovery protocols, and calm mind guidelines",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AftermaInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Your comforting maternal name",
                    label = "Your Sanctuary Profile Name",
                    leadingIcon = Icons.Filled.Person,
                    textTag = "signup_name_input"
                )

                AftermaInput(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "mama@domain.com",
                    label = "Your Private Email",
                    leadingIcon = Icons.Filled.Email,
                    textTag = "signup_email_input"
                )

                AftermaInput(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "At least 6 characters",
                    label = "Create Lock Secret",
                    leadingIcon = Icons.Filled.Lock,
                    isPassword = true,
                    textTag = "signup_password_input"
                )

                // Maternal stage selector dropdown
                Column {
                    Text(
                        text = "Current Healing Stage",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryText,
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                    )
                    Box {
                        OutlinedButton(
                            onClick = { expanded = true },
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Border),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("dropdown_healing_stage")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = postpartumPeriod, color = PrimaryText, fontSize = 14.sp)
                                Icon(
                                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = PrimaryPink
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontSize = 14.sp, color = PrimaryText) },
                                    onClick = {
                                        postpartumPeriod = option
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Guidelines agreement
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Checkbox(
                        checked = termsAccepted,
                        onCheckedChange = { termsAccepted = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryPink)
                    )
                    Text(
                        text = "I consent to receiving soothing healthcare guides & support.",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AftermaButton(
                    text = "Co-Create My App",
                    onClick = {
                        val displayEmail = if (email.isBlank()) "comfort@afterma.com" else email
                        val displayName = if (name.isBlank()) "Mama" else name
                        onSignupSuccess(displayEmail, displayName)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "signup_button"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Already part of Afterma? ",
                        fontSize = 14.sp,
                        color = SecondaryText
                    )
                    Text(
                        text = "Unlock Sanctuary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryPink,
                        modifier = Modifier
                            .clickable { onNavigateToLogin() }
                            .testTag("navigate_login_button")
                    )
                }
            }
        }
    }
}

// Data modeling
data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    val tint: Color
)
