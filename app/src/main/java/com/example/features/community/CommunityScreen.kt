package com.example.features.community

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CommunityPost
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CommunityScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val postsList by viewModel.communityPosts.collectAsState()

    var activeTagFilter by remember { mutableStateOf("All") }
    val tags = listOf("All", "Inspiration", "Recipes & Feeding", "Physical Healing")

    var newPostText by remember { mutableStateOf("") }
    var selectedPostingTag by remember { mutableStateOf("Inspiration") }
    var isWritingPost by remember { mutableStateOf(false) }

    val filteredPosts = remember(postsList, activeTagFilter) {
        if (activeTagFilter == "All") postsList
        else postsList.filter { it.tag == activeTagFilter }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        SoftTopBar(
            title = "Mothers Sanctuary",
            actions = {
                // New Post action toggler
                IconButton(
                    onClick = { isWritingPost = !isWritingPost },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SoftPink)
                ) {
                    Icon(
                        imageVector = if (isWritingPost) Icons.Filled.Close else Icons.Filled.PostAdd,
                        contentDescription = "New Post Toggler",
                        tint = PrimaryPink,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        if (isWritingPost) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Share Maternal Comfort",
                    style = MaterialTheme.typography.headlineMedium,
                    color = PrimaryText,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Your prompt will be posted anonymously or with your first name securely inside the mothers collective.",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Select classification category
                Text(
                    text = "Select Conversation Tag",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SecondaryText,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    items(tags.filter { it != "All" }) { label ->
                        SafeChip(
                            text = label,
                            isSelected = selectedPostingTag == label,
                            onClick = { selectedPostingTag = label }
                        )
                    }
                }

                // Post inputbox
                AftermaInput(
                    value = newPostText,
                    onValueChange = { newPostText = it },
                    placeholder = "Describe feelings, milestone achievements, or comfort guidance...",
                    label = "Your calming supportive thought",
                    leadingIcon = Icons.Filled.DriveFileRenameOutline,
                    textTag = "maternal_community_posting",
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                AftermaButton(
                    text = "Distribute Support Post",
                    onClick = {
                        if (newPostText.isNotBlank()) {
                            viewModel.shareCommunityPost(newPostText, selectedPostingTag)
                            newPostText = ""
                            isWritingPost = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "submit_community_post"
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                        Text(
                            text = "Comfort Support Circle",
                            style = MaterialTheme.typography.titleLarge,
                            color = PrimaryText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "A safe, closed communal harbor designed purely to trade maternal comfort, postpartum wins, and nourishment tips.",
                            fontSize = 12.sp,
                            color = SecondaryText,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Tags filter scroll row
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(tags) { t ->
                                SafeChip(
                                    text = t,
                                    isSelected = t == activeTagFilter,
                                    onClick = { activeTagFilter = t }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(filteredPosts) { post ->
                    CommunityFeedItem(
                        post = post,
                        onLikeClick = { viewModel.likeCommunityPost(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CommunityFeedItem(
    post: CommunityPost,
    onLikeClick: () -> Unit
) {
    AftermaCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SoftPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Filled.Spa, contentDescription = null, tint = PrimaryPink, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = post.authorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                        Text(text = post.weeksPostpartum, fontSize = 10.sp, color = MutedText)
                    }
                }

                // Categorised active pill tags
                Text(
                    text = post.tag,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = DeepPink,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(SoftPink)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                fontSize = 14.sp,
                color = PrimaryText,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onLikeClick() }
                        .testTag("like_post_${post.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Like Post",
                        tint = PrimaryPink,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likesCount} Encouragements",
                        fontSize = 12.sp,
                        color = SecondaryText,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Comment,
                        contentDescription = "Comments Count",
                        tint = MutedText,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentsCount} Embraces",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}
