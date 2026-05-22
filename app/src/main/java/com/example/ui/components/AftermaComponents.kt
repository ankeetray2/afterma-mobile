package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// --- 1. AftermaButton (Pill-shaped, gorgeous pink gradient or flat style) ---
@Composable
fun AftermaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    useGradient: Boolean = true,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    testTag: String = "afterma_button"
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .height(56.dp)
            .shadow(
                elevation = if (enabled) 4.dp else 0.dp,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .then(
                if (useGradient && enabled) {
                    Modifier.background(AftermaGradients.PrimaryHero)
                } else {
                    Modifier.background(if (enabled) MaterialTheme.colorScheme.primary else Color(0xFFE5E7EB))
                }
            )
            .clickable(enabled = enabled, onClick = onClick)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                color = if (enabled) Color.White else Color(0xFF9CA3AF),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- 2. AftermaCard (Spacious, rounded container, subtle soothing border) ---
@Composable
fun AftermaCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = Border,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(32.dp))
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = PrimaryPink.copy(alpha = 0.3f),
                spotColor = PrimaryPink.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

// --- 3. AftermaInput (Beautifully rounded text fields with safe focus cues) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AftermaInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    textTag: String = "afterma_input"
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = SecondaryText,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(textTag),
            placeholder = { Text(placeholder, color = MutedText, fontSize = 14.sp) },
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = PrimaryPink) }
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = SecondaryText
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default,
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryPink,
                unfocusedBorderColor = Border,
                cursorColor = PrimaryPink
            )
        )
    }
}

// --- 4. GradientHeroCard (The glowing pink banner which is AFTERMA's hallmark) ---
@Composable
fun GradientHeroCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    illustration: @Composable (BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .background(brush = AftermaGradients.PrimaryHero)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
            if (illustration != null) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(start = 12.dp),
                    content = illustration
                )
            }
        }
    }
}

// --- 5. MoodCard (Interactive postpartum mood logger with emoji and label) ---
@Composable
fun MoodCard(
    score: Int,
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(if (isSelected) 1.08f else 1.0f, label = "mood_scale")
    val borderCol = if (isSelected) PrimaryPink else Border
    val shadowElev = if (isSelected) 4.dp else 1.dp
    val bgCol = if (isSelected) SoftPink else CardSurface

    Column(
        modifier = modifier
            .scale(scale)
            .shadow(elevation = shadowElev, shape = RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(bgCol)
            .border(2.dp, borderCol, RoundedCornerShape(28.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 10.dp)
            .testTag("mood_card_$score"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = emoji,
            fontSize = 32.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) DeepPink else SecondaryText,
            textAlign = TextAlign.Center
        )
    }
}

// --- 6. WellnessStatCard (Grid stat trackers for progress / details) ---
@Composable
fun WellnessStatCard(
    title: String,
    value: String,
    unit: String = "",
    icon: ImageVector,
    iconColor: Color,
    containerColor: Color,
    modifier: Modifier = Modifier,
    subText: String? = null
) {
    Box(
        modifier = modifier
            .shadow(elevation = 1.dp, shape = RoundedCornerShape(28.dp))
            .clip(RoundedCornerShape(28.dp))
            .background(Color.White)
            .border(1.dp, Border, RoundedCornerShape(28.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(containerColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold
                )
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = unit,
                        fontSize = 12.sp,
                        color = SecondaryText,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
            if (subText != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subText,
                    fontSize = 11.sp,
                    color = MutedText
                )
            }
        }
    }
}

// --- 7. TherapyCard (Premium maternal therapist listings for care connect) ---
@Composable
fun TherapyCard(
    name: String,
    title: String,
    specialty: String,
    time: String,
    isBooked: Boolean,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AftermaCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // Profile Circular Icon representing Maternal care Professional
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(TherapyPurple),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Face,
                    contentDescription = null,
                    tint = DeepPink,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryPink,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Specialty: $specialty",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = time, fontSize = 12.sp, color = SecondaryText)
                    }

                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBooked) HealingGreen else PrimaryPink,
                            contentColor = if (isBooked) DeepPink else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("book_button_${name.replace(" ", "_")}")
                    ) {
                        if (isBooked) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Booked", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text(text = "Connect", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- 8. SafeChip (Pastel category chip labels) ---
@Composable
fun SafeChip(
    text: String,
    isSelected: Boolean,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    activeColor: Color = SoftPink,
    activeTextColor: Color = DeepPink
) {
    val bgCol = if (isSelected) activeColor else Color.White
    val textCol = if (isSelected) activeTextColor else SecondaryText
    val borderCol = if (isSelected) activeTextColor.copy(alpha = 0.3f) else Border

    Box(
        modifier = modifier
            .shadow(elevation = if (isSelected) 1.dp else 0.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(bgCol)
            .border(width = 1.dp, color = borderCol, shape = CircleShape)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textCol
        )
    }
}

// --- 9. SoftTopBar (Elegant, lightweight navigation banner) ---
@Composable
fun SoftTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        tint = PrimaryText
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                // Calm mini emotional anchor icon
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
                Spacer(modifier = Modifier.width(12.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryText,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (actions != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                actions()
            }
        } else {
            // Calm profile indicator to increase healthcare feeling
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CalmBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "User profile anchor",
                    tint = DeepPink,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

// --- 10. FloatingBottomNav (Beautiful custom floating navbar that respects safety cutouts) ---
@Composable
fun FloatingBottomNav(
    selectedRoute: String,
    onRouteSelected: (String) -> Unit,
    items: List<NavigationItem>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding(), // Ensures gesture navigation space is guaranteed on all devices
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = selectedRoute == item.route
                val backgroundAlpha by animateFloatAsState(if (isSelected) 1f else 0f, label = "bg_alpha")
                val tintColor = if (isSelected) DeepPink else SecondaryText

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(SoftPink.copy(alpha = backgroundAlpha))
                        .clickable(
                            onClick = { onRouteSelected(item.route) },
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("nav_item_${item.route}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            tint = tintColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = tintColor
                        )
                    }
                }
            }
        }
    }
}

// Helper struct for bottom bar items
data class NavigationItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
